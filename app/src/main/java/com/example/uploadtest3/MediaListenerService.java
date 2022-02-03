package com.example.uploadtest3; //MediaListenerService.java


import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import java.util.List;
import java.util.Locale;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;

import android.os.Looper;

import com.google.android.gms.tasks.Task;

import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest;

public class MediaListenerService extends Service {

    public static FileObserver observer;
    FusedLocationProviderClient mFusedLocationClient;
    //    public static String syncPath = "";
    String lati = "19.045959";
    String longi = "72.890080";

    public MediaListenerService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        System.out.println("inside onStartCmd");
        if (extras == null) {
            Log.d("Service", "null");
        } else {
            String syncPath = "";
            String movePath = "";
            Log.d("Service", "not null");
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
        System.out.println("pathhhhhh: " + pathToWatch);
        Toast.makeText(this, "Now watching for new files at " + pathToWatch, Toast.LENGTH_LONG).show();

        observer = new FileObserver(pathToWatch, FileObserver.ALL_EVENTS) { // set up a file observer to watch this directory
            @Override
            public void onEvent(int event, final String file) {
                if (event == FileObserver.CREATE || event == FileObserver.MODIFY || event == FileObserver.MOVED_TO && !file.equals(".probe")) { // check that it's not equal to .probe because thats created every time camera is launched
                    //                    Log.d("MediaListenerService", "File created [" + pathToWatch + file + "]");

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            //    Toast.makeText(getBaseContext(), file + " was saved!", Toast.LENGTH_LONG).show();
                            System.out.println("neeraj" + file + " " + event + " " + FileObserver.CREATE);
                            //    MultipartUploadRequest(this, serverUrl = "http://103.197.221.163:3478/upload/multipart")
                            //            .setMethod("POST")
                            //            .addFileToUpload(
                            //                    filePath = filePath,
                            //                    parameterName = "myFile"
                            //            ).startUpload();
                            //    MultipartUploadRequest((MultipartUploadRequest)(new MultipartUploadRequest(getApplicationContext(), "http://103.197.221.163:3478/upload/multipart")).setMethod("POST"), filePath, "myFile", (String)null, (String)null, 12, (Object)null).startUpload();
                        }
                    });
                } 
                else if (event == FileObserver.CLOSE_WRITE && !file.equals(".probe")) { // check that it's not equal to .probe because thats created every time camera is launched
                    //                    Log.d("MediaListenerService", "File created [" + pathToWatch + file + "]");
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                    //                    getLastLocation(); // [lati,longi]
                    //                    requestNewLocationData();
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                  int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    //                    Task<Location> location2 = mFusedLocationClient.getLastLocation();
                    //                    Location location = location2.getResult();

                    mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener < Location > () {
                        @Override
                        public void onComplete(@NonNull Task < Location > task) {
                            Location location = task.getResult();
                            if (location == null) {
                                System.out.println("Pehla walla nahi chala toh 245");
                                requestNewLocationData();
                            } else {
                                lati = location.getLatitude() + "";
                                longi = location.getLongitude() + "";
                                System.out.println("RANDI lati longi set 256 " + lati + "_" + longi);

                                //                    Toast.makeText(getApplicationContext(), location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                                ////                    System.out.println();
                                //                    System.out.println( + "");
                            }

                            //                    System.out.println("RANDI MC location "+ location);

                            System.out.println("RANDI BEFORE CALL of upload thread 127");
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    // LOCATIONNNNNNNNNNN
                                    //    List<String> latilongi;
                                    //    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                                    //    getLastLocation(); // [lati,longi]
                                    System.out.println("RANDI AFTER CALL of function 132");

                                    //    Toast.makeText(getBaseContext(), file + " was saved!", Toast.LENGTH_LONG).show();
                                    System.out.println("randiMain" + file + " " + event + " " + FileObserver.CREATE + "Close write");

                                    MultipartUploadRequest mu = new MultipartUploadRequest(getApplicationContext(),"http://103.197.221.163:3478/upload/multipart");
//                                    MultipartUploadRequest mu = new MultipartUploadRequest(getApplicationContext(), "https://enaiug4935taq.x.pipedream.net");
                                    mu.setMethod("POST");
                                    mu.setAutoDeleteFilesAfterSuccessfulUpload(true);
                                    //    mu.addFileToUpload("","","","");
                                    try {
                                        //        mu.addFileToUpload(pathToWatch + "/" + file, "myFile", lat+"_"+longi+"_"+file);
                                        mu.addFileToUpload(pathToWatch + "/" + file, lati + "_" + longi, file);
                                    } catch (FileNotFoundException e) {
                                        System.out.println("In catch idfk");
                                        e.printStackTrace();
                                    }
                                    mu.startUpload();
                                    System.out.println("Uploaded!!!!!!!!!");

                                    System.out.println("Starting Copying");
                                    // the file to be moved or copied
                                    File sourceFile = new File(pathToWatch + "/" + file);

                                    // make sure your target location folder exists!
                                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                    currentTime = currentTime.replaceAll(":", ".");
                                    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                                    //    String tmpTime =  Calendar.getInstance().getTime().toString();
                                    String tmpTime = currentDate + "__" + currentTime + "__";
                                    File targetFile = new File(pathToMove + "/" + tmpTime + file);

                                    try {
                                        //        if (sourceFile.renameTo(targetFile))
                                        //        {
                                        //            System.out.println("Move Doneeeeeeee!!");
                                        //        }
                                        //        else
                                        //        {
                                        //            System.out.println("Move Failed bruh!!");
                                        //        }
                                        InputStream in = new FileInputStream(sourceFile);
                                        OutputStream out = new FileOutputStream(targetFile);

                                        // Copy the bits from instream to outstream
                                        byte[] buf = new byte[1024];
                                        int len;

                                        while ((len = in .read(buf)) > 0) {
                                            out.write(buf, 0, len);
                                        }

                                        in .close();
                                        out.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    //    MultipartUploadRequest(this, serverUrl = "http://103.197.221.163:3478/upload/multipart")
                                    //            .setMethod("POST")
                                    //            .addFileToUpload(
                                    //                    filePath = filePath,
                                    //                    parameterName = "myFile"
                                    //            ).startUpload();
                                    //    MultipartUploadRequest((MultipartUploadRequest)(new MultipartUploadRequest(getApplicationContext(), "http://103.197.221.163:3478/upload/multipart")).setMethod("POST"), filePath, "myFile", (String)null, (String)null, 12, (Object)null).startUpload();
                                }
                            });
                        }
                    });
                } else {
                    Log.d("FILEOBSERVER_EVENT", "Event with id " + Integer.toHexString(event) + " happened"); // event identifies the occured Event in hex
                }
            }
        };
        observer.startWatching();
    }


    // LOCATION CODE
    /*private void getLastLocation()
    {
        // check if permissions are given
        // getting last
        // location from
        // FusedLocationClient
        // object
//        String lati = "19.045959";
//        String longi = "72.89008";
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                  int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getApplicationContext(), "Give permission first for location", Toast.LENGTH_SHORT).show();
//            System.out.println("No permission GIVENNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNnn");
//            List<String> latilongi = null;
//            latilongi.add(lati);
//            latilongi.add(longi);
//            return latilongi;
            return;
        }

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>()
        {
            @Override
            public void onComplete(@NonNull Task<Location> task)
            {
                Location location = task.getResult();
                if (location == null) {
                    System.out.println("Pehla walla nahi chala toh 245");
                    requestNewLocationData();
                } else {
                    lati = location.getLatitude()+"";
                    longi = location.getLongitude()+"";
                    System.out.println("RANDI lati longi set 256 " + lati + "_" + longi);

//                    Toast.makeText(getApplicationContext(), location.getLatitude() + "", Toast.LENGTH_SHORT).show();
////                    System.out.println();
//                    System.out.println( + "");
                }
            }
        });

    }*/

    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                  int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Toast.makeText(getApplicationContext(), mLastLocation.getLatitude() + "", Toast.LENGTH_SHORT).show();
            System.out.println("Latitude: " + mLastLocation.getLatitude() + "");
            System.out.println("Longitude: " + mLastLocation.getLongitude() + "");
            lati = mLastLocation.getLatitude() + "";
            longi = mLastLocation.getLongitude() + "";
            System.out.println("RANDI lati longi set 269");
        }
    };


}