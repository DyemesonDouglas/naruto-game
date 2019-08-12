package com.gutotech.narutogame.helper;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateCustom {

    public static String getData() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return simpleDateFormat.format(System.currentTimeMillis());
    }

    public static String getHorario() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return simpleDateFormat.format(System.currentTimeMillis());
    }

    public static String getHorario(long currentTimeMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return simpleDateFormat.format(currentTimeMillis);
    }

    public static int getHora() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.getDefault());
        return Integer.parseInt(simpleDateFormat.format(System.currentTimeMillis()));
    }

    public static int getMinuto() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm", Locale.getDefault());
        return Integer.parseInt(simpleDateFormat.format(System.currentTimeMillis()));
    }

    public static int getSegundo() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ss", Locale.getDefault());
        return Integer.parseInt(simpleDateFormat.format(System.currentTimeMillis()));
    }

    public static long getMillis() {
        return (long) getHora() * 3600000 + getMinuto() * 60000 + getSegundo() * 1000;
    }

    public static String mesAno(String data) {
        String[] tokens = data.split("/");
        return tokens[1] + tokens[2];
    }

    public static String dia() {
        return getData().split("/")[0];
    }
}
