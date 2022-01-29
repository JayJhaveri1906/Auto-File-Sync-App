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
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        sync = findViewById(R.id.button3);
        sync.setOnClickListener(new View.OnClickListener()
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

}