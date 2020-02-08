package com.gutotech.narutogame.ui.playing.academy;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.gutotech.narutogame.R;
import com.gutotech.narutogame.data.model.Attributes;
import com.gutotech.narutogame.data.model.Character;
import com.gutotech.narutogame.data.model.Formulas;
import com.gutotech.narutogame.data.model.CharOn;
import com.gutotech.narutogame.data.repository.CharacterRepository;
import com.gutotech.narutogame.ui.adapter.DistributedPointsRecyclerAdapter;
import com.gutotech.narutogame.utils.DateCustom;
import com.gutotech.narutogame.utils.SingleLiveEvent;

public class AcademyTrainingViewModel extends AndroidViewModel
        implements DistributedPointsRecyclerAdapter.OnTrainButtonListener {
    private MutableLiveData<Integer> mSpentChakra = new MutableLiveData<>();
    private MutableLiveData<Integer> mSpentStamina = new MutableLiveData<>();

    SingleLiveEvent<Void> trainingErrorEvent = new SingleLiveEvent<>();
    SingleLiveEvent<Integer> trainingCompletedEvent = new SingleLiveEvent<>();
    private SingleLiveEvent<Void> updateDistributedPointsEvent = new SingleLiveEvent<>();

    private String[] mPercents;
    private double mPercent = 0.1;

    private Character mCharacter;
    private Attributes mAttributes;
    private Formulas mFormulas;

    private int weeklyLimitOfTraining;

    public AcademyTrainingViewModel(@NonNull Application application) {
        super(application);

        mCharacter = CharOn.character;
        mAttributes = mCharacter.getAttributes();
        mFormulas = mAttributes.getFormulas();

        mPercents = application.getResources().getStringArray(R.array.attribute_percent_list);

        updateLimitOfTraining();
        calculateChakraAndStaminaSpent();
    }

    public LiveData<Integer> getSpentChakra() {
        return mSpentChakra;
    }

    public LiveData<Integer> getSpentStamina() {
        return mSpentStamina;
    }

    public int getWeeklyLimitOfTraining() {
        return weeklyLimitOfTraining;
    }

    public Attributes getAttributes() {
        return mAttributes;
    }

    public Formulas getFormulas() {
        return mFormulas;
    }

    SingleLiveEvent<Void> getUpdateDistributedPointsEvent() {
        return updateDistributedPointsEvent;
    }

    public void onItemSelected(int position) {
        mPercent = Double.parseDouble(mPercents[position]);
        calculateChakraAndStaminaSpent();
    }

    private void calculateChakraAndStaminaSpent() {
        mSpentChakra.setValue((int) (mFormulas.getChakra() * mPercent / 100));
        mSpentStamina.setValue((int) (mFormulas.getStamina() * mPercent / 100));
    }

    private void updateLimitOfTraining() {
        int dayOfWeek = DateCustom.getDayOfWeek();
        int days = dayOfWeek >= 3 ? dayOfWeek - 2 : dayOfWeek + 5;

        weeklyLimitOfTraining = mCharacter.getGraduation().dailyTrainingLimit * days;
    }

    public void onTrainButtonPressed() {
        updateLimitOfTraining();
        if (mAttributes.getTrainingProgress() < weeklyLimitOfTraining) {
            if (mSpentChakra.getValue() <= mFormulas.getChakraAtual() &&
                    mSpentStamina.getValue() <= mFormulas.getStaminaAtual()) {
                mFormulas.setChakraAtual(mFormulas.getChakraAtual() - mSpentChakra.getValue());
                mFormulas.setStaminaAtual(mFormulas.getStaminaAtual() - mSpentStamina.getValue());

                int trainingPointsEarned = (int) mPercent * 10;

                int newTrainingProgress = trainingPointsEarned + mAttributes.getTrainingProgress();

                if (newTrainingProgress > weeklyLimitOfTraining) {
                    trainingPointsEarned = weeklyLimitOfTraining - mAttributes.getTrainingProgress();
                    newTrainingProgress = weeklyLimitOfTraining;
                }

                mAttributes.setTrainingProgress(newTrainingProgress);
                mAttributes.incrementTraningPoints(trainingPointsEarned);
                mCharacter.getExtrasInformation().incrementTotalTraining(trainingPointsEarned);

                mCharacter.incrementScore(trainingPointsEarned / 100);

                CharacterRepository.getInstance().save(mCharacter);

                updateDistributedPointsEvent.call();

                trainingCompletedEvent.setValue(trainingPointsEarned);
            } else {
                trainingErrorEvent.call();
            }
        }
    }

    @Override
    public void onTrainButtonClick(int attributePosition, int quantitySelected) {
        mAttributes.train(attributePosition, quantitySelected);
        mCharacter.getExtrasInformation().incrementDistributedPoints(quantitySelected);
        mCharacter.updateFormulas();

        CharacterRepository.getInstance().save(mCharacter);

        updateDistributedPointsEvent.call();
    }
}
