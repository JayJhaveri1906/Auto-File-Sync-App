package com.example.uploadtest3;
import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import android.net.Uri;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity
{
    ImageView infoo;

    Button sync;
    Button file;
    Button credits;
    private Spinner spinner;
    private static final String[] paths = {"VESIT", "TIFR"};
    String serverUrl = "http://103.197.221.163:3478/upload/multipart";
    private static final int MNG_CODE = 501;

    GPSTracker gps;


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Log.d(TAG,"randi in try 47");
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            Uri.parse("package:" + "com.example.uploadtest3"));
                    startActivityForResult(intent, MNG_CODE);
                } catch (Exception e) {
                    Log.d(TAG, "randi in catch 52");
                    System.out.println(e);
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, MNG_CODE);
                }
            }
        }


        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
                Log.d(TAG,"randi inside if 65");
            }
        }

        if (MyVersion > Build.VERSION_CODES.Q) {
            if (!checkIfAlreadyhavePermission()) {
                backgroundLocationPermission(102);
                Log.d(TAG,"randi inside if 65");
            }
        }
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//            askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
//            askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
//            askForPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE, 1);
//            askForPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED, 1);
//        }

        gps = new GPSTracker(MainActivity.this);
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }


        infoo = findViewById(R.id.infoButtonImg);
        infoo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle("Hello, Welcome to Aatmanirbhar Samakraman!!");

                String msgg =
                        "Before proceeding towards the features first check that you have provided the app with all the required permissions:\n" +
                        "\n" +
                        "1. File Permissions : TO access new files coming into the directory\n" +
                        "2. Location Permissions : To send your current location along with the file to track the sending of file.\n" +
                        "\n" +
                        "\n" +
                        "Before proceeding with the features, you have to select a server where you need the files to be upload.\n" +
                        "\n" +
                        "\n" +
                        "In this App we are providing you with two features\n" +
                        "\n" +
                        "\n" +

                        "1. Auto File Sync : This feature is used to automatically sync files whenever they are added to a particular directory which you will select inside that option.\n" +
                        "\n\t\t\t\tEverytime a new file is added to that directory ,file is automatically sent to the server selected and once the files get uploaded to the server it will show you a notification and will get moved in a backup directory.\n" +
                        "\n" +
                        "\n" +
                        "2. Upload single file : This feature is used to send a single file directly to the server . Once the files gets uploaded it will show a notification about the progress of the file sending.";

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


        sync = findViewById(R.id.button);
        sync.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Toast.makeText(getApplicationContext(), "lmao", Toast.LENGTH_SHORT).show();
                System.out.println("RANDI clicked sync button server "+serverUrl);
                Intent secPg = new Intent(getApplicationContext(), MainActivity3.class);
                secPg.putExtra("serverUrl", serverUrl);
                startActivity(secPg);
            }
        });
        file = findViewById(R.id.button2);
        file.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Toast.makeText(getApplicationContext(), "lmao", Toast.LENGTH_SHORT).show();
                Intent secPg = new Intent(getApplicationContext(), MainActivity2.class);
                secPg.putExtra("serverUrl", serverUrl);
                startActivity(secPg);
            }
        });
        credits = findViewById(R.id.button3);
        credits.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Toast.makeText(getApplicationContext(), "lmao", Toast.LENGTH_SHORT).show();
                Intent secPg = new Intent(getApplicationContext(), MainActivity4.class);
                startActivity(secPg);
            }
        });

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
//        spinner.setPopupBackgroundResource(R.drawable.spinner_text);
//        spinner.setBackgroundResource(R.drawable.spinner_text);
//        spinner.setOutlineSpotShadowColor();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                System.out.println("RANDI item"+ (String) parent.getItemAtPosition(position));
//                int gg = ((int) parent.getChildCount());
//                parent.getItemAtPosition()
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#F7C000"));
//                ((TextView) parent.getChildAt(0)).setTextSize(5);
                switch (position) {
                    case 0:
                        serverUrl = "http://103.197.221.163:3478/upload/multipart";
                        Toast.makeText(getApplicationContext(), "Using VESIT server" + "", Toast.LENGTH_SHORT).show();
                        // Whatever you want to happen when the first item gets selected
                        break;
                    case 1:
                        serverUrl = "https://enaiug4935taq.x.pipedream.net";
                        Toast.makeText(getApplicationContext(), "Using TIFR server" + "", Toast.LENGTH_SHORT).show();
                        // Whatever you want to happen when the second item gets selected
                        break;
                    default:
                        System.out.println("RANDI in default case");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#F7C000"));
//                ((TextView) v).setGravity(Gravity.CENTER);

//                ((TextView) parent.getChildAt(0)).setTextSize(5);
            }
        });
    }


    private boolean checkIfAlreadyhavePermission() {
        Log.d(TAG,"randi in checkAlreadyPerm 114");

        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            return false;
        }
        return true;

    }



    private void requestForSpecificPermission() {
        Log.d(TAG,"randi in request specifivc 124");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 101);
    }

    private void requestForSpecificPermission2() {
        Log.d(TAG,"randi in request specifivc 139");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 101);
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private void backgroundLocationPermission(int backgroundLocationRequestCode) {
        if (checkPermissionGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
        {
            return;
        }
        else {
            // HELP BUTTON KE LIYE YEH USE KAREGE
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Location Permission");
            builder1.setMessage("Location is necessary for this application");
            builder1.setCancelable(false);

            builder1.setPositiveButton(
                    "Yes location!!",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 102);
                        }
                    });

            builder1.setNegativeButton(
                    "Cancel :_(",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Toast.makeText(getApplicationContext(), "No mate u can't cancel ;)", Toast.LENGTH_SHORT).show();
                            rerunLocationBox();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

    }

    private boolean checkPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    Log.d(TAG,"randi case 101 if granted 134");
                } else {
                    //not granted
                    Log.d(TAG,"randi case 101 if not granted 137");
                }
                break;
            default:
                Log.d(TAG,"randi default 141");
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void rerunLocationBox()
    {
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.Q) {
            if (!checkIfAlreadyhavePermission()) {
                backgroundLocationPermission(102);
                Log.d(TAG,"randi inside if 65");
            }
        }
    }

}
