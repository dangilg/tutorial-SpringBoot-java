package com.ccsw.tutorial.loan.model.available;

import java.time.LocalDate;

/**
 * @author dgilguti
 * Clase que especifica el cuerpo del mensaje para una petición de disponibilidad
 */
public class AvailableRequestDto {
    private Long loanId;
    private Long clientId;
    private Long gameId;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     *
     * @return loanId
     */
    public Long getLoanId(){
        return loanId;
    }

    /**
     *
     * @param loanId nuevo valor de {@link #getLoanId()}
     */
    public void setLoanId(Long loanId){
        this.loanId = loanId;
    }

    /**
     *
     * @return clientId
     */
    public Long getClientId() {
        return clientId;
    }

    /**
     *
     * @param clientId nuevo valor de {@link #getClientId()}
     */
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    /**
     *
     * @return gameId
     */
    public Long getGameId() {
        return gameId;
    }

    /**
     *
     * @param gameId nuevo valor de {@link #getGameId()}
     */
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    /**
     *
     * @return {@link LocalDate} fecha de inicio
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     *
     * @param startDate nuevo valor para {@link #getStartDate()}
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     *
     * @return {@link LocalDate} fecha de fin
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     *
     * @param endDate nuevo valor de {@link #getEndDate()}
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

}
