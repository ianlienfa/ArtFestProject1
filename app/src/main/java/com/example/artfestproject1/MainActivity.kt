package com.example.artfestproject1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.artfestproject1.MyImage.ImageGallery
import com.example.artfestproject1.databinding.ActivityMainBinding
import org.bytedeco.opencv.global.opencv_imgcodecs.imread
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val rootview = binding.root
        setContentView(rootview)

        // set up Intent
        binding.cameraOpen.setOnClickListener{
            Log.d("button", "camera open clicked.")
            val intent_to_camera = Intent(this, ShowCamera::class.java)
            startActivity(intent_to_camera)
        }

//        Log.d("Write", "Start")
//        val img = ImageGallery.assetsRead("rex.jpg", this)  // Load source img from apk's assets Directory
//        ImageGallery.internalImgWrite("rex.jpg", img, this) // Save source img into internal Directory
//        ImageGallery.printImageDir(this)    // For Debug
//
//        // put image for test
//        ImageGallery.DIRPATH = ImageGallery.imageDirPath(this)+'/'
//        val image_load = ImageGallery.stdLoadImg("rex.jpg")
//        ImageGallery.stdSaveImg(image_load, "rex.jpg")
//
//        // do algorithm
//        System.out.println("${ImageGallery.DIRPATH}")
//        val image = ImageGallery.stdLoadImg("rex.jpg")
//        val imageGallery = ImageGallery(image, 108, 108)
//
//        val img_user = ImageGallery.stdLoadImg("[4][7].jpg")
//        val img_base = ImageGallery.stdLoadImg("[1][7].jpg")
//        val img_new = imageGallery.algorithm_BAI(img_user, img_base)
//        ImageGallery.stdSaveImg(img_new, "output.jpg")
//        ImageGallery.printImageDir(this)    // For Debug
    }
}