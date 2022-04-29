package com.example.uploadtest3

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog


class MainActivity2 : AppCompatActivity() {



    companion object {
        // Every intent for result needs a unique ID in your app.
        // Choose the number which is good for you, here I'll use a random one.
        const val pickFileRequestCode = 42
        const val pickFolderRequestCode = 43
        var gps: GPSTracker? = null
        var lati = "19.045959"
        var longi = "72.890080"

        var gFilePath = ""

        private var textView: TextView? = null
        var serverUrl = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // get parameters
        // receiving which server to send

        // receiving which server to send
        val extras = this.intent.extras
        if (extras == null) {
            Log.d("Service", "null")
        } else {
            Log.d("Service", "not null")
            serverUrl = (extras["serverUrl"] as String?).toString()
            println("RANDI received server url $serverUrl")
        }


        gps = GPSTracker(this@MainActivity2)
        if (gps!!.canGetLocation()) {

            lati = gps!!.getLatitude().toString() + ""
            longi = gps!!.getLongitude().toString() + ""
            System.out.println("Randi Found lati longi using gps module $lati $longi")
        }

        textView = findViewById<TextView>(R.id.textView)

        findViewById<Button>(R.id.browse).setOnClickListener {
            pickFile()
        }

        findViewById<Button>(R.id.sync).setOnClickListener {
            if(gFilePath == "")
            {
                Toast.makeText(this,"Please select a File first.",Toast.LENGTH_SHORT).show()
            }
            else
            {
                onFilePicked(gFilePath)
                gFilePath = ""
                textView?.text = "No File Selected"
                Toast.makeText(this,"Started File Upload",Toast.LENGTH_SHORT).show()
            }
            println("Randi in sync button $gFilePath")
        }

        findViewById<ImageView>(R.id.infoButtonImg).setOnClickListener {
            val builder1 = AlertDialog.Builder(this@MainActivity2)
            builder1.setTitle("Single File Upload")

            val msgg = """This feature is useful if you want to select a particular file and upload it to the selected server."""

            builder1.setMessage(msgg)

            builder1.setCancelable(true)

            builder1.setNeutralButton(
                    "Ok",
                    null
            )


            val alert11 = builder1.create()
            alert11.show()
        }
    }

    // Pick a file with a content provider
    fun pickFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            // Filter to only show results that can be "opened", such as files
            addCategory(Intent.CATEGORY_OPENABLE)
            // search for all documents available via installed storage providers
            type = "*/*"
            // obtain permission to read and persistable permission
            flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        startActivityForResult(intent, pickFileRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        if (requestCode == pickFileRequestCode && resultCode == Activity.RESULT_OK) {
            data?.let {
                gFilePath = (it.data.toString())
                println("Randi $gFilePath")
                val uriTree = data.data
                val absPath: String? = uriTree?.getPath()
                var finalPath = absPath?.substring(absPath.lastIndexOf(':') + 1)
                finalPath = finalPath!!.substring(finalPath.lastIndexOf("/") + 1)
                println("decoded $finalPath")
                textView?.text = "$finalPath"
                println("Randi $gFilePath")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    fun onFilePicked(filePath: String) {
        println("path $filePath");
        println("randi $serverUrl")
        MultipartUploadRequest(this, serverUrl = serverUrl)
                .setMethod("POST")
                .addFileToUpload(
                        filePath = filePath,
                        parameterName = lati+"_"+longi
                ).startUpload()
    }
}
