package com.example.artfestproject1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.artfestproject1.MyImage.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // put image for test
        ImageGallery.DIRPATH = "/Users/linenyan/Coding/ImageTest/app/src/main/java/com/example/imagetest/MyImage/"
        val image_load = ImageGallery.stdLoadImg("rex.jpg")
        ImageGallery.DIRPATH = this.filesDir.absolutePath + '/'
        ImageGallery.stdSaveImg(image_load, "rex.jpg")

        // do algorithm
        ImageGallery.DIRPATH = this.filesDir.absolutePath + '/'
        System.out.println("${ImageGallery.DIRPATH}")
        val image = ImageGallery.stdLoadImg("rex.jpg")
        val imageGallery = ImageGallery(image, 108, 108)

        val img_user = ImageGallery.stdLoadImg("[4][7].jpg")
        val img_base = ImageGallery.stdLoadImg("[1][7].jpg")
        val img_new = imageGallery.algorithm_BAI(img_user, img_base)
        ImageGallery.stdSaveImg(img_new, "output.jpg")
        setContentView(R.layout.activity_main)
    }
}