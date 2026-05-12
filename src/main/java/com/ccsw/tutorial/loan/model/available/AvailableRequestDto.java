package com.ccsw.tutorial.loan.model.available;

import java.time.LocalDate;

public class AvailableRequestDto {
    private Long loanId;
    private Long clientId;
    private Long gameId;
    private LocalDate startDate;
    private LocalDate endDate;

    public Long getLoanId(){
        return loanId;
    }
    public void setLoanId(Long loanId){
        this.loanId = loanId;
    }
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

}
