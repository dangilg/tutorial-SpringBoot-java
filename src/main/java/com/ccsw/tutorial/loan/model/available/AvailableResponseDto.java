package com.ccsw.tutorial.loan.model.available;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.game.model.Game;

import java.util.List;

/**
 * @author dgilguti
 *
 * Clase que espeficia el cuerpo del mensaje de respuesta a una petición de disponibilidad
 */
public class AvailableResponseDto {
    private List<Client> clients;
    private List<Game> games;
    private List<Interval> validStartDates;
    private List<Interval> validEndDates;

    /**
     *
     * @return {@link List} de {@link Client} con los {@link Client} disponibles
     */
    public List<Client> getClients() {
        return clients;
    }

    /**
     *
     * @param clients nuevo valor para {@link #getClients()}
     */
    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    /**
     *
     * @return {@link List} de {@link Game} con los {@link Game} disponibles
     */
    public List<Game> getGames() {
        return games;
    }

    /**
     *
     * @param games nuevo valor para {@link #getGames()}
     */
    public void setGames(List<Game> games) {
        this.games = games;
    }

    /**
     *
     * @return {@link List} de {@link Interval} con los {@link Interval} de inicio válidos
     */
    public List<Interval> getValidStartDates() {
        return validStartDates;
    }

    /**
     *
     * @param validStartDates nuevo valor para {@link #getValidStartDates()}
     */
    public void setValidStartDates(List<Interval> validStartDates) {
        this.validStartDates = validStartDates;
    }

    /**
     *
     * @return {@link List} de {@link Interval} con los {@link Interval} de fin válidos
     */
    public List<Interval> getValidEndDates() {
        return validEndDates;
    }

    /**
     *
     * @param validEndDates nuevo valor para {@link #getValidEndDates()}
     */
    public void setValidEndDates(List<Interval> validEndDates) {
        this.validEndDates = validEndDates;
    }

}
