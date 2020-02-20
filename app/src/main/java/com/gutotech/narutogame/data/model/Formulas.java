package com.gutotech.narutogame.data.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Formulas extends BaseObservable implements Serializable {
    private int health;
    private int currentHealth;
    private int chakra;
    private int currentChakra;
    private int stamina;
    private int currentStamina;
    private int atkTaiBuki;
    private int atkNinGen;
    private int defTaiBuki;
    private int defNinGen;
    private int accuracy;
    private int concentracao;
    private int percepcao;
    private int conviccao;
    private double esquiva;
    private int determinacao;
    private int ninjaPower;

    public Formulas() {
    }

    public void full() {
        setCurrentHealth(getHealth());
        setCurrentChakra(getChakra());
        setCurrentStamina(getStamina());
    }

    public void subHeath(int value) {
        setCurrentHealth(getCurrentHealth() - value > 0 ? getCurrentHealth() - value : 0);
    }

    public void subChakra(int value) {
        setCurrentChakra(getCurrentChakra() - value > 0 ? getCurrentChakra() - value : 0);
    }

    public void subStamina(int value) {
        setCurrentStamina(getCurrentStamina() - value > 0 ? getCurrentStamina() - value : 0);
    }

    public void addHeath(int value) {
        int newHealth = getCurrentHealth() + value;
        setCurrentHealth(newHealth >= getHealth() ? getHealth() : newHealth);
    }

    public void addChakra(int value) {
        int newChakra = getCurrentChakra() + value;
        setCurrentChakra(newChakra >= getChakra() ? getChakra() : newChakra);
    }

    public void addStamina(int value) {
        int newStamina = getCurrentStamina() + value;
        setCurrentStamina(newStamina >= getStamina() ? getStamina() : newStamina);
    }

    @Bindable
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        notifyPropertyChanged(BR.health);
    }

    @Bindable
    public int getChakra() {
        return chakra;
    }

    public void setChakra(int chakra) {
        this.chakra = chakra;
        notifyPropertyChanged(BR.chakra);
    }

    @Bindable
    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
        notifyPropertyChanged(BR.stamina);
    }

    @Bindable
    public int getAtkTaiBuki() {
        return atkTaiBuki;
    }

    @Bindable
    public void setAtkTaiBuki(int atkTaiBuki) {
        this.atkTaiBuki = atkTaiBuki;
        notifyPropertyChanged(BR.atkTaiBuki);
    }

    @Bindable
    public int getAtkNinGen() {
        return atkNinGen;
    }

    public void setAtkNinGen(int atkNinGen) {
        this.atkNinGen = atkNinGen;
        notifyPropertyChanged(BR.atkNinGen);
    }

    @Bindable
    public int getDefTaiBuki() {
        return defTaiBuki;
    }

    public void setDefTaiBuki(int defTaiBuki) {
        this.defTaiBuki = defTaiBuki;
        notifyPropertyChanged(BR.defTaiBuki);
    }

    @Bindable
    public int getDefNinGen() {
        return defNinGen;
    }

    public void setDefNinGen(int defNinGen) {
        this.defNinGen = defNinGen;
        notifyPropertyChanged(BR.defNinGen);
    }

    @Bindable
    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
        notifyPropertyChanged(BR.accuracy);
    }

    public int getConcentracao() {
        return concentracao;
    }

    public void setConcentracao(int concentracao) {
        this.concentracao = concentracao;
    }

    public int getPercepcao() {
        return percepcao;
    }

    public void setPercepcao(int percepcao) {
        this.percepcao = percepcao;
    }

    public int getConviccao() {
        return conviccao;
    }

    public void setConviccao(int conviccao) {
        this.conviccao = conviccao;
    }

    public double getEsquiva() {
        return esquiva;
    }

    public void setEsquiva(double esquiva) {
        this.esquiva = esquiva;
    }

    public int getDeterminacao() {
        return determinacao;
    }

    public void setDeterminacao(int determinacao) {
        this.determinacao = determinacao;
    }

    @Bindable
    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
        notifyPropertyChanged(BR.currentHealth);
    }

    @Bindable
    public int getCurrentChakra() {
        return currentChakra;
    }

    public void setCurrentChakra(int currentChakra) {
        this.currentChakra = currentChakra;
        notifyPropertyChanged(BR.currentChakra);
    }

    @Bindable
    public int getCurrentStamina() {
        return currentStamina;
    }

    public void setCurrentStamina(int currentStamina) {
        this.currentStamina = currentStamina;
        notifyPropertyChanged(BR.currentStamina);
    }

    @Bindable
    public int getNinjaPower() {
        return ninjaPower;
    }

    public void setNinjaPower(int ninjaPower) {
        this.ninjaPower = ninjaPower;
        notifyPropertyChanged(BR.ninjaPower);
    }

    public List<Integer> asList() {
        List<Integer> formulas = new ArrayList<>();

        formulas.add(Formula.HEALTH.id, currentHealth);
        formulas.add(Formula.CHAKRA.id, currentChakra);
        formulas.add(Formula.STAMINA.id, currentStamina);
        formulas.add(Formula.ATK_TAI.id, atkTaiBuki);
        formulas.add(Formula.ATK_NIN.id, atkNinGen);
        formulas.add(Formula.DEF_TAI.id, defTaiBuki);
        formulas.add(Formula.DEF_NIN.id, defNinGen);
        formulas.add(Formula.ACC.id, accuracy);

        return formulas;
    }


}
