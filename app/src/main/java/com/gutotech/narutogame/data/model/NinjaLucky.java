package com.gutotech.narutogame.data.model;

import java.util.ArrayList;
import java.util.List;

public class NinjaLucky {
    private int lastDayPlayed;
    private List<Boolean> daysOfWeek;

    public NinjaLucky() {
    }

    public void deselectAllDaysPlayed() {
        lastDayPlayed = -1;
        daysOfWeek = new ArrayList<>();

        for (int day = 0; day < 7; day++) {
            daysOfWeek.add(day, false);
        }
    }

    public void selectDayAsPlayed(int day) {
        daysOfWeek.set(day - 1, true);
    }

    public boolean playedAllDays() {
        for (boolean dayPlayed : daysOfWeek) {
            if (!dayPlayed) {
                return false;
            }
        }

        return true;
    }

    public boolean played(int day) {
        return getLastDayPlayed() == day;
    }

    public int getLastDayPlayed() {
        return lastDayPlayed;
    }

    public void setLastDayPlayed(int lastDayPlayed) {
        this.lastDayPlayed = lastDayPlayed;
    }

    public List<Boolean> getDaysOfWeek() {
        return daysOfWeek;
    }
}
