package com.example.testdrive4;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class UploadImageTask extends AsyncTask<ByteArrayContent, Void, String> {
    private final String fileNames;
    private final Context context;
    private final SharedPreferences sharedPreferences;
    //private String folderId = "1OEon_6lH94B6TupvVeicEwf8cc1vEIfL";
    private final String folderId;
    private Drive mDriveService;

    public UploadImageTask(String fileNames,Context context, Drive mDriveService) {
        this.fileNames = fileNames;
        this.context = context;
        sharedPreferences=context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        folderId = sharedPreferences.getString("URL", "");
        this.mDriveService = mDriveService;
    }
    @Override
    protected String doInBackground(ByteArrayContent... params) {
        try {
            File fileMetadata = new File();
            fileMetadata.setName(fileNames+".jpg");
            if (folderId != null && !folderId.isEmpty()) {
                List<String> parents = Collections.singletonList(folderId);
                fileMetadata.setParents(parents);
            }
            if(params.length==1) {
                fileMetadata.setName(fileNames+".csv");
                File csvFile = mDriveService.files().create(fileMetadata, params[0])
                        .setFields("id")
                        .execute();
                return csvFile.getId();

            }else {
                File imageFile = mDriveService.files().create(fileMetadata, params[0])
                        .setFields("id")
                        .execute();
                fileMetadata.setName(fileNames + ".csv");
                File csvFile = mDriveService.files().create(fileMetadata, params[1])
                        .setFields("id")
                        .execute();
                return imageFile.getId() + "," + csvFile.getId();
            }
        } catch (UserRecoverableAuthIOException e) {
//            startActivityForResult(e.getIntent(), REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String fileId) {
        super.onPostExecute(fileId);
        if (fileId != null) {
            deleteFileNames(fileNames);
            SharedPreferences.Editor editor = sharedHistoryInfo.edit();
            int sent = sharedHistoryInfo.getInt("Sent",0)+1;
            editor.putInt("Sent",sent);
            editor.apply();

            listView.setAdapter(adapter);
            countUploaded++;
        } else {
            countErrors++;
        }
        if(countUploaded+countErrors==countAll)
        {
            Toast.makeText(HistoryActivity.this, "Успешно отправленно файлов : " + countUploaded +"\n Не отправленно файлов : "+countErrors, Toast.LENGTH_SHORT).show();
            countUploaded=0;
            countErrors=0;
            adapter.clear();
            adapter.notifyDataSetChanged();
            makeListView();
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
            cheakUpload=true;
        }
    }
}
