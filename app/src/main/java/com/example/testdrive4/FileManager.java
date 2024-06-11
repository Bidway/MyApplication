package com.example.testdrive4;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class FileManager {
    public File getOutputMediaFile(Context context,double timeStamp) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        SharedPreferences sharedPreferencesID = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        timeStamp = System.currentTimeMillis();
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                sharedPreferencesID.getString("surname","")+"_"+timeStamp + ".jpg");

        SharedPreferences sharedPhoto = context.getSharedPreferences("Photo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPhoto.edit();
        editor.putString("Photo", mediaFile.getAbsolutePath());
        editor.apply();
        return mediaFile;
    }
    public void createCSVFile(Context context,String timeStamp,double[] dataGPS,String[] dataText) {

        try {
            SharedPreferences sharedStops = context.getSharedPreferences("Stops",Context.MODE_PRIVATE);
            SharedPreferences sharedPreferences = context.getSharedPreferences("Saves", Context.MODE_PRIVATE);
            SharedPreferences sharedPreferencesID = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Map<String, ?> allEntries = sharedPreferences.getAll();
            int sizeKeys = allEntries.size();
            editor.putString(String.valueOf(sizeKeys), sharedPreferencesID.getString("surname", "") + "_" + timeStamp);
            editor.apply();

            java.io.File csvFileDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS), "MyAppFolder");

            java.io.File csvFile = new java.io.File(csvFileDir.getPath() + java.io.File.separator +
                    sharedPreferencesID.getString("surname", "") + "_" + timeStamp + ".csv");
            if (!csvFile.exists()) {
                csvFile.createNewFile();
                FileWriter writer = new FileWriter(csvFile);
                writer.append("Время" + "," + "Индетификатор" + "," + "Название остановки" + "," + "Название следующей остановки " + "," + "GPS-широта" + "," + "GPS-долгота" + "," +
                        "Высота места над уровнем моря" + "," + "Число пассажиров на остановке" + "," + "Номер маршрута транспортного средства" +
                        "," + "Тип транспорта" + "," + "Степень заполненности транспортного средства" + "," + "Число вошедших пассажиров" + "," + "Число вышедших пассажиров" + "\n");
                writer.append(timeStamp).append(",").append(sharedPreferencesID.getString("surname", "")).append(",").
                        append(sharedStops.getString("stop", "")).append(",").append(sharedStops.getString("nextStop", "")).
                        append(",").append(String.valueOf(dataGPS[0])).append(",").append(String.valueOf(dataGPS[1])).
                        append(",").append(String.valueOf(dataGPS[2])).append(",").append(dataText[0]).
                        append(",").append(dataText[1]).append(",").append(dataText[2]).append(",").
                        append(dataText[3]).append(",").append(dataText[4]).
                        append(",").append(dataText[5]);
                writer.close();
                SharedPreferences sharedHistoryInfo = context.getSharedPreferences("HistoryInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedHistoryInfo.edit();
                int saved = sharedHistoryInfo.getInt("Saved", 0) + 1;
                editor2.putInt("Saved", saved);
                editor2.apply();
                Toast.makeText(context, "Фиксация сохранена", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
