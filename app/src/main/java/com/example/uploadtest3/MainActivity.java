package com.example.uploadtest3;
import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
    Button browse;
    private static final int RQS_OPEN_DOCUMENT_TREE = 45;
    private static final int MNG_CODE = 501;
    public static String syncPath = "";
    public static int pathFlag = 0;


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

        // init button
        browse = findViewById(R.id.browse);
        browse.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Toast.makeText(getApplicationContext(), "lmao", Toast.LENGTH_SHORT).show();
                Intent secPg = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(secPg);
            }
        });

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
                if (pathFlag == 0)
                {
                    Toast.makeText(getApplicationContext(),"Please set path first", Toast.LENGTH_SHORT).show();
                }
                else if (syncPath=="")
                {
                    Toast.makeText(getApplicationContext(),"Invalid Path", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent syncIntent = new Intent(getApplicationContext(), MediaListenerService.class);
                    System.out.println("path while calling intent "+ syncPath);
                    syncIntent.putExtra("pathh",syncPath);
                    startService(syncIntent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RQS_OPEN_DOCUMENT_TREE) {
            Uri uriTree = data.getData();
            System.out.println("path?? " + uriTree + "\n" + uriTree.toString());
            String absPath = uriTree.getPath();
            String finalPath = absPath.substring(absPath.lastIndexOf(':') + 1);
            System.out.println("decoded " + finalPath);
            TextView path = findViewById(R.id.path);
            path.setText(finalPath);
            pathFlag = 1;
            syncPath = finalPath;
        }
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
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE, Manifest.permission.RECEIVE_BOOT_COMPLETED}, 101);
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