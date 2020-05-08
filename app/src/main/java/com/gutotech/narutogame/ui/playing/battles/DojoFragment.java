package com.gutotech.narutogame.ui.playing.battles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.gutotech.narutogame.R;
import com.gutotech.narutogame.ui.SectionFragment;
import com.gutotech.narutogame.utils.FragmentUtils;

public class DojoFragment extends Fragment implements SectionFragment {

    public DojoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dojo, container, false);

        Button dojoNpcButton = view.findViewById(R.id.dojoNpcButton);
        Button dojoPvpButton = view.findViewById(R.id.dojoPvpButton);

        dojoNpcButton.setOnClickListener(v -> {
            goTo(new DojoNPCFightersFragment());
            dojoNpcButton.setBackgroundResource(R.drawable.bg_button_orange);
            dojoPvpButton.setBackgroundResource(R.drawable.bg_button);
        });

        dojoPvpButton.setOnClickListener(v -> {
            goTo(new DojoPvpBattlesFragment());
            dojoNpcButton.setBackgroundResource(R.drawable.bg_button);
            dojoPvpButton.setBackgroundResource(R.drawable.bg_button_orange);
        });

        Bundle bundle = getArguments();

        if (bundle != null) {
            String battleType = bundle.getString("battleType");

            if (battleType.equals("PVP-DOJO")) {
                goTo(new DojoPvpBattlesFragment());
                dojoNpcButton.setBackgroundResource(R.drawable.bg_button);
                dojoPvpButton.setBackgroundResource(R.drawable.bg_button_orange);
            } else {
                goTo(new DojoNPCFightersFragment());
                dojoNpcButton.setBackgroundResource(R.drawable.bg_button_orange);
                dojoPvpButton.setBackgroundResource(R.drawable.bg_button);
            }
        } else {
            dojoNpcButton.setBackgroundResource(R.drawable.bg_button_orange);
            goTo(new DojoNPCFightersFragment());
        }

        FragmentUtils.setSectionTitle(getActivity(), R.string.section_dojo);

        AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

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
