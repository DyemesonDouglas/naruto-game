package com.gutotech.narutogame.ui.playing.battles;

import android.os.CountDownTimer;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gutotech.narutogame.R;
import com.gutotech.narutogame.data.model.Battle;
import com.gutotech.narutogame.data.model.BattleLog;
import com.gutotech.narutogame.data.model.CharOn;
import com.gutotech.narutogame.data.model.Character;
import com.gutotech.narutogame.data.model.Classe;
import com.gutotech.narutogame.data.model.Formulas;
import com.gutotech.narutogame.data.model.Jutsu;
import com.gutotech.narutogame.data.model.JutsuInfo;
import com.gutotech.narutogame.data.model.Npc;
import com.gutotech.narutogame.data.model.Score;
import com.gutotech.narutogame.data.repository.BattleRepository;
import com.gutotech.narutogame.data.repository.CharacterRepository;
import com.gutotech.narutogame.ui.adapter.JutsusAdapter;
import com.gutotech.narutogame.utils.DateCustom;
import com.gutotech.narutogame.utils.SingleLiveEvent;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DojoBatalhaLutadorViewModel extends ViewModel
        implements JutsusAdapter.OnJutsuClickListener {
    private final long TIME_TO_ATTACK = 150000;

    private BattleRepository mBattleRepository;

    private Battle mBattle;

    private Character player;
    private List<Jutsu> mAllJutsus;
    private MutableLiveData<List<Jutsu>> mJutsus = new MutableLiveData<>(new ArrayList<>());
    private Jutsu.Type mJutsuTypeSelected;

    private Npc npc;

    private Formulas playerFormulas;
    private Formulas npcFormulas;

    private MutableLiveData<List<Jutsu>> myBuffsDebuffsStatus = new MutableLiveData<>();
    private MutableLiveData<List<Jutsu>> oppBuffsDebuffsStatus = new MutableLiveData<>();

    private MutableLiveData<List<BattleLog>> mBattleLogs = new MutableLiveData<>(new ArrayList<>());

    private CountDownTimer mCountDownTimer;

    public final ObservableField<String> countDown = new ObservableField<>("--:--");

    SingleLiveEvent<Object[]> showJutsuInfoPopupEvent = new SingleLiveEvent<>();
    SingleLiveEvent<Integer> showWarningDialogEvent = new SingleLiveEvent<>();
    SingleLiveEvent<View> startAnimationEvent = new SingleLiveEvent<>();
    SingleLiveEvent<Integer[]> showWonEvent = new SingleLiveEvent<>();
    SingleLiveEvent<Void> showLostEvent = new SingleLiveEvent<>();
    SingleLiveEvent<Void> showDrawnEvent = new SingleLiveEvent<>();
    SingleLiveEvent<Void> showInactivatedEvent = new SingleLiveEvent<>();

    private long elapsedTime;

    private int myTurn;

    DojoBatalhaLutadorViewModel(Battle battle) {
        mBattle = battle;

        myTurn = 0;

        player = mBattle.getPlayer1();
        npc = new Npc(mBattle.getPlayer2());

        playerFormulas = player.getFormulas();
        npcFormulas = npc.getCharacter().getFormulas();

        myBuffsDebuffsStatus.setValue(mBattle.getPlayer1BuffsDebuffsStatus());
        oppBuffsDebuffsStatus.setValue(mBattle.getPlayer2BuffsDebuffsStatus());
        mBattleLogs.setValue(mBattle.getBattleLogs());

        mAllJutsus = player.getJutsus();
        filterJutsus(Jutsu.Type.ATK);

        mBattleRepository = BattleRepository.getInstance();

        if (mBattle.getStatus() == Battle.Status.CONTINUE) {
            init();
        } else {
            finishFight();
        }
    }

    void init() {
        elapsedTime = Calendar.getInstance().getTimeInMillis() - mBattle.getAttackStart();

        if (mCountDownTimer == null) {
            startTimer();
        }
    }

    public Character getPlayer() {
        return player;
    }

    public Npc getNpc() {
        return npc;
    }

    public Formulas getPlayerFormulas() {
        return playerFormulas;
    }

    public Formulas getNpcFormulas() {
        return npcFormulas;
    }

    LiveData<List<BattleLog>> getBattleLogs() {
        return mBattleLogs;
    }

    LiveData<List<Jutsu>> getJutsus() {
        return mJutsus;
    }

    LiveData<List<Jutsu>> getMyBuffsDebuffsStatus() {
        return myBuffsDebuffsStatus;
    }

    LiveData<List<Jutsu>> getOppBuffsDebuffsStatus() {
        return oppBuffsDebuffsStatus;
    }

    @Override
    public void onJutsuClick(View view, Jutsu jutsu) {
        startAnimationEvent.setValue(view);

        if (mBattle.getCurrentPlayer() != myTurn) {
            showWarningDialogEvent.setValue(R.string.it_is_not_your_turn_to_attack);
            return;
        }

        if (jutsu.getRemainingIntervals() != 0) {
            showWarningDialogEvent.setValue(R.string.jutsu_is_not_yet_available);
            return;
        }

        if (jutsu.getConsumesChakra() > playerFormulas.getCurrentChakra() ||
                jutsu.getConsumesStamina() > playerFormulas.getCurrentStamina()) {
            showWarningDialogEvent.setValue(R.string.dont_have_chakra_to_use_this_jutsu);
            return;
        }

        JutsuInfo playerJutsuInfo = jutsu.getJutsuInfo();

        if (playerJutsuInfo.type == Jutsu.Type.ATK || playerJutsuInfo.type == Jutsu.Type.DEF) {
            mCountDownTimer.cancel();

            Jutsu jutsuNpc = npc.attack();

            executeAttacks(jutsu, playerJutsuInfo, jutsuNpc, JutsuInfo.valueOf(jutsuNpc.getName()));

            updateFightStatus();

            mBattle.setAttackStart(DateCustom.getTimeInMillis());
            elapsedTime = 0;
            saveBattle();
            startTimer();
        } else {
            if (!buffOrDebuffUsed(playerJutsuInfo.type)) {
                if (playerJutsuInfo.type == Jutsu.Type.BUFF) {
                    addBuffOrDebuff(playerFormulas, jutsu);

                    List<Jutsu> buffsAndDebuffs = mBattle.getPlayer1BuffsDebuffsStatus();
                    buffsAndDebuffs.add(jutsu);
                    myBuffsDebuffsStatus.setValue(buffsAndDebuffs);
                } else if (playerJutsuInfo.type == Jutsu.Type.DEBUFF) {
                    addBuffOrDebuff(npcFormulas, jutsu);

                    List<Jutsu> buffsAndDebuffs = mBattle.getPlayer2BuffsDebuffsStatus();
                    buffsAndDebuffs.add(jutsu);
                    oppBuffsDebuffsStatus.setValue(buffsAndDebuffs);
                }

                playerFormulas.subChakra(jutsu.getConsumesChakra());
                playerFormulas.subStamina(jutsu.getConsumesStamina());

                addLog(new BattleLog(player.getNick(), BattleLog.Type.BUFF_DEBUFF_WEAPON,
                        playerJutsuInfo.name, jutsu));
            } else {
                return;
            }
        }

        if (mBattle.getStatus() == Battle.Status.CONTINUE) {
            updateRemainingIntervals();
            int jutsuIndex = mAllJutsus.indexOf(jutsu);
            jutsu.setRemainingIntervals(jutsu.getUsageInterval() - 1);
            mAllJutsus.set(jutsuIndex, jutsu);
            filterJutsus(mJutsuTypeSelected);
        } else {
            finishFight();
        }
    }

    @Override
    public void onJutsuInfoClick(View anchor, Jutsu jutsu) {
        Object[] objects = new Object[3];
        objects[0] = calculateChanceOfSuccess(jutsu.getAccuracy(), playerFormulas.getAccuracy());
        objects[1] = jutsu;
        objects[2] = anchor;

        showJutsuInfoPopupEvent.setValue(objects);
    }

    private boolean buffOrDebuffUsed(Jutsu.Type type) {
        if (type == Jutsu.Type.BUFF) {
            for (Jutsu jutsu : myBuffsDebuffsStatus.getValue()) {
                if (jutsu.getJutsuInfo().type == Jutsu.Type.BUFF) {
                    return true;
                }
            }
        } else {
            for (Jutsu jutsu : oppBuffsDebuffsStatus.getValue()) {
                if (jutsu.getJutsuInfo().type == Jutsu.Type.DEBUFF) {
                    return true;
                }
            }
        }

        return false;
    }

    private int calculateChanceOfSuccess(int jutsuAccuracy, int currentAccuracy) {
        int chanceOfSuccess;

        try {
            chanceOfSuccess = (int) ((double) currentAccuracy / jutsuAccuracy * 100);
        } catch (ArithmeticException e) {
            chanceOfSuccess = 100;
        }

        if (chanceOfSuccess > 100) {
            chanceOfSuccess = 100;
        } else if (chanceOfSuccess < 0) {
            chanceOfSuccess = 0;
        }

        return chanceOfSuccess;
    }

    private int calculateChanceOfError(int jutsuAccuracy, int currentAccuracy) {
        return 100 - calculateChanceOfSuccess(jutsuAccuracy, currentAccuracy);
    }

    private final SecureRandom random = new SecureRandom();

    private boolean missedTheJutsu(int chanceOfError) {
        int n = random.nextInt(100);
        return n < chanceOfError;
    }

    private void executeAttacks(Jutsu myJutsu, JutsuInfo myJutsuInfo, Jutsu oppJutsu,
                                JutsuInfo oppJutsuInfo) {
        int myChanceOfError = calculateChanceOfError(myJutsu.getAccuracy(), playerFormulas.getAccuracy());

        boolean myMissed = missedTheJutsu(myChanceOfError);

        int myDamage = 0;
        int oppDamage = calcuteDamage(oppJutsu, oppJutsuInfo, npcFormulas, playerFormulas);

        if (myMissed) {
            if (oppDamage > 0) {
                playerFormulas.subHeath(oppDamage);
            }
        } else {
            myDamage = calcuteDamage(myJutsu, myJutsuInfo, playerFormulas, npcFormulas);

            if (myDamage > 0 && oppDamage > 0) {
                playerFormulas.subHeath(oppDamage);
                npcFormulas.subHeath(myDamage);
            } else if (myDamage > 0 && oppDamage < 0) {
                myDamage = myDamage - Math.abs(oppDamage);
                npcFormulas.subHeath(myDamage);
            } else if (myDamage < 0 && oppDamage > 0) {
                oppDamage = oppDamage - Math.abs(myDamage);
                playerFormulas.subHeath(oppDamage);
            }
        }

        playerFormulas.subChakra(myJutsu.getConsumesChakra());
        playerFormulas.subStamina(myJutsu.getConsumesStamina());
        npcFormulas.subChakra(oppJutsu.getConsumesChakra());
        npcFormulas.subStamina(oppJutsu.getConsumesStamina());

        // Creates battle log
        addLog(new BattleLog(player.getNick(), BattleLog.Type.USES, myJutsuInfo.name, myJutsu,
                calculateChanceOfSuccess(myJutsu.getAccuracy(), playerFormulas.getAccuracy())));
        if (myMissed) {
            addLog(new BattleLog(player.getNick(), BattleLog.Type.MISSED));
        } else {
            if (myJutsuInfo.type == Jutsu.Type.ATK) {
                addLog(new BattleLog(npc.getCharacter().getNick(), BattleLog.Type.RECEIVES, myDamage));
            }
        }

        addLog(new BattleLog(npc.getCharacter().getNick(), BattleLog.Type.USES, oppJutsuInfo.name,
                oppJutsu, 100));
        if (oppJutsuInfo.type == Jutsu.Type.ATK) {
            addLog(new BattleLog(player.getNick(), BattleLog.Type.RECEIVES, oppDamage));
        }

        addLog(new BattleLog(BattleLog.Type.END));
    }

    private int calcuteDamage(Jutsu jutsu, JutsuInfo jutsuInfo, Formulas myFormulas,
                              Formulas oppFormulas) {
        int damage;

        if (jutsuInfo.type == Jutsu.Type.ATK) {
            if (jutsu.getClasse() == Classe.NIN || jutsu.getClasse() == Classe.GEN) {
                damage = (jutsu.getAtk() + myFormulas.getAtkNinGen()) - oppFormulas.getDefNinGen();
            } else {
                damage = (jutsu.getAtk() + myFormulas.getAtkTaiBuki()) - oppFormulas.getDefNinGen();
            }
        } else {
            damage = (jutsu.getBaseDefense() + myFormulas.getDefNinGen()) * -1;
        }

        return damage;
    }

    private void addLog(BattleLog log) {
        List<BattleLog> logs = mBattle.getBattleLogs();
        logs.add(log);
        mBattleLogs.setValue(logs);
    }

    private void updateRemainingIntervals() {
        for (int i = 0; i < mAllJutsus.size(); i++) {
            Jutsu jutsu = mAllJutsus.get(i);

            if (jutsu.getRemainingIntervals() > 0) {
                jutsu.setRemainingIntervals(jutsu.getRemainingIntervals() - 1);

                if (jutsu.getRemainingIntervals() == 0) {
                    if (jutsu.getJutsuInfo().type == Jutsu.Type.BUFF) {
                        List<Jutsu> buffsAndDebuffs = myBuffsDebuffsStatus.getValue();
                        buffsAndDebuffs.remove(jutsu);
                        myBuffsDebuffsStatus.setValue(buffsAndDebuffs);
                        removeBuffDebuff(playerFormulas, jutsu);
                    } else if (jutsu.getJutsuInfo().type == Jutsu.Type.DEBUFF) {
                        List<Jutsu> buffsAndDebuffs = oppBuffsDebuffsStatus.getValue();
                        buffsAndDebuffs.remove(jutsu);
                        oppBuffsDebuffsStatus.setValue(buffsAndDebuffs);
                        removeBuffDebuff(npcFormulas, jutsu);
                    }
                }
            }
        }
    }

    private void addBuffOrDebuff(Formulas formulas, Jutsu buffOrDebuff) {
        formulas.setAtkTaiBuki(formulas.getAtkTaiBuki() + buffOrDebuff.getAtk());
        formulas.setAtkNinGen(formulas.getAtkNinGen() + buffOrDebuff.getAtk());
        formulas.setDefTaiBuki(formulas.getDefTaiBuki() + buffOrDebuff.getBaseDefense());
        formulas.setDefNinGen(formulas.getDefNinGen() + buffOrDebuff.getBaseDefense());
        formulas.setAccuracy(formulas.getAccuracy() + buffOrDebuff.getAccuracy());
    }

    private void removeBuffDebuff(Formulas formulas, Jutsu buffOrDebuff) {
        formulas.setAtkTaiBuki(formulas.getAtkTaiBuki() - buffOrDebuff.getAtk());
        formulas.setAtkNinGen(formulas.getAtkNinGen() - buffOrDebuff.getAtk());
        formulas.setDefTaiBuki(formulas.getDefTaiBuki() - buffOrDebuff.getBaseDefense());
        formulas.setDefNinGen(formulas.getDefNinGen() - buffOrDebuff.getBaseDefense());
        formulas.setAccuracy(formulas.getAccuracy() - buffOrDebuff.getAccuracy());
    }

    public void filterJutsus(Jutsu.Type filteredType) {
        mJutsuTypeSelected = filteredType;

        List<Jutsu> filteredJutsus = new ArrayList<>();

        for (int i = 0; i < mAllJutsus.size(); i++) {
            Jutsu jutsu = mAllJutsus.get(i);
            JutsuInfo jutsuInfo = jutsu.getJutsuInfo();

            if (filteredType == Jutsu.Type.ATK &&
                    (jutsuInfo.type == Jutsu.Type.ATK || jutsuInfo.type == Jutsu.Type.DEF)) {
                filteredJutsus.add(jutsu);
            } else if (filteredType == Jutsu.Type.BUFF &&
                    (jutsuInfo.type == Jutsu.Type.BUFF || jutsuInfo.type == Jutsu.Type.DEBUFF)) {
                filteredJutsus.add(jutsu);
            }
        }

        mJutsus.setValue(filteredJutsus);
    }

    private void updateFightStatus() {
        if ((npcFormulas.getCurrentHealth() < 10 || npcFormulas.getCurrentChakra() < 10 || npcFormulas.getCurrentStamina() < 10)
                && (playerFormulas.getCurrentHealth() < 10 || playerFormulas.getCurrentChakra() < 10 || playerFormulas.getCurrentStamina() < 10)) {
            mBattle.setStatus(Battle.Status.DRAWN);
        } else if (npcFormulas.getCurrentHealth() < 10 || npcFormulas.getCurrentChakra() < 10 || npcFormulas.getCurrentStamina() < 10) {
            mBattle.setStatus(Battle.Status.PLAYER1_WON);
        } else if (playerFormulas.getCurrentHealth() < 10 || playerFormulas.getCurrentChakra() < 10 || playerFormulas.getCurrentStamina() < 10) {
            mBattle.setStatus(Battle.Status.PLAYER2_WON);
        } else {
            mBattle.setStatus(Battle.Status.CONTINUE);
        }
    }

    private void finishFight() {
        if (mBattle.getStatus() == Battle.Status.PLAYER1_WON) {
            int earnedRyous = 100;
            int earnedExp = 344 - (29 * player.getLevel()) > 0 ? 344 - (29 * player.getLevel()) : 0;

            showWonEvent.setValue(new Integer[]{earnedRyous, earnedExp});
        } else if (mBattle.getStatus() == Battle.Status.PLAYER2_WON) {
            showLostEvent.call();
        } else if (mBattle.getStatus() == Battle.Status.DRAWN) {
            showDrawnEvent.call();
        } else {
            playerFormulas.setCurrentHealth(0);
            showInactivatedEvent.call();
        }

        saveBattle();
    }

    void exit() {
        CharOn.character.setNpcDailyCombat(CharOn.character.getNpcDailyCombat() + 1);
        CharOn.character.getFormulas().setCurrentHealth(playerFormulas.getCurrentHealth());
        CharOn.character.getFormulas().setCurrentChakra(playerFormulas.getCurrentChakra());
        CharOn.character.getFormulas().setCurrentStamina(playerFormulas.getCurrentStamina());

        if (mBattle.getStatus() == Battle.Status.PLAYER1_WON) {
            CharOn.character.getCombatOverview().setWinsNpc(CharOn.character.getCombatOverview().getWinsNpc() + 1);

            int earnedRyous = 100;
            int earnedExp = 344 - (29 * player.getLevel()) > 0 ? 344 - (29 * player.getLevel()) : 0;

            CharOn.character.incrementExp(earnedExp);
            CharOn.character.addRyous(earnedRyous);
            CharOn.character.incrementScore(Score.VIT_DOJO_NPC);
        } else if (mBattle.getStatus() == Battle.Status.PLAYER2_WON) {
            CharOn.character.getCombatOverview().setLossesNpc(CharOn.character.getCombatOverview().getLossesNpc() + 1);
            CharOn.character.decrementScore(Score.DER_DOJO_NPC);
        } else if (mBattle.getStatus() == Battle.Status.DRAWN) {
            CharOn.character.getCombatOverview().setDrawsNpc(CharOn.character.getCombatOverview().getDrawsNpc() + 1);
        } else {
            CharOn.character.getCombatOverview().setLossesNpc(CharOn.character.getCombatOverview().getLossesNpc() + 1);
            CharOn.character.decrementScore(Score.DER_DOJO_NPC);
        }

        mBattleRepository.delete(mBattle.getId());
        mBattle.setPlayerCount(0);

        CharOn.character.setBattle(false);
        CharOn.character.battleId = "";
        CharacterRepository.getInstance().save(CharOn.character);
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(TIME_TO_ATTACK - elapsedTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDown.set(String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                )
                        )
                );
            }

            @Override
            public void onFinish() {
                countDown.set("--:--");

                if (mBattle.getCurrentPlayer() != myTurn) {
                    mBattle.setStatus(Battle.Status.PLAYER1_WON);
                } else {
                    mBattle.setStatus(Battle.Status.PLAYER2_WON);
                    playerFormulas.setCurrentHealth(0);
                }

                finishFight();
            }
        }.start();
    }

    void stop() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
            if (mBattle.getPlayerCount() != 0) {
                saveBattle();
            }
        }
    }

    private void saveBattle() {
        mBattleRepository.save(mBattle);
    }
}
