package com.example.artfestproject1

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.artfestproject1.MyImage.ImageGallery
import com.example.artfestproject1.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    protected var mMyApp: MyApp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // app reference
        mMyApp = this.applicationContext as MyApp

        val binding = ActivityMainBinding.inflate(layoutInflater)
        val rootview = binding.root
        setContentView(rootview)

        // set up Intent
        binding.cameraOpen.setOnClickListener{
            // ==============================
//            Log.d("button", "camera open clicked.")
//            val intent_to_camera = Intent(this, ShowCamera::class.java)
//            startActivity(intent_to_camera)
            // TODO:
//             val intent_to_nfc = Intent(this, NfcActivity::class.java)
//             startActivity(intent_to_nfc)
            // For testing
            val intent_to_camera = Intent(this, ShowCamera::class.java)
            startActivity(intent_to_camera)
            // ==============================
        }

        binding.adminOpen.setOnClickListener {
            // TODO:
            // val intent_to_login = Intent(this, LoginActivity::class.java)
            // startActivity(intent_to_login)
            // For testing
            val intent_to_admin = Intent(this, AdminActivity::class.java)
            startActivity(intent_to_admin)
        }
        binding.sendOpen.setOnClickListener {
            // TODO:
            // val intent_to_login = Intent(this, LoginActivity::class.java)
            // startActivity(intent_to_login)
            // For testing
            val intenttosend = Intent(this, SendMailActivity::class.java)
            startActivity(intenttosend)
        }

        Log.d("Write", "Start")
        val img = ImageGallery.assetsRead("rex.jpg", this)  // Load source img from apk's assets Directory
        ImageGallery.internalImgWrite("rex.jpg", img, this) // Save source img into internal Directory
        ImageGallery.printImageDir(this)    // For Debug

        // put image for test
        ImageGallery.DIRPATH = ImageGallery.imageDirPath(this)+'/'
        val image_load = ImageGallery.stdLoadImg("rex.jpg", this)
        ImageGallery.stdSaveImg(image_load, "rex.jpg", this)

        // do algorithm
        System.out.println("${ImageGallery.DIRPATH}")
        val image = ImageGallery.stdLoadImg("rex.jpg", this)
        val imageGallery = ImageGallery(image, 108, 108)

//
//        val img_user = ImageGallery.stdLoadImg("[4][7].jpg")
//        val img_base = ImageGallery.stdLoadImg("[1][7].jpg")
//        val img_new = imageGallery.algorithm_BAI(img_user, img_base)
//        ImageGallery.stdSaveImg(img_new, "output.jpg")
//        ImageGallery.printImageDir(this)    // For Debug

        // ==============================
//        var mat: Mat = ImageGallery.internalImgRead("2021-02-04-15-50-10-790.jpg", this)
//        mat = ImageGallery.colorToGray(mat)
//        ImageGallery.internalImgWrite("test.jpg", mat, this)
        // ==============================
    }

    override fun onResume(){
        super.onResume()
        // app reference relink
        mMyApp!!.setCurrentActivity(this);

        object : CountDownTimer(10000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
//                Toast.setText("seconds remaining: " + millisUntilFinished / 1000)
                runOnUiThread { Toast.makeText(applicationContext, millisUntilFinished.toString(), Toast.LENGTH_LONG).show() }
            }

            override fun onFinish() {
                runOnUiThread {
                    Toast.makeText(applicationContext, "done!", Toast.LENGTH_LONG).show()
                    if(applicationContext is MyApp) {
                        val currentActivity = (applicationContext as MyApp).currentActivity
//                    val intent_to_admin = Intent(applicationContext, AdminActivity::class.java)
//                    startActivity(intent_to_admin)
                        val builder: AlertDialog.Builder = AlertDialog.Builder(currentActivity)
                        builder.setTitle("hi")
                        builder.show()
                    }
                    else{
                        runOnUiThread {
                            Toast.makeText(applicationContext, "error showing dialog", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }.start()
    }
}