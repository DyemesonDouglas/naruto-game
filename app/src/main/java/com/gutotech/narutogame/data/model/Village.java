package com.gutotech.narutogame.data.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.gutotech.narutogame.R;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public enum Village implements Serializable {
    FOLHA(R.string.leaf, R.drawable.layout_home_kages_1, R.drawable.layout_bandanas_1,
            R.drawable.layout_mapa_1, Arrays.asList(34, 47, 67, 75, 98)),
    AREIA(R.string.sand, R.drawable.layout_home_kages_2, R.drawable.layout_bandanas_2,
            R.drawable.layout_mapa_2, Arrays.asList(25, 54, 63, 68, 98)),
    NEVOA(R.string.mist, R.drawable.layout_home_kages_3, R.drawable.layout_bandanas_3,
            R.drawable.layout_mapa_3, Arrays.asList(42, 58, 81, 94, 98)),
    PEDRA(R.string.stone, R.drawable.layout_home_kages_4, R.drawable.layout_bandanas_4,
            R.drawable.layout_mapa_4, Arrays.asList(33, 62, 68, 85, 98)),
    NUVEM(R.string.cloud, R.drawable.layout_home_kages_5, R.drawable.layout_bandanas_5,
            R.drawable.layout_mapa_5, Arrays.asList(31, 65, 68, 72, 98)),
    AKATSUKI(R.string.akatsuki, R.drawable.layout_home_kages_6, R.drawable.layout_bandanas_6,
            R.drawable.layout_mapa_6, Arrays.asList(48, 54, 78, 95, 98)),
    SOM(R.string.sound, R.drawable.layout_home_kages_7, R.drawable.layout_bandanas_7,
            R.drawable.layout_mapa_7, Arrays.asList(33, 36, 62, 65, 98)),
    CHUVA(R.string.rain, R.drawable.layout_home_kages_8, R.drawable.layout_bandanas_8,
            R.drawable.layout_mapa_8, Arrays.asList(32, 36, 48, 74, 98, 102)),

    NEVE(R.string.snow, R.drawable.layout_home_kages_1, R.drawable.layout_bandanas_1,
            R.drawable.layout_map_9, null),
    CACHOEIRA(R.string.waterfall, R.drawable.layout_home_kages_1, R.drawable.layout_bandanas_1,
            R.drawable.layout_map_10, null),
    FONTES_TERMAIS(R.string.hot_springs, R.drawable.layout_home_kages_1, R.drawable.layout_bandanas_1,
            R.drawable.layout_map_11, null),
    GRAMA(R.string.grass, R.drawable.layout_home_kages_1, R.drawable.layout_bandanas_1,
            R.drawable.layout_map_12, null);

    @StringRes
    public final int name;

    @DrawableRes
    public final int homeResId;

    @DrawableRes
    public final int bandanaResId;

    @DrawableRes
    public final int mapResId;

    public final List<Integer> placeEntries;

    Village(@StringRes int name, @DrawableRes int homeResId, @DrawableRes int bandanaResId,
            @DrawableRes int mapResId, List<Integer> placeEntries) {
        this.name = name;
        this.homeResId = homeResId;
        this.bandanaResId = bandanaResId;
        this.mapResId = mapResId;
        this.placeEntries = placeEntries;
    }

    @StringRes
    public int getName() {
        return name;
    }
}
