package com.gutotech.narutogame.data.repository;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.gutotech.narutogame.data.firebase.FirebaseConfig;
import com.gutotech.narutogame.data.model.Player;

public class PlayerRepository {
    private static final PlayerRepository sInstance = new PlayerRepository();

    private PlayerRepository() {
    }

    public static PlayerRepository getInstance() {
        return sInstance;
    }

    public void savePlayer(Player player) {
        DatabaseReference playerRef = FirebaseConfig.getDatabase()
                .child("players")
                .child(player.getId());

        playerRef.setValue(player);
    }

    public void getCurrentPlayer(Callback<Player> callBack) {
        DatabaseReference playerReference = FirebaseConfig.getDatabase()
                .child("players")
                .child(AuthRepository.getInstance().getUid());

        playerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Player player = dataSnapshot.getValue(Player.class);
                callBack.call(player);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private boolean mSignInResult;

    public void setSignedIn(boolean signIn, Callback<Boolean> callback) {
        DatabaseReference playerReference = FirebaseConfig.getDatabase()
                .child("players")
                .child(AuthRepository.getInstance().getUid())
                .child("signedIn");

        playerReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    return Transaction.success(mutableData);
                }

                boolean isSignIn = (boolean) mutableData.getValue();

                if (signIn && isSignIn) {
                    mSignInResult = false;
                } else {
                    mSignInResult = true;
                    mutableData.setValue(signIn);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b,
                                   @Nullable DataSnapshot dataSnapshot) {
                if (callback != null) {
                    callback.call(mSignInResult);
                }
            }
        });
    }

    public void updatePassword(String newPassword) {
        DatabaseReference playerReference = FirebaseConfig.getDatabase()
                .child("players")
                .child(AuthRepository.getInstance().getUid())
                .child("password");

        playerReference.setValue(newPassword);
    }

    public void isValidCurrentPassword(String currentPassword, Callback<Boolean> callback) {
        DatabaseReference playerRef = FirebaseConfig.getDatabase()
                .child("players")
                .child(AuthRepository.getInstance().getUid())
                .child("password");

        playerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String password = dataSnapshot.getValue(String.class);

                if (TextUtils.equals(password, currentPassword)) {
                    callback.call(true);
                } else {
                    callback.call(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getTotalCharacters(Callback<Integer> callback) {
        DatabaseReference reference = FirebaseConfig.getDatabase()
                .child("players")
                .child(AuthRepository.getInstance().getUid())
                .child("totalCharacters");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    callback.call(dataSnapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void setTotalCharacters(boolean increment) {
        DatabaseReference reference = FirebaseConfig.getDatabase()
                .child("players")
                .child(AuthRepository.getInstance().getUid())
                .child("totalCharacters");

        reference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer total = mutableData.getValue(Integer.class);

                if (total == null) {
                    return Transaction.success(mutableData);
                }

                if (increment) {
                    mutableData.setValue(total + 1);
                } else {
                    mutableData.setValue(total - 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b,
                                   @Nullable DataSnapshot dataSnapshot) {
            }
        });
    }
}
