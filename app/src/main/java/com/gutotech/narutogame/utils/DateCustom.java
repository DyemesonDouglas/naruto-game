package com.gutotech.narutogame.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateCustom {

    public static int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static long getTimeInMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return simpleDateFormat.format(System.currentTimeMillis());
    }

    public static String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return simpleDateFormat.format(System.currentTimeMillis());
    }

    public static String getTime(long currentTimeMillis) {
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
}
