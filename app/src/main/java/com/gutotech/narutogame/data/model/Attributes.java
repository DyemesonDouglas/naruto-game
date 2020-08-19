package com.gutotech.narutogame.data.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
            Formulas
    Vida	                Fórmula da Vida	                (ENERGIA * 6)
	Chakra	                Fórmula do Chakra               (ENERGIA * 6) + (NIN * 14) + (GEN * 7)
	Stamina             	Fórmula da Stamina              (ENERGIA * 6) + (TAI * 7) + (BIK * 7)
	Ataque (Tai/Buk)	    +1 de Ataque (Tai/Buk)	        (FORÇA/ 2 )
	Ataque (Nin/Gen         +1 de Ataque (Nin /Gen)	        (INTELIGÊNCIA / 2 )
	Defesa (Nin/Gen)        +1 Defesa  (Nin/Gen)	        (RESISTENCIA / 2 )
	Defesa (Tai / Buk)      +1 Defesa (Tai /Buk)	        (RESISTENCIA/ 2 )
	Precisão	            +1 de Precisão	                (SELO * 1)
	Esquiva	                +1% de Esquiva	                (AGILIDADE / 6)
	Incremento de Absorção	1% de Incremento de Absorção	(Level / 4)
	Incremento de Crítico	1% de Incremento de Crítico	    (Level / 4)
 */
public class Attributes extends BaseObservable {
    private static final int TOTAL_ATTRIBUTES = 10;

    private int totalFreePoints;
    private int totalRedistributePoints;

    private Formulas formulas;
    private List<Integer> basePoints;
    private List<Integer> distributedPoints;
    private List<Integer> pointsEarned;

    public Attributes() {
    }

    public Attributes(Classe classe) {
        totalFreePoints = 5;
        formulas = new Formulas();

        basePoints = new ArrayList<>();
        distributedPoints = new ArrayList<>();
        pointsEarned = new ArrayList<>();

        for (int i = 0; i < TOTAL_ATTRIBUTES; i++) {
            basePoints.add(i, 0);
            distributedPoints.add(i, 0);
            pointsEarned.add(i, 0);
        }

        changeBasePoints(classe);
    }

    public void changeBasePoints(Classe classe) {
        switch (classe) {
            case TAI:
                basePoints.set(Attribute.TAI.ordinal(), 10);
                basePoints.set(Attribute.BUK.ordinal(), 1);
                basePoints.set(Attribute.NIN.ordinal(), 1);
                basePoints.set(Attribute.GEN.ordinal(), 1);
                basePoints.set(Attribute.INTE.ordinal(), 1);
                basePoints.set(Attribute.STR.ordinal(), 5);
                break;
            case BUK:
                basePoints.set(Attribute.TAI.ordinal(), 1);
                basePoints.set(Attribute.BUK.ordinal(), 10);
                basePoints.set(Attribute.NIN.ordinal(), 1);
                basePoints.set(Attribute.GEN.ordinal(), 1);
                basePoints.set(Attribute.INTE.ordinal(), 1);
                basePoints.set(Attribute.STR.ordinal(), 5);
                break;
            case NIN:
                basePoints.set(Attribute.TAI.ordinal(), 1);
                basePoints.set(Attribute.BUK.ordinal(), 1);
                basePoints.set(Attribute.NIN.ordinal(), 10);
                basePoints.set(Attribute.GEN.ordinal(), 1);
                basePoints.set(Attribute.INTE.ordinal(), 5);
                basePoints.set(Attribute.STR.ordinal(), 1);
                break;
            case GEN:
                basePoints.set(Attribute.TAI.ordinal(), 1);
                basePoints.set(Attribute.BUK.ordinal(), 1);
                basePoints.set(Attribute.NIN.ordinal(), 1);
                basePoints.set(Attribute.GEN.ordinal(), 10);
                basePoints.set(Attribute.INTE.ordinal(), 5);
                basePoints.set(Attribute.STR.ordinal(), 5);
                break;
        }
        basePoints.set(Attribute.SEAL.ordinal(), 3);
        basePoints.set(Attribute.AGI.ordinal(), 3);
        basePoints.set(Attribute.RES.ordinal(), 1);
        basePoints.set(Attribute.ENER.ordinal(), 10);
    }

    public void updateFormulas(Classe classe, int level) {
        final int additionalPerLevel1 = level * 18;
        final int additionalPerLevel2 = level * 9;

        formulas.setHealth(getEnergy() * 6 + additionalPerLevel1);

        if (classe == Classe.NIN) {
            formulas.setChakra(getEnergy() * 6 + getNinjutsu() * 14 + getGenjutsu() * 7 + additionalPerLevel1);
            formulas.setStamina(getEnergy() * 6 + getTaijutsu() * 7 + getBukijutsu() * 7 + additionalPerLevel2);
        } else if (classe == Classe.GEN) {
            formulas.setChakra(getEnergy() * 6 + getNinjutsu() * 7 + getGenjutsu() * 14 + additionalPerLevel1);
            formulas.setStamina(getEnergy() * 6 + getTaijutsu() * 7 + getBukijutsu() * 7 + additionalPerLevel2);
        } else if (classe == Classe.TAI) {
            formulas.setChakra(getEnergy() * 6 + getNinjutsu() * 7 + getGenjutsu() * 7 + additionalPerLevel2);
            formulas.setStamina(getEnergy() * 6 + getTaijutsu() * 14 + getBukijutsu() * 7 + additionalPerLevel1);
        } else {
            formulas.setChakra(getEnergy() * 6 + getNinjutsu() * 7 + getGenjutsu() * 7 + additionalPerLevel2);
            formulas.setStamina(getEnergy() * 6 + getTaijutsu() * 7 + getBukijutsu() * 14 + additionalPerLevel1);
        }

        formulas.setAtkTaiBuki((int) Math.ceil((double) getStrength() / 2));
        formulas.setAtkNinGen((int) Math.ceil((double) getIntelligence() / 2));

        formulas.setDefNinGen(getResistance() / 2);
        formulas.setDefTaiBuki(getResistance() / 2);

        formulas.setAccuracy(getSeal());

        formulas.setNinjaPower(getEnergy() * 200 +
                (getStrength() + getIntelligence() + getResistance()) * 150 +
                (getNinjutsu() + getGenjutsu() + getTaijutsu() + getBukijutsu()) * 200 +
                (getAgility() + getSeal()) * 100);
    }

    public void earn(int attributeId) {
        earn(attributeId, 1);
    }

    public void earn(int attributeId, int quantity) {
        int current = getPointsEarned().get(attributeId);
        getPointsEarned().set(attributeId, current + quantity);
    }

    public void train(int attributeId, int quantity) {
        int current = getDistributedPoints().get(attributeId);
        getDistributedPoints().set(attributeId, current + quantity);
        totalFreePoints -= quantity;
    }

    public void remove(int attributeId, int quantity) {
        int total = getDistributedPoints().get(attributeId);

        int newTotal = Math.max(total - quantity, 0);

        getDistributedPoints().set(attributeId, newTotal);

        if (total - quantity < 0) {
            quantity = quantity - (quantity - total);
        }

        totalFreePoints += quantity;
        totalRedistributePoints -= quantity;
    }

    public List<Integer> asList() {
        return Arrays.asList(
                getTaijutsu(),
                getBukijutsu(),
                getNinjutsu(),
                getGenjutsu(),
                getSeal(),
                getAgility(),
                getStrength(),
                getIntelligence(),
                getResistance(),
                getEnergy()
        );
    }

    @Exclude
    public int getTaijutsu() {
        return basePoints.get(Attribute.TAI.ordinal()) + distributedPoints.get(Attribute.TAI.ordinal())
                + pointsEarned.get(Attribute.TAI.ordinal());
    }

    @Exclude
    public int getBukijutsu() {
        return basePoints.get(Attribute.BUK.ordinal()) + distributedPoints.get(Attribute.BUK.ordinal())
                + pointsEarned.get(Attribute.BUK.ordinal());
    }

    @Exclude
    public int getNinjutsu() {
        return basePoints.get(Attribute.NIN.ordinal()) + distributedPoints.get(Attribute.NIN.ordinal())
                + pointsEarned.get(Attribute.NIN.ordinal());
    }

    @Exclude
    public int getGenjutsu() {
        return basePoints.get(Attribute.GEN.ordinal()) + distributedPoints.get(Attribute.GEN.ordinal())
                + pointsEarned.get(Attribute.GEN.ordinal());
    }

    @Exclude
    public int getSeal() {
        return basePoints.get(Attribute.SEAL.ordinal()) + distributedPoints.get(Attribute.SEAL.ordinal())
                + pointsEarned.get(Attribute.SEAL.ordinal());
    }

    @Exclude
    public int getAgility() {
        return basePoints.get(Attribute.AGI.ordinal()) + distributedPoints.get(Attribute.AGI.ordinal())
                + pointsEarned.get(Attribute.AGI.ordinal());
    }

    @Exclude
    public int getStrength() {
        return basePoints.get(Attribute.STR.ordinal()) + distributedPoints.get(Attribute.STR.ordinal())
                + pointsEarned.get(Attribute.STR.ordinal());
    }

    @Exclude
    public int getIntelligence() {
        return basePoints.get(Attribute.INTE.ordinal()) + distributedPoints.get(Attribute.INTE.ordinal())
                + pointsEarned.get(Attribute.INTE.ordinal());
    }

    @Exclude
    public int getResistance() {
        return basePoints.get(Attribute.RES.ordinal()) + distributedPoints.get(Attribute.RES.ordinal())
                + pointsEarned.get(Attribute.RES.ordinal());
    }

    @Exclude
    public int getEnergy() {
        return basePoints.get(Attribute.ENER.ordinal()) + distributedPoints.get(Attribute.ENER.ordinal())
                + pointsEarned.get(Attribute.ENER.ordinal());
    }

    public Formulas getFormulas() {
        return formulas;
    }

    public void setFormulas(Formulas formulas) {
        this.formulas = formulas;
    }

    @Bindable
    public int getTotalFreePoints() {
        return totalFreePoints;
    }

    public void setTotalFreePoints(int totalFreePoints) {
        this.totalFreePoints = totalFreePoints;
        notifyPropertyChanged(BR.totalFreePoints);
    }

    public int getTotalRedistributePoints() {
        return totalRedistributePoints;
    }

    public void setTotalRedistributePoints(int totalRedistributePoints) {
        this.totalRedistributePoints = totalRedistributePoints;
    }

    public List<Integer> getBasePoints() {
        return basePoints;
    }

    public void setBasePoints(List<Integer> basePoints) {
        this.basePoints = basePoints;
    }

    public List<Integer> getDistributedPoints() {
        return distributedPoints;
    }

    public void setDistributedPoints(List<Integer> distributedPoints) {
        this.distributedPoints = distributedPoints;
    }

    public List<Integer> getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(List<Integer> pointsEarned) {
        this.pointsEarned = pointsEarned;
    }
}
