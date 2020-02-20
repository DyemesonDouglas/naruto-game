package com.gutotech.narutogame.ui.playing.battles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gutotech.narutogame.R;
import com.gutotech.narutogame.ui.SectionFragment;
import com.gutotech.narutogame.utils.FragmentUtil;

public class DojoFragment extends Fragment implements SectionFragment {

    public DojoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dojo, container, false);

        Button dojoNpcButton = view.findViewById(R.id.dojoNpcButton);
        Button dojoPvpButton = view.findViewById(R.id.dojoPvpButton);

        dojoNpcButton.setOnClickListener(v -> goTo(new DojoNPCFightersFragment()));
        dojoPvpButton.setOnClickListener(v -> goTo(new BatalhasDojoPVPFragment()));

        goTo(new DojoNPCFightersFragment());

        FragmentUtil.setSectionTitle(getActivity(), R.string.section_dojo);

        return view;
    }

    private void goTo(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public int getDescription() {
        return R.string.dojo;
    }
}
