package com.example.uploadtest3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
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

public class MainActivity3 extends AppCompatActivity
{

    // Broadcast stuff
    BroadcastReceiver receiver;


    FusedLocationProviderClient mFusedLocationClient;

    ImageView infoo;

    Button browse;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private static final int RQS_OPEN_DOCUMENT_TREE = 45;
    private static final int RQS_MOVE_DOCUMENT_TREE = 69;

//    // BUTTONS
//    Button browseDir;
    String syncStatus = "false";


    //    protected LocationManager locationManager;
//    protected LocationListener locationListener;
//    protected String latitude, longitude;
//    protected boolean gps_enabled, network_enabled;

    public String syncPath = "";
    //    public String syncPath = sharedPref.getString("syncPath", "");
    public static int pathFlag = 0;

    public String movePath = "";
    public static int moveFlag = 0;

    public int Gsize = 0;
    public int Countt = 0;
    public String Gtime = "";

    String serverUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        // receiving which server to send
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("Service", "null");
        } else {
            Log.d("Service", "not null");
            serverUrl = (String) extras.get("serverUrl");
            System.out.println("RANDI received server url "+serverUrl);
        }


        syncStatus = "false";


        // Receive broadcast
        // do something here.
        receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {

                String fname = intent.getStringExtra("fname");
                String date = intent.getStringExtra("date");
                String time = intent.getStringExtra("time");
                int size = intent.getIntExtra("size",0);
                System.out.println("Randiiii in main activity"+fname+date+time+size);

                if (Gtime == time)
                {
                    System.out.println("Randii Skipping cause multi calls");
                }
                else {
                    Gtime = time;
                    Countt++;
                    Gsize += size;

                    TextView tmp = findViewById(R.id.textView13);
                    tmp.setText(Countt + " Files");

                    tmp = findViewById(R.id.textView14);
                    tmp.setText(Gsize + " KB");

                    tmp = findViewById(R.id.scrollText);
                    String logss = tmp.getText() + "";
                    String toPrint = logss + "\n" + fname + " " + date + " " + time + " " + size + " KB";
                    tmp.setText(toPrint);
                }


                // do something here.
            }
        };



        // Main Pgm

        // instating preused vars

        // sync path
        // SHARED Path for next sessions
        sharedPref = getSharedPreferences("mypref", 0);
        editor = sharedPref.edit();
        try {
            String tmp = sharedPref.getString("syncPath", "");
            syncPath = tmp;
            System.out.println("try catch path" + syncPath);
        } catch (Exception e) {
            System.out.println("Creating new shared Var sync");
            editor.putString("syncPath", "");
        }

        // move path
        try {
            String tmp = sharedPref.getString("movePath", "");
            movePath = tmp;
        } catch (Exception e) {
            System.out.println("Creating new shared Var move");
            editor.putString("movePath", "");
        }


        if (!syncPath.equals("")) {
            TextView path = findViewById(R.id.path);
            path.setText(syncPath);
            pathFlag = 1;
        }
        if (!movePath.equals("")) {
            TextView path = findViewById(R.id.path2);
            path.setText(movePath);
            moveFlag = 1;
        }


//        browse = findViewById(R.id.browse);
//        browse.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
////                Toast.makeText(getApplicationContext(), "lmao", Toast.LENGTH_SHORT).show();
//                Intent secPg = new Intent(getApplicationContext(), MainActivity2.class);
//                startActivity(secPg);
//            }
//        });

        infoo = findViewById(R.id.infoButtonImg);
        infoo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity3.this);
                builder1.setTitle("File Auto-Sync");

                String msgg = "To use this feature follow the following steps:\n" +
                        "\n" +
                        "1. STEP 1:- Select  a directory to be monitored :  In this option u have to select a diretory which has to be polled to watch new files coming to the directory so as to send the same to the selected server in the previous step.\n" +
                        "\n" +
                        "2. STEP 2:- Select a backup directory : In this option you have to select a directory which will be used as a backup directory in which files will be moved from previous directory once the file is succesfully uploaded to the server.\n" +
                        "\n" +
                        "3. STEP 3:- Start Sync : This option is used to start the whole process of watching a particular directory and then send the files to the server.\n";

                builder1.setMessage(msgg);

                builder1.setCancelable(true);

                builder1.setNeutralButton(
                        "Ok",
                        null
                );


                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });



        TextView scrolll = findViewById(R.id.scrollText);
        scrolll.setMovementMethod(new ScrollingMovementMethod());

        Button browseDirPath = findViewById(R.id.browseDirPath);
        browseDirPath.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, RQS_OPEN_DOCUMENT_TREE);
            }
        });

        Button browseDirPath2 = findViewById(R.id.browseDirPath2);
        browseDirPath2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, RQS_MOVE_DOCUMENT_TREE);
            }
        });


        Button browseDir = findViewById(R.id.browseDir);
        browseDir.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view)
            {
//                Intent thirdPg = new Intent(getApplicationContext(), StartupReceiver.class);
//                startActivity(thirdPg);
//                Intent myIntent = new Intent(getApplicationContext(), FileSystemObserverService.class);
//                getApplicationContext().startService(myIntent);
//                Intent intent2 = new Intent("android.intent.action.OPEN_DOCUMENT");
//                // Filter to only show results that can be "opened", such as files
//                intent2.addCategory(Intent.CATEGORY_OPENABLE);
//                // search for all documents available via installed storage providers
//                intent2.setType("*/*");
//                // obtain permission to read and persistable permission
//                intent2.setFlags(65);
//                public static final int pickFileRequestCode = 42;
//                this.startActivityForResult(var2, 42);
//                System.out.println(intent2);
//                Intent intent = new Intent(getApplicationContext(), FileObserverService.class);
//                String INTENT_EXTRA_FILEPATH = "";
//                intent.putExtra(INTENT_EXTRA_FILEPATH, "//com.android.externalstorage.documents/document/primary/Download");
//                getApplicationContext().startService(intent);
                Intent syncIntent = new Intent(getApplicationContext(), MediaListenerService.class);
                if (syncStatus == "false") {
                    if (pathFlag == 0) {
                        Toast.makeText(getApplicationContext(), "Please set sync path first", Toast.LENGTH_SHORT).show();
                    } else if (syncPath.equals("")) {
                        Toast.makeText(getApplicationContext(), "Invalid Sync Path", Toast.LENGTH_SHORT).show();
                    } else if (moveFlag == 0) {
                        Toast.makeText(getApplicationContext(), "Please set move path first", Toast.LENGTH_SHORT).show();
                    } else if (movePath.equals("")) {
                        Toast.makeText(getApplicationContext(), "Invalid Move Path", Toast.LENGTH_SHORT).show();
                    } else {
                        //                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        //                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                        browseDir.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.stop_sync));
                        browseDir.setText("Stop Sync");
                        syncStatus="true";

                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());


//                        Intent syncIntent = new Intent(getApplicationContext(), MediaListenerService.class);
                        System.out.println("path while calling intent " + syncPath);
                        //                    getLastLocation();
                        syncIntent.putExtra("pathh", syncPath);
                        syncIntent.putExtra("movePathhh", movePath);
                        syncIntent.putExtra("serverUrl", serverUrl);
                        startService(syncIntent);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Sync Service stopped", Toast.LENGTH_SHORT).show();

                    browseDir.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.start_sync));
                    browseDir.setText("Start Sync");
                    syncStatus="false";
                    stopService(syncIntent);
//                    onBackPressed();
                }
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RQS_OPEN_DOCUMENT_TREE) {
            System.out.println("inside sync path activity");
            Uri uriTree = data.getData();
            System.out.println("path?? " + uriTree + "\n" + uriTree.toString());
            String absPath = uriTree.getPath();
            String finalPath = absPath.substring(absPath.lastIndexOf(':') + 1);
            System.out.println("decoded " + finalPath);

            TextView path = findViewById(R.id.path);
            path.setText(finalPath);

            sharedPref = getSharedPreferences("mypref", 0);
            editor = sharedPref.edit();
            editor.putString("syncPath", finalPath);
            editor.commit();

            pathFlag = 1;
            syncPath = finalPath;
        } else if (resultCode == RESULT_OK && requestCode == RQS_MOVE_DOCUMENT_TREE) {
            System.out.println("inside move path activity");
            Uri uriTree = data.getData();
            System.out.println("path?? " + uriTree + "\n" + uriTree.toString());
            String absPath = uriTree.getPath();
            String finalPath = absPath.substring(absPath.lastIndexOf(':') + 1);
            System.out.println("decoded " + finalPath);

            TextView path = findViewById(R.id.path2);
            path.setText(finalPath);

            sharedPref = getSharedPreferences("mypref", 0);
            editor = sharedPref.edit();
            editor.putString("movePath", finalPath);
            editor.commit();

            moveFlag = 1;
            movePath = finalPath;
        }

    }

    private void getLastLocation()
    {
        // check if permissions are given
        // getting last
        // location from
        // FusedLocationClient
        // object
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            System.out.println("No permission GIVENNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNnn");
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>()
        {
            @Override
            public void onComplete(@NonNull Task<Location> task)
            {
                Location location = task.getResult();
                if (location == null) {
                    requestNewLocationData();
                } else {
                    Toast.makeText(getApplicationContext(), location.getLatitude() + "", Toast.LENGTH_SHORT).show();
//                    System.out.println();
                    System.out.println(location.getLongitude() + "");
                }
            }
        });

    }

    private void requestNewLocationData()
    {

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
            //                                          int[] grantResults)
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
            Toast.makeText(getApplicationContext(), mLastLocation.getLatitude()+"", Toast.LENGTH_SHORT).show();
            System.out.println("Latitude: " + mLastLocation.getLatitude() + "");
            System.out.println("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter("returnFromMediaService")
        );
    }

}

