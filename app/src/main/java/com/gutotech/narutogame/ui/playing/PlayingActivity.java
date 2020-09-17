package com.gutotech.narutogame.ui.playing;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.gutotech.narutogame.R;
import com.gutotech.narutogame.data.model.Battle;
import com.gutotech.narutogame.data.model.CharOn;
import com.gutotech.narutogame.data.model.Title;
import com.gutotech.narutogame.data.network.NetworkUtils;
import com.gutotech.narutogame.data.repository.AuthRepository;
import com.gutotech.narutogame.databinding.ActivityPlayingBinding;
import com.gutotech.narutogame.databinding.DialogGameRoutinesBinding;
import com.gutotech.narutogame.databinding.NavHeaderPlayingBinding;
import com.gutotech.narutogame.ui.WarningDialogFragment;
import com.gutotech.narutogame.ui.adapter.BagItemsAdapter;
import com.gutotech.narutogame.ui.adapter.ChatMessagesAdapter;
import com.gutotech.narutogame.ui.adapter.ExpandableListLoggedinAdapter;
import com.gutotech.narutogame.ui.home.HomeActivity;
import com.gutotech.narutogame.utils.FragmentUtils;
import com.gutotech.narutogame.utils.SettingsUtils;
import com.gutotech.narutogame.utils.SoundUtil;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayingActivity extends AppCompatActivity implements
        WarningDialogFragment.WarningDialogListener {
    private ActivityPlayingBinding mBinding;
    private PlayingViewModel mViewModel;
    private YoYo.YoYoString mRope;
    private boolean mInitialization = true;
    private List<Map<String, String>> mAlerts = new ArrayList<>();
    private WarningDialogFragment mConnectionWarningDialog;
    private static final int CONNECTION_WARNING_REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = (float) 1; // 0.85 small size, 1 normal size, 1,15 big etc
        DisplayMetrics metrics = new DisplayMetrics();
        getDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        configuration.densityDpi = (int) getResources().getDisplayMetrics().xdpi;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);

        mViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(PlayingViewModel.class);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_playing);
        mBinding.setLifecycleOwner(this);
        mBinding.setViewModel(mViewModel);

        NavHeaderPlayingBinding navHeaderBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.nav_header_playing, mBinding.navView, false);
        mBinding.navView.addHeaderView(navHeaderBinding.getRoot());
        navHeaderBinding.setViewModel(mViewModel);

        ExpandableListLoggedinAdapter menuAdapter = new ExpandableListLoggedinAdapter();
        mBinding.expandableListView.setAdapter(menuAdapter);
        mViewModel.getMenuGroups().observe(this, menuAdapter::setMenuGroups);
        mBinding.expandableListView.setOnChildClickListener(mViewModel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mBinding.drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mViewModel.getCurrentSection().observe(this, sectionFragment -> {
            FragmentUtils.goTo(this, (Fragment) sectionFragment);
            closeDrawer();
            if (!mInitialization) {
                SoundUtil.play(getApplicationContext(), R.raw.sound_btn06);
            } else {
                mInitialization = false;
            }
        });

        mBinding.setTitles(Title.values());

        mViewModel.getTitles().observe(this, titleIndices -> {
            List<String> titleList = new ArrayList<>();

            Title[] titles = Title.values();

            for (int titleIndex : titleIndices) {
                titleList.add(getString(titles[titleIndex].getText()));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, titleList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mBinding.titlesSpinner.setAdapter(adapter);
            mBinding.titlesSpinner.setSelection(mViewModel.getCharacter().getSelectedTitle());
        });

        mViewModel.getShowWarningDialogEvent().observe(this, resid -> {
            WarningDialogFragment dialog = WarningDialogFragment.newInstance(this, resid);
            dialog.openDialog(getSupportFragmentManager());
        });

        mViewModel.getFidelityAnimationEvent().observe(this, running -> {
            if (running) {
                mRope = YoYo.with(Techniques.RubberBand)
                        .duration(1200)
                        .repeat(YoYo.INFINITE)
                        .playOn(navHeaderBinding.hasFidelityRewardImageButton);
            } else if (mRope != null) {
                mRope.stop();
            }
        });

        mViewModel.getShowAlerterEvent().observe(this, messageMap -> {
            mAlerts.add(messageMap);
            if (!Alerter.isShowing()) {
                showAlert(messageMap);
            }
        });

        mConnectionWarningDialog = WarningDialogFragment.newInstance(
                this, R.string.communication_error,
                R.string.communication_error_description,
                R.string.ok, CONNECTION_WARNING_REQUEST_CODE);

        mViewModel.getShowConnectionWarning().observe(this, connected -> {
            if (connected) {
                if (mConnectionWarningDialog.isVisible()) {
                    mConnectionWarningDialog.dismiss();
                }
            } else {
                mConnectionWarningDialog.show(getSupportFragmentManager());
            }
        });

        setUpChat();
        setUpBag();
    }

    @Override
    public void onCloseClick(int requestCode) {
        if (requestCode == CONNECTION_WARNING_REQUEST_CODE) {
            if (!NetworkUtils.CONNECTED) {
                mConnectionWarningDialog.show(getSupportFragmentManager());
            }
        }
    }

    private void showAlert(Map<String, String> messageMap) {
        String message = "";
        String type = messageMap.get("type");

        if (type == null) {
            return;
        }

        switch (type) {
            case "warning":
                message = messageMap.get("warning");
                break;
            case "kage_defeated":
                message = getString(R.string.kage_defeated_alert,
                        getString(Integer.parseInt(messageMap.get("kageTitle"))),
                        messageMap.get("kageName"),
                        messageMap.get("player"));
                break;
            case "ninja_lucky":
                message = getString(R.string.lucky_ninja_alert,
                        messageMap.get("player"),
                        getString(Integer.parseInt(messageMap.get("premium"))));
                break;
        }

        Alerter.create(PlayingActivity.this)
                .setTitle(message)
                .setBackgroundColorRes(R.color.colorGold)
                .enableSwipeToDismiss()
                .setDuration(4000)
                .setOnHideListener(() -> {
                    mAlerts.remove(messageMap);
                    if (mAlerts.size() > 0) {
                        showAlert(mAlerts.get(0));
                    }
                })
                .hideIcon()
                .show();
    }

    public void showGameRoutines(View view) {
        Dialog routinesDialog = new Dialog(this);
        routinesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DialogGameRoutinesBinding binding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.dialog_game_routines, null, false);
        binding.setViewModel(mViewModel);

        routinesDialog.setContentView(binding.getRoot());
        routinesDialog.show();
        SoundUtil.play(getApplicationContext(), R.raw.sound_pop);
    }

    public void onSettingsClick(View view) {
        DialogFragment dialog = SettingsDialogFragment.getInstance(mViewModel::checkForSettingsChanged);
        dialog.show(getSupportFragmentManager(), "SettingsDialogFragment");
        SoundUtil.play(getApplicationContext(), R.raw.sound_pop);
    }

    private Dialog mBagDialog;

    public void showBag(View view) {
        mViewModel.updateBag();
        mBagDialog.show();
        SoundUtil.play(getApplicationContext(), R.raw.sound_pop);
    }

    private void setUpBag() {
        mBagDialog = new Dialog(this);
        mBagDialog.setContentView(R.layout.dialog_bag);
        mBagDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView ramensRecyclerView = mBagDialog.findViewById(R.id.ramensRecyclerView);
        ramensRecyclerView.setHasFixedSize(true);
        BagItemsAdapter ramensAdapter = new BagItemsAdapter(this,
                mViewModel.onRamenClickListener);
        ramensRecyclerView.setAdapter(ramensAdapter);

        mViewModel.getRamens().observe(this, ramens -> {
            LinearLayout ramensLinearLayout = mBagDialog.findViewById(R.id.ramensLinearLayout);

            if (ramens != null) {
                ramensAdapter.setItems(new ArrayList<>(ramens));
                ramensLinearLayout.setVisibility(View.VISIBLE);
            } else {
                ramensLinearLayout.setVisibility(View.GONE);
            }
        });

        RecyclerView scrollsRecyclerView = mBagDialog.findViewById(R.id.scrollsRecyclerView);
        scrollsRecyclerView.setHasFixedSize(true);
        BagItemsAdapter scrollsAdapter = new BagItemsAdapter(this,
                mViewModel.onScrollClickListener);
        scrollsRecyclerView.setAdapter(scrollsAdapter);

        mViewModel.getScrolls().observe(this, scrolls -> {
            LinearLayout scrollsLinearLayout = mBagDialog.findViewById(R.id.scrollsLinearLayout);

            if (scrolls != null) {
                scrollsAdapter.setItems(new ArrayList<>(scrolls));
                scrollsLinearLayout.setVisibility(View.VISIBLE);
            } else {
                scrollsLinearLayout.setVisibility(View.GONE);
            }
        });

        mViewModel.getDismissBagDialog().observe(this, aVoid -> mBagDialog.dismiss());
    }

    private void setUpChat() {
        updateChatChannels();

        mViewModel.getUpdateChannelsEvent().observe(this, aVoid -> updateChatChannels());

        mViewModel.getStartChatAnimationEvent().observe(this, openChat -> {
            Animation animation;

            if (openChat) {
                animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                mBinding.scrollView.postDelayed(() ->
                        mBinding.scrollView.smoothScrollTo(0,
                                mBinding.scrollView.getBottom() + 1000), 500);
            } else {
                animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
            }

            mBinding.chatTopImageView.startAnimation(animation);
        });

        mBinding.messagesRecyclerView.setHasFixedSize(true);
        ChatMessagesAdapter adapter = new ChatMessagesAdapter();
        mBinding.messagesRecyclerView.setAdapter(adapter);
        mViewModel.getChatMessages().observe(this, messages -> {
            adapter.setMessageList(messages);
            mBinding.messagesRecyclerView.smoothScrollToPosition(adapter.getItemCount());
        });

        mBinding.messageEditText.setOnEditorActionListener((v, actionId, event) -> {
            mViewModel.onSendMessageButtonPressed();
            return true;
        });
    }

    private void updateChatChannels() {
        List<String> channels = new ArrayList<>();
        channels.add(getString(R.string.world));
        channels.add(getString(CharOn.character.getVillage().name));
        if (!TextUtils.isEmpty(CharOn.character.getTeam())) {
            channels.add(getString(R.string.team));
        }
        if (CharOn.character.isBattle() &&
                (CharOn.character.getBattleId().contains(Battle.MAP_PVP) ||
                        CharOn.character.getBattleId().contains(Battle.DOJO_PVP))) {
            channels.add(getString(R.string.batalha));
        }

        ArrayAdapter<String> channelsAdapter = new ArrayAdapter<>(
                this, R.layout.simple_spinner_item, channels);
        channelsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_chat);
        mBinding.channelSpinner.setAdapter(channelsAdapter);
        mBinding.channelSpinner.setSelection(1);
    }


    public void onLogoutClick(View view) {
        mViewModel.logout();
        GoogleSignIn.getClient(
                this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();

        startActivity(new Intent(PlayingActivity.this, HomeActivity.class));
        finish();
    }


    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetectorCompat mGestureDetector;

    public void registerScaleGestureDetector(ScaleGestureDetector scaleGestureDetector) {
        mScaleGestureDetector = scaleGestureDetector;
    }

    public void registerGestureListener(GestureDetectorCompat gestureDetector) {
        mGestureDetector = gestureDetector;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mScaleGestureDetector != null) {
            mScaleGestureDetector.onTouchEvent(event);
        }

        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mScaleGestureDetector != null) {
            mScaleGestureDetector.onTouchEvent(event);
        }

        if (mGestureDetector != null && !mBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
            mGestureDetector.onTouchEvent(event);
        }

        return super.dispatchTouchEvent(event);
    }


    public void closeDrawer() {
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        boolean showHomeTour = SettingsUtils.get(this, SettingsUtils.HOME_TOUR_KEY);

        if (showHomeTour) {
            SettingsUtils.set(this, SettingsUtils.HOME_TOUR_KEY, false);

            Toolbar toolbar = findViewById(R.id.toolbar);
            TapTarget firstTapTarget = TapTarget.forToolbarNavigationIcon(toolbar,
                    getString(R.string.tour_home_open_the_nav_drawer),
                    getString(R.string.tour_home_open_the_nav_drawer_desc))
                    .id(1)
                    .transparentTarget(true)
                    .outerCircleAlpha(0.96f)
                    .cancelable(false);

            TapTarget welcomeTapTarget = TapTarget.forView(mBinding.dummyView,
                    getString(R.string.tour_welcome),
                    getString(R.string.tour_welcome_desc))
                    .id(2)
                    .targetRadius(150)
                    .transparentTarget(true)
                    .cancelable(false)
                    .outerCircleAlpha(0.96f);

            TapTarget gradesTapTarget = TapTarget.forView(mBinding.expandableListView,
                    getString(R.string.grades),
                    getString(R.string.tour_grades_desc))
                    .id(3)
                    .targetRadius(150)
                    .outerCircleAlpha(0.96f)
                    .cancelable(false);

            TapTarget getStartedTapTarget = TapTarget.forView(mBinding.expandableListView,
                    getString(R.string.tour_get_started_now),
                    getString(R.string.tour_get_started_now_desc))
                    .id(4)
                    .targetRadius(150)
                    .outerCircleAlpha(0.96f);

            TapTargetSequence sequence =
                    new TapTargetSequence(this)
                            .targets(
                                    firstTapTarget,
                                    welcomeTapTarget,
                                    gradesTapTarget,
                                    getStartedTapTarget
                            );

            sequence.listener(new TapTargetSequence.Listener() {
                @Override
                public void onSequenceFinish() {
                }

                @Override
                public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                    if (lastTarget.id() == 1) {
                        mBinding.drawerLayout.openDrawer(GravityCompat.START);
                    } else if (lastTarget.id() == 2) {
                        mBinding.expandableListView.expandGroup(PlayingViewModel.ACADEMY_GROUP);
                        mBinding.scrollView.scrollTo(0, mBinding.expandableListView.getScrollY() + 350);
                    } else if (lastTarget.id() == 3) {
                        mBinding.expandableListView.collapseGroup(PlayingViewModel.ACADEMY_GROUP);
                        mBinding.expandableListView.expandGroup(PlayingViewModel.CURRENT_VILLAGE_GROUP);
                        mBinding.expandableListView.expandGroup(PlayingViewModel.BATTLES_GROUP);
                        mBinding.scrollView.scrollTo(0, mBinding.expandableListView.getScrollY() + 700);
                    }
                }

                @Override
                public void onSequenceCanceled(TapTarget lastTarget) {
                }
            });

            sequence.start();
        }
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.start();
        if (!AuthRepository.getInstance().isSignedIn()) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewModel.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
    }
}
