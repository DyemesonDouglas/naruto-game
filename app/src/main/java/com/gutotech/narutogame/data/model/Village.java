package com.gutotech.narutogame.data.model;

import androidx.annotation.NonNull;

import com.gutotech.narutogame.R;

import java.io.Serializable;

public enum Village implements Serializable {
    FOLHA(1, "Folha", R.drawable.layout_home_kages_1, R.drawable.layout_bandanas_1, R.drawable.layout_mapa_1),
    AREIA(2, "Areia", R.drawable.layout_home_kages_2, R.drawable.layout_bandanas_2, R.drawable.layout_mapa_2),
    NEVOA(3, "Névoa", R.drawable.layout_home_kages_3, R.drawable.layout_bandanas_3, R.drawable.layout_mapa_3),
    PEDRA(4, "Pedra", R.drawable.layout_home_kages_4, R.drawable.layout_bandanas_4, R.drawable.layout_mapa_4),
    NUVEM(5, "Nuvem", R.drawable.layout_home_kages_5, R.drawable.layout_bandanas_5, R.drawable.layout_mapa_5),
    AKATSUKI(6, "Akatsuki", R.drawable.layout_home_kages_6, R.drawable.layout_bandanas_6, R.drawable.layout_mapa_6),
    SOM(7, "Som", R.drawable.layout_home_kages_7, R.drawable.layout_bandanas_7, R.drawable.layout_mapa_7),
    CHUVA(8, "Chuva", R.drawable.layout_home_kages_8, R.drawable.layout_bandanas_8, R.drawable.layout_mapa_8),

    NEVE(9, "Neve", R.drawable.layout_home_kages_1, R.drawable.layout_bandanas_1, R.drawable.layout_map_9),
    CACHOEIRA(10, "Cachoeira", R.drawable.layout_home_kages_1, R.drawable.layout_bandanas_1, R.drawable.layout_map_10),
    FONTES_TERMAIS(11, "Fontes Termais", R.drawable.layout_home_kages_1, R.drawable.layout_bandanas_1, R.drawable.layout_map_11),
    GRAMA(12, "Grama", R.drawable.layout_home_kages_1, R.drawable.layout_bandanas_1, R.drawable.layout_map_12);

    public final int id;
    public final String name;
    public final int homeResId;
    public final int bandanaResId;
    public final int mapResId;

    Village(int id, String name, int homeResId, int bandanaResId, int mapResId) {
        this.id = id;
        this.name = name;
        this.homeResId = homeResId;
        this.bandanaResId = bandanaResId;
        this.mapResId = mapResId;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
