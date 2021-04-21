package com.pcamarounds.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Developed by : Kamal Patidar
 * Author : Shanti Infotech Pvt. Ltd.
 * Email : kamal.shantiinfotech@gmail.com
 * Website : https://shantiinfotech.com/
 * Created on : 21,May,2020
 */
public class TimeAgo {
    public String covertTimeToText(String dataDate) {

        String convTime = null;

        String prefix = "";
       // String suffix = "hace";
        String suffix = "";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                // convTime = second + " Seconds " + suffix;
                convTime = " Hace 1 día "+ suffix;
            } else if (minute < 60) {
                //convTime = minute + " Minutes "+suffix;
                convTime = " Hace 1 día "+ suffix;
            } else if (hour < 24) {
                //convTime = hour + " Hours "+suffix;
                convTime = " Hace 1 día "+ suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = "Hace "+(day / 360) + " años " + suffix;
                } else if (day > 30) {
                    convTime = "Hace "+(day / 30) + " meses " + suffix;
                } else {
                    convTime = "Hace "+(day / 7) + " semana " + suffix;
                }
            } else if (day < 7) {
                convTime = "Hace "+day+" día "+suffix;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }
}
