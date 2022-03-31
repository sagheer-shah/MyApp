package com.example.myapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var image: ImageView
    private lateinit var bitmap: Bitmap
    private var mStorageReference: StorageReference? = null
    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {


        fun getImageUri(src: Bitmap, format: CompressFormat?, quality: Int): Uri? {
            val os = ByteArrayOutputStream()
            src.compress(format, quality, os)
            val path = MediaStore.Images.Media.insertImage(contentResolver, src, "title", null)
            return Uri.parse(path)
        }
        override fun createIntent(context: Context, input: Any?): Intent {
            println("Image From Firebase: "+FirebaseStorage.getInstance().reference.child("photo1.jpg").toString())

//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//            val uri = Uri.parse(getImageUri(bitmap))
            return CropImage.activity(getImageUri(bitmap, CompressFormat.JPEG, 100))
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

        mStorageReference = FirebaseStorage.getInstance().reference.child("photo1.jpg")
        try {
            val localFile: File = File.createTempFile("photo", "jpg")
            mStorageReference!!.getFile(localFile)
                .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot?> {
                    Toast.makeText(this@MainActivity, "Image Loaded", Toast.LENGTH_SHORT).show()
                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath())
                    (findViewById<View>(R.id.imageView) as ImageView).setImageBitmap(bitmap)
                }).addOnFailureListener(OnFailureListener {
                    Toast.makeText(
                        this@MainActivity,
                        "Image Not Loaded",
                        Toast.LENGTH_SHORT
                    ).show()
                })
        } catch (e: IOException) {
            e.printStackTrace()
        }

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                image.setImageURI(uri)
            }
        }
        btn.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }
    }
}