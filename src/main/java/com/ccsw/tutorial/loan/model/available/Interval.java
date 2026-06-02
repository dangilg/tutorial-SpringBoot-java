package com.ccsw.tutorial.loan.model.available;

import java.time.LocalDate;

/**
 * @author dgilguti
 *
 * Clase que define un Intervalo de fechas, con su fecha de inicio y su fecha de fin
 */
public class Interval {

    private LocalDate start;
    private LocalDate end;

    public Interval() {}

    public Interval(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    /**
     *
     * @return {@link LocalDate} fecha de inicio
     */
    public LocalDate getStart() {
        return start;
    }

    /**
     *
     * @param start nuevo valor para {@link #getStart()}
     */
    public void setStart(LocalDate start) {
        this.start = start;
    }

    /**
     *
     * @return {@link LocalDate} fecha de fin
     */
    public LocalDate getEnd() {
        return end;
    }

    /**
     *
     * @param end nuevo valor para {@link #getEnd()}
     */
    public void setEnd(LocalDate end) {
        this.end = end;
    }

}
