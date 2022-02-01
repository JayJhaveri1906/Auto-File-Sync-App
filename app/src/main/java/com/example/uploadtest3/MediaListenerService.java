package com.example.uploadtest3;//MediaListenerService.java


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest;

public class MediaListenerService extends Service {

    public static FileObserver observer;
//    public static String syncPath = "";
    String lati = "19.045959";
    String longi = "72.890080";

    public MediaListenerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        System.out.println("inside onStartCmd");
        if (extras == null)
        {
            Log.d("Service", "null");
        }
        else
        {
            String syncPath = "";
            String movePath = "";
            Log.d("Service","not null");
            syncPath = (String) extras.get("pathh");
            movePath = (String) extras.get("movePathhh");
            startWatching(syncPath, movePath);
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
//        syncPath = intent.getStringExtra("pathh");
//        System.out.println("inBind " + syncPath);
        return null;
    }

    @Override
    public void onCreate() {
        System.out.println("in onCreate");
        super.onCreate();
//        startWatching(syncPath);
    }

    private void startWatching(String pathToWatch2, String pathToMove2) {

        //The desired path to watch or monitor
        //E.g Camera folder
//        final String pathToWatch = android.os.Environment.getExternalStorageDirectory().toString() + "/Documents";
//        final String pathToWatch = "content://com.android.externalstorage.documents/tree/parallel%3ADocuments";
        final String pathToWatch = android.os.Environment.getExternalStorageDirectory().toString() + "/" + pathToWatch2;
        final String pathToMove = android.os.Environment.getExternalStorageDirectory().toString() + "/" + pathToMove2;
        System.out.println("pathhhhhh: "+ pathToWatch);
        Toast.makeText(this, "Now watching for new files at " + pathToWatch, Toast.LENGTH_LONG).show();

        observer = new FileObserver(pathToWatch, FileObserver.ALL_EVENTS) { // set up a file observer to watch this directory
            @Override
            public void onEvent(int event, final String file) {
                if (event == FileObserver.CREATE || event == FileObserver.MODIFY || event == FileObserver.MOVED_TO && !file.equals(".probe")) { // check that it's not equal to .probe because thats created every time camera is launched
//                    Log.d("MediaListenerService", "File created [" + pathToWatch + file + "]");

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(getBaseContext(), file + " was saved!", Toast.LENGTH_LONG).show();
                            System.out.println("neeraj"+ file +" "+ event+" "+FileObserver.CREATE);
//                            MultipartUploadRequest(this, serverUrl = "http://103.197.221.163:3478/upload/multipart")
//                                    .setMethod("POST")
//                                    .addFileToUpload(
//                                            filePath = filePath,
//                                            parameterName = "myFile"
//                                    ).startUpload();
//                            MultipartUploadRequest((MultipartUploadRequest)(new MultipartUploadRequest(getApplicationContext(), "http://103.197.221.163:3478/upload/multipart")).setMethod("POST"), filePath, "myFile", (String)null, (String)null, 12, (Object)null).startUpload();
                        }
                    });
                }
                else if (event == FileObserver.CLOSE_WRITE && !file.equals(".probe")) { // check that it's not equal to .probe because thats created every time camera is launched
//                    Log.d("MediaListenerService", "File created [" + pathToWatch + file + "]");

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {


//                            Toast.makeText(getBaseContext(), file + " was saved!", Toast.LENGTH_LONG).show();
                            System.out.println("randiMain"+ file +" "+ event+" "+FileObserver.CREATE + "Close write");

                            MultipartUploadRequest mu = new MultipartUploadRequest(getApplicationContext(),"http://103.197.221.163:3478/upload/multipart");
//                            MultipartUploadRequest mu = new MultipartUploadRequest(getApplicationContext(),"https://enaiug4935taq.x.pipedream.net");
                            mu.setMethod("POST");
                            mu.setAutoDeleteFilesAfterSuccessfulUpload(true);
//                            mu.addFileToUpload("","","","");
                            try {
//                                mu.addFileToUpload(pathToWatch + "/" + file, "myFile", lat+"_"+longi+"_"+file);
                                mu.addFileToUpload(pathToWatch + "/" + file, lati+"_"+longi, file);
                            } catch (FileNotFoundException e) {
                                System.out.println("In catch idfk");
                                e.printStackTrace();
                            }
                            mu.startUpload();
                            System.out.println("Uploaded!!!!!!!!!");

                            System.out.println("Starting Copying");
                            // the file to be moved or copied
                            File sourceFile = new File (pathToWatch + "/" + file);

                            // make sure your target location folder exists!
                            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                            currentTime = currentTime.replaceAll(":",".");
                            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//                            String tmpTime =  Calendar.getInstance().getTime().toString();
                            String tmpTime = currentDate + "__" + currentTime + "__";
                            File targetFile = new File (pathToMove + "/" + tmpTime + file );

                            try
                            {
//                                if (sourceFile.renameTo(targetFile))
//                                {
//                                    System.out.println("Move Doneeeeeeee!!");
//                                }
//                                else
//                                {
//                                    System.out.println("Move Failed bruh!!");
//                                }
                                InputStream in = new FileInputStream(sourceFile);
                                OutputStream out = new FileOutputStream(targetFile);

                                // Copy the bits from instream to outstream
                                byte[] buf = new byte[1024];
                                int len;

                                while ((len = in.read(buf)) > 0) {
                                    out.write(buf, 0, len);
                                }

                                in.close();
                                out.close();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }


//                            MultipartUploadRequest(this, serverUrl = "http://103.197.221.163:3478/upload/multipart")
//                                    .setMethod("POST")
//                                    .addFileToUpload(
//                                            filePath = filePath,
//                                            parameterName = "myFile"
//                                    ).startUpload();
//                            MultipartUploadRequest((MultipartUploadRequest)(new MultipartUploadRequest(getApplicationContext(), "http://103.197.221.163:3478/upload/multipart")).setMethod("POST"), filePath, "myFile", (String)null, (String)null, 12, (Object)null).startUpload();
                        }
                    });
                }
                else
                {
                    Log.d("FILEOBSERVER_EVENT", "Event with id " + Integer.toHexString(event) + " happened"); // event identifies the occured Event in hex
                }
            }
        };
        observer.startWatching();
    }
}