package com.example.myapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage


class MainActivity : AppCompatActivity() {
    private lateinit var image:ImageView
    private val mStorageReference: StorageReference? = null
    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>(){

        override fun createIntent(context: Context, input: Any?): Intent {
            val uri = Uri.parse("android.resource://com.example.myapp/drawable/first")
            return CropImage.activity(uri)
                .getIntent(this@MainActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.button2)
        image = findViewById<ImageView>(R.id.imageView)
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract){
            it?.let{uri ->
                image.setImageURI(uri)
            }
        }
        btn.setOnClickListener{
            cropActivityResultLauncher.launch(null)
        }
    }
}