package com.example.uploadtest3

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest

class MainActivity2 : AppCompatActivity() {

    companion object {
        // Every intent for result needs a unique ID in your app.
        // Choose the number which is good for you, here I'll use a random one.
        const val pickFileRequestCode = 42
        const val pickFolderRequestCode = 43
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        findViewById<Button>(R.id.browse).setOnClickListener {
            pickFile()
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
                onFilePicked(it.data.toString())
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    fun onFilePicked(filePath: String) {
        println("path $filePath");
        MultipartUploadRequest(this, serverUrl = "http://103.197.221.163:3478/upload/multipart")
                .setMethod("POST")
                .addFileToUpload(
                        filePath = filePath,
                        parameterName = "myFile"
                ).startUpload()
    }
}
