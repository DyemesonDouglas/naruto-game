package com.gutotech.narutogame.ui.playing.team;

import android.text.TextUtils;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.gutotech.narutogame.R;
import com.gutotech.narutogame.data.model.CharOn;
import com.gutotech.narutogame.data.model.Team;
import com.gutotech.narutogame.data.repository.TeamRepository;
import com.gutotech.narutogame.utils.SingleLiveEvent;

public class TeamCreateViewModel extends ViewModel {
    public final ObservableField<String> teamName = new ObservableField<>();

    private SingleLiveEvent<Integer> mShowWarningDialogEvent = new SingleLiveEvent<>();

    private TeamRepository mTeamRepository = TeamRepository.getInstance();

    LiveData<Integer> getShowWarningDialogEvent() {
        return mShowWarningDialogEvent;
    }

    public void onCreateTeamClick() {
        if (isValidName()) {
            mTeamRepository.save(new Team(mTeamRepository.generateId(), teamName.get(),
                    1, CharOn.character.getVillage().ordinal(), 0, 5000,
                    1000, CharOn.character.getId()));
            CharOn.character.setTeam(teamName.get());
        }
    }

    private boolean isValidName() {
        if (TextUtils.isEmpty(teamName.get())) {
            mShowWarningDialogEvent.setValue(R.string.error_the_name_of_team_cant_be_empty);
            return false;
        }

        return true;
    }
}
