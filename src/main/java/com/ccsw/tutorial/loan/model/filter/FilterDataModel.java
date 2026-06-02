package com.ccsw.tutorial.loan.model.filter;

/**
 * @author dgilguti
 *
 * Clase que espeficia los filtros para obtener las listas de {@link com.ccsw.tutorial.loan.model.Loan}
 */
public class FilterDataModel {
    private Long clientId;
    private Long gameId;
    private String date;

    /**
     *
     * @return PK del {@link com.ccsw.tutorial.client.model.Client}
     */
    public Long getClientId() {
        return clientId;
    }

    /**
     *
     * @param clientId nuevo valor de {@link #getClientId}
     */
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    /**
     *
     * @return PK del {@link com.ccsw.tutorial.game.model.Game}
     */
    public Long getGameId() {
        return gameId;
    }

    /**
     *
     * @param gameId nuevo valor de {@link #getGameId}
     */
    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    /**
     *
     * @return fecha
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date nuevo valor para {@link #getDate}
     */
    public void setDate(String date) {
        this.date = date;
    }
}
