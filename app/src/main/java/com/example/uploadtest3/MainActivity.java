package com.example.uploadtest3;
import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import android.net.Uri;

import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity
{
    Button sync;
    Button file;
    Button credits;
    private static final int MNG_CODE = 501;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            Uri.parse("package:" + "com.example.uploadtest3"));
                    startActivityForResult(intent, MNG_CODE);
                } catch (Exception e) {
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
            }
        }
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//            askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
//            askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
//            askForPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE, 1);
//            askForPermission(Manifest.permission.RECEIVE_BOOT_COMPLETED, 1);
//        }




        sync = findViewById(R.id.button);
        sync.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Toast.makeText(getApplicationContext(), "lmao", Toast.LENGTH_SHORT).show();
                Intent secPg = new Intent(getApplicationContext(), MainActivity3.class);
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
    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}