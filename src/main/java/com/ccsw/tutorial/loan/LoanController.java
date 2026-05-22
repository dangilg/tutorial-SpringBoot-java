package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.available.AvailableRequestDto;
import com.ccsw.tutorial.loan.model.available.AvailableResponseDto;
import com.ccsw.tutorial.loan.model.filter.PageFilterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Tag(name = "Loan", description = "API of Loans")
@RequestMapping(value = "/loan")
@RestController
@CrossOrigin(origins = "*")
public class LoanController {

    @Autowired
    ModelMapper mapper;

    @Autowired
    LoanService loanService;

    @Operation(summary = "Find", description = "Method that returns a page of filtered Loans")
    @RequestMapping(path="", method = RequestMethod.POST)
    public Page<LoanDto> find(@RequestBody PageFilterDto dto){


        Page<Loan> page = loanService.findPageFiltered(dto);


        Page<LoanDto> ret =new PageImpl<>(page.getContent().stream().map(e->mapper.map(e,LoanDto.class)).collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
        return  ret;
    }

    @Operation(summary = "Available",description = "Method that returns the availables clients, games and dates due to the data in the dto")
    @RequestMapping(path = "/available",method = RequestMethod.POST)
    public AvailableResponseDto available(@RequestBody AvailableRequestDto dto){

        return  loanService.calculateAvailability(dto);
    }


    @Operation(summary = "save", description = "Mathod that save or updates a loan")
    @RequestMapping(path = {"/save","/save/{id}"},method = RequestMethod.PUT)
    public void save(@PathVariable (name="id", required = false) Long id, @RequestBody AvailableRequestDto dto){

        loanService.save(id,dto);
    }

    @Operation(summary = "lastId", description = "Method that returns the lastId in the system")
    @RequestMapping(path = "/lastId",method = RequestMethod.GET)
    public long count(){
        return loanService.getLastId();
    }

    @Operation(summary = "delete", description = "Method that deletes a Loan")
    @RequestMapping(path = "/{id}",method = RequestMethod.DELETE)
    public void delete(@PathVariable (name="id",required = true)Long id){
        loanService.delete(id);
    }
}
