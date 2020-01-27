package com.gutotech.narutogame.ui.playing.academy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gutotech.narutogame.R;
import com.gutotech.narutogame.databinding.FragmentAcademyJutsuBinding;
import com.gutotech.narutogame.ui.SectionFragment;
import com.gutotech.narutogame.ui.adapter.JutsusLearnAdapter;
import com.gutotech.narutogame.utils.FragmentUtil;

public class AcademyJutsuFragment extends Fragment implements SectionFragment {
    private FragmentAcademyJutsuBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AcademyJutsuViewModel viewModel = ViewModelProviders.of(this)
                .get(AcademyJutsuViewModel.class);

        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_academy_jutsu, container, false);
        mBinding.setLifecycleOwner(this);
        mBinding.setViewModel(viewModel);

        mBinding.trainingResultLayout.setVisibility(View.GONE);

        mBinding.jutsusRecyclerView.setHasFixedSize(true);
        JutsusLearnAdapter adapter = new JutsusLearnAdapter(getActivity(), getFragmentManager(), viewModel);
        mBinding.jutsusRecyclerView.setAdapter(adapter);

        viewModel.getJutsus().observe(this, adapter::setJutsusList);

        viewModel.getShowCongratulationsEvent().observe(this, resId -> {
            SpannableStringBuilder message = new SpannableStringBuilder();
            message.append(getText(R.string.lerned_a_new_jutsu));
            message.append(" ");

            int index = message.length();
            message.append(getString(resId));
            message.setSpan(new ForegroundColorSpan(
                            getResources().getColor(android.R.color.holo_orange_light)),
                    index, message.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            message.append("\n");
            message.append(getString(R.string.keep_training));

           showTrainingResult(R.string.congratulations_you_made_it, message);
        });

        viewModel.getShowWarningEvent().observe(this, resId -> {
            showTrainingResult(R.string.problem, getString(R.string.insufficient_chakra_or_stamina,
                    getString(resId)));
        });

        FragmentUtil.setSectionTitle(getActivity(), R.string.section_learned_jutsus);
        return mBinding.getRoot();
    }

    private void showTrainingResult(@StringRes int title, CharSequence message) {
        mBinding.msgTitleTextView.setText(title);
        mBinding.msgTextView.setText(message);
        mBinding.trainingResultLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public int getDescription() {
        return R.string.leaned_jutsus;
    }
}
