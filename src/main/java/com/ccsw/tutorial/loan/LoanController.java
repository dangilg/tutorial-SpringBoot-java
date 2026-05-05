package com.ccsw.tutorial.loan;

import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.loan.model.FilterDataModel;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.PageFilterDto;
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
        System.out.println("estoy en find");

        Page<Loan> page = loanService.findPageFiltered(dto);



        return  new PageImpl<>(page.getContent().stream().map(e->mapper.map(e,LoanDto.class)).collect(Collectors.toList()), page.getPageable(), page.getTotalElements());
    }

}
