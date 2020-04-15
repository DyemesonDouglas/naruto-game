package com.gutotech.narutogame.utils;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.annotation.RawRes;

public class MediaPlayerUtil {

    public static void play(Context context, @RawRes int resId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, resId);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
    }

}
