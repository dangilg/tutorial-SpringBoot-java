package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.client.ClientRepository;
import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.common.criteria.GenericSpecification;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.exceptions.NotValidLoanException;
import com.ccsw.tutorial.game.GameRepository;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.loan.model.available.AvailableRequestDto;
import com.ccsw.tutorial.loan.model.available.AvailableResponseDto;
import com.ccsw.tutorial.loan.model.available.Interval;
import com.ccsw.tutorial.loan.model.filter.FilterDataModel;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.filter.PageFilterDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class LoanServiceImp implements LoanService{
    //cantidad máxima de dias seleccionables desde hoy
    private final int MAX_LIMIT_DATES_SELECTED = 60;

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    ClientRepository clientRepository;


    @Override
    public long getCount(){
        return this.loanRepository.count();
    }
    @Override
    public Page<Loan> findPageFiltered(PageFilterDto dto) {
        FilterDataModel filters = dto.getFilters();
        PageableRequest pageable = dto.getPageable();

        GenericSpecification<Loan> clientSpec = new GenericSpecification<Loan>(new SearchCriteria("client.id",":",filters.getClientId()));
        GenericSpecification<Loan> gameSpec = new GenericSpecification<Loan>(new SearchCriteria("game.id",":",filters.getGameId()));

        Date referenceDate = null;
        //todo -> revisar excepciones posibles en un TryCatch
        if(filters.getDate()!=null){
             referenceDate= Date.valueOf(filters.getDate());
        }


        Specification<Loan> spec = clientSpec.and(gameSpec).and(
                DateBetweenLoanSpecification.dateBetween(referenceDate)
        );

        return this.loanRepository.findAll(spec, pageable.getPageable());
    }

    @Override
    public AvailableResponseDto calculateAvailability(AvailableRequestDto dto){
        AvailableResponseDto response = new AvailableResponseDto();
        LocalDate start = dto.getStartDate();
        LocalDate end = dto.getEndDate();
        Long loanId = dto.getLoanId();
        List<Game> games = resolveGames(loanId,start,end);
        List<Client> clients = resolveClients(loanId,start,end);




        List<Interval> startIntervals = resolveStartIntervals(dto);
        List<Interval> endIntervals = resolveEndIntervals(dto);

        response.setClients(clients);
        response.setGames(games);
        response.setValidStartDates(startIntervals);
        response.setValidEndDates(endIntervals);

        return response;


    }


    @Override
    public void save(Long id, AvailableRequestDto dto){
        Loan loan;
        if(id==null){
            loan = new Loan();
        }
        else{
            loan = loanRepository.findById(id).orElse(null);
        }
        if(loan ==null){
            throw new NotValidLoanException();
        }

        if(dto.getStartDate() ==null ||
            dto.getEndDate() == null ||
            dto.getClientId() == null ||
            dto.getGameId() == null){
            throw new NotValidLoanException();
        }

        Client client = clientRepository.findById(dto.getClientId()).orElse(null);

        Game game = gameRepository.findById(dto.getGameId()).orElse(null);

        if(client==null||game == null){
            throw new NotValidLoanException();
        }
        LocalDate start = dto.getStartDate();
        LocalDate end = dto.getEndDate();

        if(end.isBefore(start)){
            throw new NotValidLoanException();
        }
        if(start.plusDays(13).isBefore(end)){
            throw new NotValidLoanException();
        }

        if(!isEndDateValidForClient(dto,start,end)){
            throw new NotValidLoanException();
        }
        if(!isEndDateValidForGame(dto,start,end)){
            throw new NotValidLoanException();
        }


        loan.setClient(client);
        loan.setGame(game);
        loan.setStartDate(start);
        loan.setEndDate(end);
        loanRepository.save(loan);
    }



    private List<LocalDate> generateCandidateDates(){
        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();

        //todo rango de 60  dias variable
        for(int i =0;i<MAX_LIMIT_DATES_SELECTED; i++){
            dates.add(today.plusDays(i));
        }

        return  dates;
    }


    private boolean isDateValidForClient(AvailableRequestDto dto, LocalDate date){
        Long clientId = dto.getClientId();

        if(clientId==null){
            return true;
        }
        long count = loanRepository.countOverlappingLoansByClient(
                dto.getLoanId(),
                clientId,
                date,
                date
        );

        return count<2;
    }

    private boolean isDateValidForGame(AvailableRequestDto dto, LocalDate date){
        Long gameId = dto.getGameId();
        if(gameId==null){
            return true;
        }
        List<Loan> loans = loanRepository.findOverlappingLoansByGame(
                dto.getLoanId(),
                gameId,
                date,
                date
        );
        return loans.isEmpty();
    }

    private List<LocalDate> calculateValidStartDates(AvailableRequestDto dto){
        List<LocalDate> candidates = generateCandidateDates();
        return candidates.stream()
                .filter(date-> isDateValidForClient(dto,date))
                .filter(date -> isDateValidForGame(dto, date))
                .toList();
    }

    private List<Interval> buildIntervals(List<LocalDate> dates){
        List<Interval> intervals = new ArrayList<>();
        if(dates.isEmpty()) return intervals;

        LocalDate start = dates.get(0);
        LocalDate prev = start;

        for( int i=1;i<dates.size();i++){
            if(!dates.get(i).equals(prev.plusDays(1))){
                intervals.add(new Interval(start,prev));
                start = dates.get(i);
            }

            prev = dates.get(i);
        }

        intervals.add(new Interval(start,prev));

        return intervals;
    }

    private List<LocalDate> generateCandidateEndDates(LocalDate start){
        List<LocalDate> dates = new ArrayList<>();

        for(int i=0;i<=13;i++){
            dates.add(start.plusDays(i));
        }
        return dates;
    }

    private boolean isEndDateValidForGame(AvailableRequestDto dto, LocalDate start, LocalDate end){
        Long gameId = dto.getGameId();
        if(gameId ==null){
            return true;
        }

        List<Loan> overlaps = loanRepository.findOverlappingLoansByGame(
                dto.getLoanId(),
                gameId,
                start,
                end
        );
        return overlaps.isEmpty();
    }

    private boolean isEndDateValidForClient(AvailableRequestDto dto, LocalDate start, LocalDate end){
        Long clientId = dto.getClientId();
        if(clientId==null){
            return  true;
        }

        LocalDate current = start;
        while(!current.isAfter(end)){
            long count = loanRepository.countOverlappingLoansByClient(
                    dto.getLoanId(),
                    clientId,
                    current,
                    current
            );
            if(count>=2){
                return false;
            }
            current =current.plusDays(1);
        }
        return true;
    }

    private List<LocalDate> calculateValidEndDates(AvailableRequestDto dto){
        if(dto.getStartDate()==null){
            return List.of();
        }

        LocalDate start = dto.getStartDate();

        List<LocalDate> candidates = generateCandidateEndDates(start);

        return candidates.stream()
                .filter(end-> isEndDateValidForGame(dto, start,end))
                .filter(end-> isEndDateValidForClient(dto, start, end))
                .toList();
    }



    private List<Client> resolveClients(Long loanId, LocalDate start, LocalDate end){

        if(start==null && end ==null){
            return (List<Client>) clientRepository.findAll();
        }

        if(start != null && end == null){
            return clientRepository.findAvailableClients(loanId, start, start);
        }
        if(start!=null){
            return clientRepository.findAvailableClients(loanId, start,end);
        }
        throw  new IllegalArgumentException("invalidStateClient");
    }

    private List<Game> resolveGames(Long loanId, LocalDate start, LocalDate end){
        if(start==null && end == null){
            return (List<Game>) gameRepository.findAll();
        }
        if(start!=null && end == null){
            return gameRepository.findAvailableGames(loanId,start,start);
        }
        if(start != null){
            List<Game> ret = gameRepository.findAvailableGames(loanId,start,end);
            return ret;
        }
        throw new IllegalArgumentException("invalidStateGame");
    }

    private List<Interval> resolveStartIntervals(AvailableRequestDto dto){
        List<LocalDate> validDates = calculateValidStartDates(dto);
        return buildIntervals(validDates);
    }

    private List<Interval> resolveEndIntervals(AvailableRequestDto dto){
        if(dto.getStartDate()==null){
            return null;
        }
        List<LocalDate> validDates =calculateValidEndDates(dto);
        return buildIntervals(validDates);

    }
}
