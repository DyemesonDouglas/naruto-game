package com.gutotech.narutogame.ui.playing.battles;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.gutotech.narutogame.R;
import com.gutotech.narutogame.ui.adapter.JutsusAdapter;
import com.gutotech.narutogame.data.firebase.FirebaseConfig;
import com.gutotech.narutogame.utils.StorageUtil;
import com.gutotech.narutogame.utils.RecyclerItemClickListener;
import com.gutotech.narutogame.data.model.BatalhaPVP;
import com.gutotech.narutogame.data.model.Formulas;
import com.gutotech.narutogame.data.model.Jutsu;
import com.gutotech.narutogame.data.model.Character;
import com.gutotech.narutogame.data.model.Oponente;
import com.gutotech.narutogame.data.model.CharOn;

import java.util.Locale;

public class DojoBatalhaPVPFragment extends Fragment {
    private ImageView profileOpoImageView;
    private TextView nickOpoTextView;
    private TextView graduELvlOpoTextView;
    private ImageView statusOpoImageView;
    private ProgressBar hpOpoProgressBar;
    private TextView hpOpoTextView;
    private ProgressBar chakraOpoProgressBar;
    private TextView chakraOpoTextView;
    private ProgressBar staminaOpoProgressBar;
    private TextView staminaOpoTextView;
    private Formulas oponenteFormulas;

    private Character player;
    private ProgressBar hpPlayerProgressBar;
    private TextView hpPlayerTextView;
    private ProgressBar chakraPlayerProgressBar;
    private TextView chakraPlayerTextView;
    private ProgressBar staminaPlayerProgressBar;
    private TextView staminaPlayerTextView;
    private Formulas playerFormulas;

    private RecyclerView meusJutsusRecyclerView;
    private JutsusAdapter jutsusAdapter;

    private RecyclerView logCombateRecycler;

    private TextView countDownTimerTextView;
    private final long TEMPO_PARA_ATACAR = 120000;
    CountDownTimer countDownTimer;
    private TextView vezDeQuemTextView;

    private enum Status {GANHOU, PERDEU, EMPATOU, CONTINUA}

    private Status LUTA_STATUS = Status.CONTINUA;

    private ConstraintLayout msgLinear2;
    private ImageView imagemMsg;
    private TextView titleMsg;
    private TextView messageMsg;
    private TextView linkMsg;
    private TextView textoParaLinkMsg;

    private boolean iniciarBatalha = true;
    private DatabaseReference batalhaPvpReference;
    private ValueEventListener valueEventListenerBatalhaPvp;
    private String quemAtaca;
    private String primeiroAtacar;

    private BatalhaPVP batalhaPVP;

    private boolean souPlayer1;

    private MediaPlayer mediaPlayer;

    public DojoBatalhaPVPFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dojo_batalha_pv, container, false);

        batalhaPvpReference = FirebaseConfig.getDatabase()
                .child("batalha_dojo_pvp");
//                .child(PersonagemOn.character.getIdBatalhaAtual());
        recuperarBatalha();

        statusOpoImageView = view.findViewById(R.id.statusOpoImageView);
        profileOpoImageView = view.findViewById(R.id.profileOpoImageView);
        nickOpoTextView = view.findViewById(R.id.nickOpoTextView);
        graduELvlOpoTextView = view.findViewById(R.id.gradELvlOpoTextView);
        hpOpoProgressBar = view.findViewById(R.id.hpOpoProgressBar);
        hpOpoTextView = view.findViewById(R.id.hpOpoTextView);
        chakraOpoProgressBar = view.findViewById(R.id.chakraOpoProgressBar);
        chakraOpoTextView = view.findViewById(R.id.chakraOpoTextView);
        staminaOpoProgressBar = view.findViewById(R.id.staminaOpoProgressBar);
        staminaOpoTextView = view.findViewById(R.id.staminaOpoTextView);

        // CONFIGURA PLAYER
        player = CharOn.character;
//        playerFormulas = PersonagemOn.character.getAtributos().getFormulas();
        ImageView statusPlayerImageView = view.findViewById(R.id.statusMeImageView);
        statusPlayerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exibirStatus(v, playerFormulas);
            }
        });
        ImageView profilePlayerImageView = view.findViewById(R.id.profileMeImageView);
        StorageUtil.downloadProfile(getActivity(), profilePlayerImageView, player.getNinja().getId(), player.getProfile());

        TextView nickPlayerTextView = view.findViewById(R.id.nickMeTextView);
        nickPlayerTextView.setText(player.getNick());
        TextView graduELvlPlayerTextView = view.findViewById(R.id.gradELvlMeTextView);
//        graduELvlPlayerTextView.setText(String.format(Locale.getDefault(), "%s - Lvl %d", player.getGraducao(), player.getLevel()));
//
//        hpPlayerProgressBar = view.findViewById(R.id.hpMeProgressBar);
//        hpPlayerProgressBar.setMax(player.getAtributos().getFormulas().getCurrentHealth());
        hpPlayerTextView = view.findViewById(R.id.hpMeTextView);
        chakraPlayerProgressBar = view.findViewById(R.id.chakraMeProgressBar);
        chakraPlayerProgressBar.setMax(playerFormulas.getChakraAtual());
        chakraPlayerTextView = view.findViewById(R.id.chakraMeTextView);
        staminaPlayerProgressBar = view.findViewById(R.id.staminaMeProgressBar);
        staminaPlayerProgressBar.setMax(playerFormulas.getStaminaAtual());
        staminaPlayerTextView = view.findViewById(R.id.staminaMeTextView);

        meusJutsusRecyclerView = view.findViewById(R.id.meusJutsusRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        meusJutsusRecyclerView.setLayoutManager(layoutManager);
        meusJutsusRecyclerView.setHasFixedSize(true);
//        jutsusAdapter = new JutsusAdapter(getActivity(), player.getJutsus());
        meusJutsusRecyclerView.setAdapter(jutsusAdapter);
        meusJutsusRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(), meusJutsusRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (quemAtaca.equals(CharOn.character.getNick())) {
                    countDownTimer.cancel();
//                    PersonagemOn.character.setJutsuSelecionado(PersonagemOn.character.getJutsus().get(position));
                    configurarPlayersNaBatalha();
                    batalhaPVP.setQuemAtaca(Oponente.oponente.getNick());
                    batalhaPVP.setNumeroDeQuemAtaca(batalhaPVP.getNumeroDeQuemAtaca() + 1);
                    batalhaPvpReference.setValue(batalhaPVP);
                } else
                    Toast.makeText(getActivity(), "Não é sua vez de atacar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongItemClick(View view, int position) {
                exibirJutsu(view, position);
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        }
        ));

        logCombateRecycler = view.findViewById(R.id.logCombateRecycler);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        logCombateRecycler.setLayoutManager(layoutManager);
        logCombateRecycler.setHasFixedSize(true);
        //logCombateRecycler.setAdapter();

        // Configura msg
        msgLinear2 = view.findViewById(R.id.msgConstraint2);
        imagemMsg = view.findViewById(R.id.imagemMsg2);
        titleMsg = view.findViewById(R.id.msgTitleTextView2);
        messageMsg = view.findViewById(R.id.msgMensagem2);
        linkMsg = view.findViewById(R.id.linkTextView);
        textoParaLinkMsg = view.findViewById(R.id.textoParaLinkTextView);

        countDownTimerTextView = view.findViewById(R.id.countDownTimer);
        vezDeQuemTextView = view.findViewById(R.id.vezDeQuemTextView);

        return view;
    }

    private void recuperarBatalha() {
        valueEventListenerBatalhaPvp = batalhaPvpReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                batalhaPVP = dataSnapshot.getValue(BatalhaPVP.class);

                quemAtaca = batalhaPVP.getQuemAtaca();

                if (batalhaPVP.getPlayer1().getNick().equals(CharOn.character.getNick())) {
                    CharOn.character = batalhaPVP.getPlayer1();
                    Oponente.oponente = batalhaPVP.getPlayer2();
                } else {
                    CharOn.character = batalhaPVP.getPlayer2();
                    Oponente.oponente = batalhaPVP.getPlayer1();
                }

                if (iniciarBatalha) {
                    primeiroAtacar = quemAtaca;
                    iniciarBatalha = false;
                    carregarOponente();
                }

                if (batalhaPVP.getNumeroDeQuemAtaca() == 2) { // realizar calcula do ataque
                    countDownTimer.cancel();
//                    realizarAtaques(PersonagemOn.character.getJutsuSelecionado(), Oponente.oponente.getJutsuSelecionado());
                    atualizarLuta();
                    atualizarStatusLuta();
                    configurarMsgDoResultadoDaBatalha();
                    configurarPlayersNaBatalha();
                    batalhaPVP.setNumeroDeQuemAtaca(0);
                    batalhaPVP.setQuemAtaca(primeiroAtacar);
                    batalhaPvpReference.setValue(batalhaPVP);
                }

                startTimer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void carregarOponente() {
//        oponenteFormulas = Oponente.oponente.getAtributos().getFormulas();
//
//        statusOpoImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                exibirStatus(v, oponenteFormulas);
//            }
//        });
//
//        StorageUtil.downloadProfile(getActivity(), profileOpoImageView, Oponente.oponente.getNinja().getId(), Oponente.oponente.getProfile());
//
//        nickOpoTextView.setText(Oponente.oponente.getNick());
//        graduELvlOpoTextView.setText(String.format(Locale.getDefault(), "%s - Lvl %d", Oponente.oponente.getGraducao(), Oponente.oponente.getLevel()));
//
//        hpOpoProgressBar.setMax(oponenteFormulas.getCurrentHealth());
//        chakraOpoProgressBar.setMax(oponenteFormulas.getChakraAtual());
//        staminaOpoProgressBar.setMax(oponenteFormulas.getStaminaAtual());
    }

    private void configurarMsgDoResultadoDaBatalha() {
        if (LUTA_STATUS != Status.CONTINUA) {

            if (LUTA_STATUS == Status.GANHOU) {
                titleMsg.setText("COMBATE ENCERRADO!");
                messageMsg.setText("Parabéns! Você venceu o combate!Como recompensa você estará recebendo RY$ 134 e 0 pontos de experiência");
                textoParaLinkMsg.setText("Voltar ao ");
                linkMsg.setText("Dojo");
                linkMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeToFragment(new DojoFragment());
                    }
                });
            } else if (LUTA_STATUS == Status.PERDEU) {
                titleMsg.setText("QUE PENA!");
                messageMsg.setText("Você perdeu o combate.");
                textoParaLinkMsg.setText("Ir para o ");
                linkMsg.setText("Hospital");
                linkMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeToFragment(new HospitalQuartoFragment());
                    }
                });
            } else if (LUTA_STATUS == Status.EMPATOU) {
                titleMsg.setText("EMPATES!");
                messageMsg.setText("Por ambos estarem muito cansados, o combate terminou em empate!");
                textoParaLinkMsg.setText("Ir para o ");
                linkMsg.setText("Hospital");
                linkMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeToFragment(new HospitalQuartoFragment());
                    }
                });
            }

            finalizarLuta();
        }
    }

    private void configurarPlayersNaBatalha() {
        if (souPlayer1) {
            batalhaPVP.setPlayer1(CharOn.character);
            batalhaPVP.setPlayer2(Oponente.oponente);
        } else {
            batalhaPVP.setPlayer1(Oponente.oponente);
            batalhaPVP.setPlayer2(CharOn.character);
        }
    }


    private void realizarAtaques(Jutsu acaoPlayer, Jutsu acaoNpc) {
        int ataquePlayer = 0;
        int ataqueNpc = 0;

        if (player.getClasse().equals("Ninjutsu") || player.getClasse().equals("Genjutsu")) {
            if (acaoPlayer.getTipo().equals("atk") && acaoNpc.getTipo().equals("atk")) {
                ataquePlayer = (acaoPlayer.getAtkNinGen() + playerFormulas.getAtkNinGen()) - oponenteFormulas.getDefNinGen();
                ataqueNpc = (acaoNpc.getAtkNinGen() + oponenteFormulas.getAtkNinGen()) - playerFormulas.getDefNinGen();
            } else if (acaoPlayer.getTipo().equals("atk") && acaoNpc.getTipo().equals("def"))
                ataquePlayer = (playerFormulas.getAtkNinGen() + acaoPlayer.getAtkNinGen()) - (oponenteFormulas.getDefNinGen() - acaoNpc.getDefesaBase());
            else if (acaoPlayer.getTipo().equals("def") && acaoNpc.getTipo().equals("atk"))
                ataqueNpc = (oponenteFormulas.getAtkNinGen() + acaoPlayer.getAtkNinGen()) - (playerFormulas.getDefNinGen() - acaoPlayer.getDefesaBase());
        } else {
            if (acaoPlayer.getTipo().equals("atk") && acaoNpc.getTipo().equals("atk")) {
                ataquePlayer = (acaoPlayer.getAtkTaiBuk() + playerFormulas.getAtkTaiBuki()) - oponenteFormulas.getDefTaiBuki();
                ataqueNpc = (acaoNpc.getAtkTaiBuk() + oponenteFormulas.getAtkTaiBuki()) - playerFormulas.getDefTaiBuki();
            } else if (acaoPlayer.getTipo().equals("atk") && acaoNpc.getTipo().equals("def"))
                ataquePlayer = (playerFormulas.getAtkTaiBuki() + acaoPlayer.getAtkTaiBuk()) - (oponenteFormulas.getDefTaiBuki() - acaoNpc.getDefesaBase());
            else if (acaoPlayer.getTipo().equals("def") && acaoNpc.getTipo().equals("atk"))
                ataqueNpc = (oponenteFormulas.getAtkTaiBuki() + acaoPlayer.getAtkTaiBuk()) - (playerFormulas.getDefTaiBuki() - acaoPlayer.getDefesaBase());
        }

        playerFormulas.setCurrentHealth(playerFormulas.getCurrentHealth() - ataqueNpc);
        playerFormulas.setChakraAtual(playerFormulas.getChakraAtual() - acaoPlayer.getConsomeChakra());
        playerFormulas.setStaminaAtual(playerFormulas.getStaminaAtual() - acaoPlayer.getConsomeStamina());

        oponenteFormulas.setCurrentHealth(oponenteFormulas.getCurrentHealth() - ataquePlayer);
        oponenteFormulas.setChakraAtual(oponenteFormulas.getChakraAtual() - acaoNpc.getConsomeChakra());
        oponenteFormulas.setStaminaAtual(oponenteFormulas.getStaminaAtual() - acaoNpc.getConsomeStamina());
    }

    private void atualizarLuta() {
        hpPlayerProgressBar.setProgress(playerFormulas.getCurrentHealth());
        hpPlayerTextView.setText(String.format(Locale.getDefault(), "Vida: %d", playerFormulas.getCurrentHealth()));
        chakraPlayerProgressBar.setProgress(playerFormulas.getChakraAtual());
        chakraPlayerTextView.setText(String.format(Locale.getDefault(), "Chakra: %d", playerFormulas.getChakraAtual()));
        staminaPlayerProgressBar.setProgress(playerFormulas.getStaminaAtual());
        staminaPlayerTextView.setText(String.format(Locale.getDefault(), "Stamina: %d", playerFormulas.getStaminaAtual()));

        hpOpoProgressBar.setProgress(oponenteFormulas.getCurrentHealth());
        hpOpoTextView.setText(String.format(Locale.getDefault(), "Vida: %d", oponenteFormulas.getCurrentHealth()));
        chakraOpoProgressBar.setProgress(oponenteFormulas.getChakraAtual());
        chakraOpoTextView.setText(String.format(Locale.getDefault(), "Chakra: %d", oponenteFormulas.getChakraAtual()));
        staminaOpoProgressBar.setProgress(oponenteFormulas.getStaminaAtual());
        staminaOpoTextView.setText(String.format(Locale.getDefault(), "Stamina: %d", oponenteFormulas.getStaminaAtual()));
    }

    private void atualizarStatusLuta() {
        if ((oponenteFormulas.getCurrentHealth() < 10 || oponenteFormulas.getChakraAtual() < 10 || oponenteFormulas.getStaminaAtual() < 10)
                && playerFormulas.getCurrentHealth() < 10 || playerFormulas.getChakraAtual() < 10 || playerFormulas.getStaminaAtual() < 10)
            LUTA_STATUS = Status.EMPATOU;
        else if (oponenteFormulas.getCurrentHealth() < 10 || oponenteFormulas.getChakraAtual() < 10 || oponenteFormulas.getStaminaAtual() < 10)
            LUTA_STATUS = Status.GANHOU;
        else if (playerFormulas.getCurrentHealth() < 10 || playerFormulas.getChakraAtual() < 10 || playerFormulas.getStaminaAtual() < 10)
            LUTA_STATUS = Status.PERDEU;
        else
            LUTA_STATUS = Status.CONTINUA;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(TEMPO_PARA_ATACAR, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutos = (int) millisUntilFinished / 1000 / 60;
                int segundos = (int) millisUntilFinished / 1000 % 60;

                if (quemAtaca.equals(CharOn.character.getNick())) {
                    countDownTimerTextView.setTextColor(Color.RED);
                    vezDeQuemTextView.setText("Aguardando a sua ação");
                } else {
                    countDownTimerTextView.setTextColor(Color.GREEN);
                    vezDeQuemTextView.setText("Aguardando a ação do oponente");
                }

                countDownTimerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos));
            }

            @Override
            public void onFinish() {
                countDownTimerTextView.setText("--:--");

                if (LUTA_STATUS == Status.CONTINUA) {
                    playerFormulas.setCurrentHealth(0);
                    hpPlayerProgressBar.setProgress(0);
                    hpPlayerTextView.setText("Vida: 0");

                    titleMsg.setText("OPS!");
                    messageMsg.setText("Por ficar muito tempo sem efetuar nenhuma ação, você perdeu o combate!");
                    textoParaLinkMsg.setText("Ir para o ");
                    linkMsg.setText("Hospital");
                    linkMsg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changeToFragment(new HospitalQuartoFragment());
                        }
                    });

                    LUTA_STATUS = Status.PERDEU;
                    finalizarLuta();
                }
            }
        }.start();
    }

    private void finalizarLuta() {
        Oponente.oponente = null;
        meusJutsusRecyclerView.setVisibility(View.GONE);
        msgLinear2.setVisibility(View.VISIBLE);

        if (LUTA_STATUS == Status.GANHOU) {
            CharOn.character.getCombatOverview().setWinsDojoPvp(CharOn.character.getCombatOverview().getWinsDojoPvp() + 1);
            CharOn.character.setExp(CharOn.character.getExp() + 315);
        } else if (LUTA_STATUS == Status.PERDEU)
            CharOn.character.getCombatOverview().setLossesDojoPvp(CharOn.character.getCombatOverview().getLossesDojoPvp() + 1);

        CharOn.character.salvar();
    }

    private void exibirStatus(View view, Formulas formulas) {
        PopupMenu menu = new PopupMenu(getActivity(), view);

        menu.getMenu().add("Resumo dos Atributos");
        menu.getMenu().add("Ataque ( Tai / Buk ): %d");
        menu.getMenu().add("Ataque ( Nin / Gen ): %d");
        menu.getMenu().add("Defesa ( Tai / Buk ): %d");
        menu.getMenu().add("Defesa ( Nin / Gen ): %d");
        menu.getMenu().add("Precisão: %d");
        menu.getMenu().add("Determinação: %d %%");
        menu.getMenu().add("Percepção: %d %% ( 0 %% - 0 %% ) ");
        menu.getMenu().add("Incremento de Absorção: 11.25%%");
        menu.getMenu().add("Concentração: %d %% ( 0 %% - 0 %% )");
        menu.getMenu().add("Incremento de Crítico: 11.25%%");
        menu.getMenu().add("Esquiva: %,.2f%");
                /*
                 oponenteFormulas.getAtkTaiBuki(), oponenteFormulas.getAtkNinGen(),
                        oponenteFormulas.getDefTaiBuki(), oponenteFormulas.getDefNinGen(),
                        oponenteFormulas.getAccuracy(), oponenteFormulas.getDeterminacao(),
                        oponenteFormulas.getConviccao(), oponenteFormulas.getPercepcao(),
                        oponenteFormulas.getConcentracao(), oponenteFormulas.getEsquiva()
                 */
        menu.show();
    }

    @SuppressLint("RestrictedApi")
    private void exibirJutsu(View view, int position) {
        MenuBuilder menuBuilder = new MenuBuilder(getActivity());
        MenuInflater menuInflater = new MenuInflater(getActivity());
        menuInflater.inflate(R.menu.pop_menu_jutsu, menuBuilder);

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(getActivity(), menuBuilder, view);
        menuPopupHelper.setForceShowIcon(true);
//
//        Jutsu jutsu = PersonagemOn.character.getJutsus().get(position);
//
//        menuBuilder.findItem(R.id.nomeJutsu).setTitle(String.format("%s       x%d", jutsu.getDescription(), jutsu.getTotal()));
//
//        String atkOuDefJutsu;
//        if (PersonagemOn.character.getClasse().equals("Ninjutsu") || PersonagemOn.character.getClasse().equals("Genjutsu")) {
//            if (jutsu.getTipo().equals("atk")) {
//                atkOuDefJutsu = "Atk (Nin / Gen) : " + jutsu.getAtkNinGen();
//                menuBuilder.findItem(R.id.atkOuDefJutsu).setIcon(R.drawable.layout_icones_atk_magico);
//            } else {
//                atkOuDefJutsu = "Def (Nin / Gen) : " + jutsu.getDefesaBase();
//                menuBuilder.findItem(R.id.atkOuDefJutsu).setIcon(R.drawable.layout_icones_defense);
//            }
//        } else {
//            if (jutsu.getTipo().equals("atk")) {
//                atkOuDefJutsu = "Atk (Tai / Buk) : " + jutsu.getAtkNinGen();
//                menuBuilder.findItem(R.id.atkOuDefJutsu).setIcon(R.drawable.layout_icones_atk_fisico);
//            } else {
//                atkOuDefJutsu = "Def (Tai/ Buk) : " + jutsu.getDefesaBase();
//                menuBuilder.findItem(R.id.atkOuDefJutsu).setIcon(R.drawable.layout_icones_defense);
//            }
//        }

//        menuBuilder.findItem(R.id.atkOuDefJutsu).setTitle(atkOuDefJutsu);
//        menuBuilder.findItem(R.id.precisaoJutsu).setTitle(String.format("Precisão: %d%%", jutsu.getAccuracy()));
//        menuBuilder.findItem(R.id.intervaloJutsu).setTitle(String.format("Intervalo de uso: %d turno(s)", jutsu.getIntervaloDeUso()));
//        menuBuilder.findItem(R.id.consomeChakraJutsu).setTitle(String.format("Consome: %d", jutsu.getConsomeChakra()));
//        menuBuilder.findItem(R.id.consomeStaminaJutsu).setTitle(String.format("Consome: %d", jutsu.getConsomeStamina()));
//        menuPopupHelper.show();
    }

    private void tocarSom() {
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.crystal);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }

    private void changeToFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
