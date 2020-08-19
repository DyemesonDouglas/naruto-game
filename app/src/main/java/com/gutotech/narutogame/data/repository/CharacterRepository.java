package com.gutotech.narutogame.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.gutotech.narutogame.data.firebase.FirebaseConfig;
import com.gutotech.narutogame.data.model.Character;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CharacterRepository {
    private static final CharacterRepository sInstance = new CharacterRepository();

    private CharacterRepository() {
    }

    public static CharacterRepository getInstance() {
        return sInstance;
    }

    public void save(Character character) {
        DatabaseReference characterRef = FirebaseConfig.getDatabase()
                .child("characters")
                .child(character.getId());

        characterRef.setValue(character);
    }

    public void remove(String id) {
        DatabaseReference characterRef = FirebaseConfig.getDatabase()
                .child("characters")
                .child(id);

        characterRef.removeValue();
    }

    public void getChar(String id, Callback<Character> callback) {
        DatabaseReference reference = FirebaseConfig.getDatabase()
                .child("characters")
                .child(id);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.call(dataSnapshot.getValue(Character.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void checkByRepeatedNick(String nick, Callback<Boolean> callback) {
        DatabaseReference charactersRef = FirebaseConfig.getDatabase()
                .child("characters");

        Query query = charactersRef.orderByChild("nick").equalTo(nick);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
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

    public void getAllCharacters(String playerId, Callback<List<Character>> callback) {
        Query charactersQuery = FirebaseConfig.getDatabase()
                .child("characters")
                .orderByChild("playerId")
                .equalTo(playerId);

        charactersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Character> characterList = new ArrayList<>();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Character character = data.getValue(Character.class);
                    characterList.add(character);
                }

                sortByLevel(characterList);

                callback.call(characterList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void setLastSeen(String charid) {
        FirebaseConfig.getDatabase()
                .child("characters")
                .child(charid)
                .child("lastSeenInMillis")
                .setValue(ServerValue.TIMESTAMP);
    }

    public void sortByLevel(List<Character> characterList) {
        Collections.sort(characterList, (char1, char2) -> {
            if (char1.getLevel() == char2.getLevel()) {
                return 0;
            }

            return char1.getLevel() > char2.getLevel() ? -1 : 1;
        });
    }

    public void sortByScore(List<Character> characterList) {
        Collections.sort(characterList, (char1, char2) -> {
            if (char1.getScore() == char2.getScore()) {
                return 0;
            }

            return char1.getScore() > char2.getScore() ? -1 : 1;
        });
    }

    public void sortByVillage(List<Character> charactersList) {
        Collections.sort(charactersList, (char1, char2) -> {
            if (char1.getVillage().ordinal() == char2.getVillage().ordinal()) {
                return 0;
            }

            return char1.getVillage().ordinal() > char2.getVillage().ordinal() ? 1 : -1;
        });
    }
}
