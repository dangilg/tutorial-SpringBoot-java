package com.ccsw.tutorial.loan.model.available;

import java.time.LocalDate;

public class Interval {

    private LocalDate start;
    private LocalDate end;

    public Interval() {}

    public Interval(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

}
