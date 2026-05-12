package com.ccsw.tutorial.loan.model.available;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.game.model.Game;

import java.util.List;

public class AvailableResponseDto {
    private List<Client> clients;
    private List<Game> games;
    private List<Interval> validStartDates;
    private List<Interval> validEndDates;

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public List<Interval> getValidStartDates() {
        return validStartDates;
    }

    public void setValidStartDates(List<Interval> validStartDates) {
        this.validStartDates = validStartDates;
    }

    public List<Interval> getValidEndDates() {
        return validEndDates;
    }

    public void setValidEndDates(List<Interval> validEndDates) {
        this.validEndDates = validEndDates;
    }

}
