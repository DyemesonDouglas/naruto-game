package com.gutotech.narutogame.ui.playing.character;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.gutotech.narutogame.R;
import com.gutotech.narutogame.data.repository.AuthRepository;
import com.gutotech.narutogame.ui.SectionFragment;
import com.gutotech.narutogame.ui.adapter.RecompensasFidelidadeAdapter;
import com.gutotech.narutogame.data.firebase.FirebaseConfig;
import com.gutotech.narutogame.utils.FragmentUtil;
import com.gutotech.narutogame.utils.StorageUtil;
import com.gutotech.narutogame.data.model.Player;
import com.gutotech.narutogame.data.model.RecompensaFidelidade;
import com.gutotech.narutogame.ui.MyListener;
import com.gutotech.narutogame.data.model.CharOn;

import java.util.ArrayList;
import java.util.List;

public class FidelityFragment extends Fragment implements SectionFragment, MyListener {
    private List<RecompensaFidelidade> recompensas = new ArrayList<>();

    public FidelityFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fidelity, container, false);

        ImageView personagemMsgImageView = view.findViewById(R.id.personagemMsg);
        StorageUtil.downloadProfileForMsg(getActivity(), personagemMsgImageView);

        recompensas.add(new RecompensaFidelidade(RecompensaFidelidade.Tipo.RY, 100, "de Ryous", this));
        recompensas.add(new RecompensaFidelidade(RecompensaFidelidade.Tipo.EXP, 200, "de Experiência", this));
        recompensas.add(new RecompensaFidelidade(RecompensaFidelidade.Tipo.RY, 300, "de Ryous", this));
        recompensas.add(new RecompensaFidelidade(RecompensaFidelidade.Tipo.EXP, 400, "de Experiência", this));
        recompensas.add(new RecompensaFidelidade(RecompensaFidelidade.Tipo.RY, 1000, "de Ryous", this));
        recompensas.add(new RecompensaFidelidade(RecompensaFidelidade.Tipo.RAMEM, 5, "Shio Tyashu-Ramen", this));
        recompensas.add(new RecompensaFidelidade(RecompensaFidelidade.Tipo.PONTO_BIJUU, 5, "Pontos para o Sorteio de Bijuu", this));
        recompensas.add(new RecompensaFidelidade(RecompensaFidelidade.Tipo.CREDITO, 5, "Crédito VIP ( uma vez por conta )", this));

        RecompensasFidelidadeAdapter adapter = new RecompensasFidelidadeAdapter(getActivity(), recompensas);
//        recompensasReceberGridView.setAdapter(adapter);

        return view;
    }

    @Override
    public void callback(int position) {
        final RecompensaFidelidade recompensa = recompensas.get(position);

        switch (recompensa.getTipo()) {
            case RY:
                CharOn.character.setRyous(CharOn.character.getRyous() + recompensa.getQuantidade());
                break;
            case EXP:
                CharOn.character.setExp(CharOn.character.getExp() + recompensa.getQuantidade());
                break;
            case RAMEM:
                break;
            case PONTO_BIJUU:
                break;
            case CREDITO:
                DatabaseReference playerReference = FirebaseConfig.getDatabase()
                        .child("players").child(AuthRepository.getInstance().getCurrentUser().getUid());
                playerReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Player player = dataSnapshot.getValue(Player.class);
//                        player.setVipCredits(player.getVipCredits() + recompensa.getQuantidade());
//                        player.salvar();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                break;
        }

//        int diasLogadosFidelidade = PersonagemOn.character.getDiasLogadosFidelidade();
//
//        PersonagemOn.character.setDiasLogadosFidelidade(diasLogadosFidelidade + 1 > 8 ? 1 : diasLogadosFidelidade + 1);
//        PersonagemOn.character.setTemRecompensaFidelidade(false);

        CharOn.character.salvar();

        FragmentUtil.setSectionTitle(getActivity(), R.string.section_ninja_fidelity);

        changeTo(new FidelityFragment());
    }

    private void changeTo(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public int getDescription() {
        return R.string.ninja_fidelity;
    }
}
