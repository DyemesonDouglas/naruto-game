package com.gutotech.narutogame.data.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.gutotech.narutogame.utils.DateCustom;

import java.util.ArrayList;
import java.util.List;

public class Battle extends BaseObservable {
    public enum Status {
        CONTINUE, PLAYER1_WON, PLAYER2_WON, DRAWN
    }

    private Status status;

    private String id;

    private Character player1;
    private Character player2;

    private int currentPlayer;

    private List<BattleLog> battleLogs;

    private List<Jutsu> player1BuffsDebuffsStatus;
    private List<Jutsu> player2BuffsDebuffsStatus;

    private long attackStart;

    private Jutsu oppJutsu;

    private int playerCount;

    public Battle() {
    }

    public Battle(Character player1, Character player2) {
        this.player1 = player1;
        this.player2 = player2;
        status = Status.CONTINUE;
        currentPlayer = 1;
        playerCount = 2;
        attackStart = DateCustom.getTimeInMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Bindable
    public Character getPlayer1() {
        return player1;
    }

    public void setPlayer1(Character player1) {
        this.player1 = player1;
        notifyPropertyChanged(BR.player1);
    }

    @Bindable
    public Character getPlayer2() {
        return player2;
    }

    public void setPlayer2(Character player2) {
        this.player2 = player2;
        notifyPropertyChanged(BR.player2);
    }

    @Bindable
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
        notifyPropertyChanged(BR.currentPlayer);
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public List<BattleLog> getBattleLogs() {
        if (battleLogs == null) {
            battleLogs = new ArrayList<>();
        }
        return battleLogs;
    }

    public void setBattleLogs(List<BattleLog> battleLogs) {
        this.battleLogs = battleLogs;
    }

    public List<Jutsu> getPlayer1BuffsDebuffsStatus() {
        if (player1BuffsDebuffsStatus == null) {
            player1BuffsDebuffsStatus = new ArrayList<>();
        }

        return player1BuffsDebuffsStatus;
    }

    public void setPlayer1BuffsDebuffsStatus(List<Jutsu> player1BuffsDebuffsStatus) {
        this.player1BuffsDebuffsStatus = player1BuffsDebuffsStatus;
    }

    public List<Jutsu> getPlayer2BuffsDebuffsStatus() {
        if (player2BuffsDebuffsStatus == null) {
            player2BuffsDebuffsStatus = new ArrayList<>();
        }

        return player2BuffsDebuffsStatus;
    }

    public void setPlayer2BuffsDebuffsStatus(List<Jutsu> player2BuffsDebuffsStatus) {
        this.player2BuffsDebuffsStatus = player2BuffsDebuffsStatus;
    }

    public long getAttackStart() {
        return attackStart;
    }

    public void setAttackStart(long attackStart) {
        this.attackStart = attackStart;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Jutsu getOppJutsu() {
        return oppJutsu;
    }

    public void setOppJutsu(Jutsu oppJutsu) {
        this.oppJutsu = oppJutsu;
    }
}
