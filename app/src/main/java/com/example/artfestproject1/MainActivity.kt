package com.example.artfestproject1

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.artfestproject1.MyImage.ImageGallery
import com.example.artfestproject1.databinding.ActivityMainBinding
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter


class MainActivity : AppCompatActivity() {
    protected var mMyApp: MyApp? = null

    var h: Int = 0
    var w: Int = 0

    private val messageIntervalmin: Long = 15;
    private val messageFuturemin: Long = 30;
    private val messageInterval: Long = messageIntervalmin * 1000L * 60L;
    private val messageFuture: Long = messageFuturemin * 1000L * 60L;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // app reference
        mMyApp = this.applicationContext as MyApp

        val binding = ActivityMainBinding.inflate(layoutInflater)
        val rootview = binding.root
        setContentView(rootview)
        val count=10
        // set up Intent
        binding.cameraOpen.setOnClickListener{
            // ==============================
//            Log.d("button", "camera open clicked.")
//            val intent_to_camera = Intent(this, ShowCamera::class.java)
//            startActivity(intent_to_camera)
            // TODO:
               val intent_to_detail = Intent(this, DetailActivity::class.java)
               startActivity(intent_to_detail)
            // For testing
     //       val intent_to_camera = Intent(this, ShowCamera::class.java)
       //     startActivity(intent_to_camera)
            //val intent_to_camera = Intent(this, ExplainActivity::class.java)
              //   startActivity(intent_to_camera)
            // ==============================
        }

        binding.adminOpen.setOnClickListener {
            // TODO:
            val intent_to_login = Intent(this, LoginActivity::class.java)
            startActivity(intent_to_login)
            // For testing
//            val intent_to_admin = Intent(this, AdminActivity::class.java)
//            startActivity(intent_to_admin)
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
//        val img = ImageGallery.assetsRead("rex.jpg", this)  // Load source img from apk's assets Directory
//        ImageGallery.internalImgWrite("rex.jpg", img, this) // Save source img into internal Directory
        val img = ImageGallery.assetsRead("test_image.jpg", this)  // Load source img from apk's assets Directory
        ImageGallery.internalImgWrite("test_image.jpg", img, this) // Save source img into internal Directory
        ImageGallery.printImageDir(this)    // For Debug

        // put image for test
        ImageGallery.DIRPATH = ImageGallery.imageDirPath(this)+'/'
//        val image_load = ImageGallery.stdLoadImg("rex.jpg", this)
//        ImageGallery.stdSaveImg(image_load, "rex.jpg", this)
        val image_load = ImageGallery.stdLoadImg("test_image.jpg", this)
        ImageGallery.stdSaveImg(image_load, "test_image.jpg", this)

        // do algorithm
        System.out.println("${ImageGallery.DIRPATH}")
//        val image = ImageGallery.stdLoadImg("rex.jpg", this)
//        val imageGallery = ImageGallery(image, 108, 108)
        val image = ImageGallery.stdLoadImg("test_image.jpg", this)
        val imageGallery = ImageGallery(image, 95, 136)

        // create status.txt here
        w = imageGallery.get_ImageGallery_width()
        h = imageGallery.get_ImageGallery_height()
        val status = Array(h) { IntArray(w) }
        if (!hasStatusFile()) {
            initStatus(w, h, status)
            createStatusFile()
            saveStatusToTxt(w, h, status)
        }

        // create mode.txt (compute algorithm)
        val mode: Int
        if (!hasModeFile()) {
            mode = 0    // 0: random,  1: BAI,  2: shiuan,  3: Tim
            createModeFile()
            saveModeToTxt(mode)
        }

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

//        object : CountDownTimer(messageFuture, messageInterval) {
//
//            override fun onTick(millisUntilFinished: Long) {
////                Toast.setText("seconds remaining: " + millisUntilFinished / 1000)
//                if(millisUntilFinished < messageFuture - 100L)
//                {
//                    runOnUiThread {
//                        if (applicationContext is MyApp) {
//                            val currentActivity = (applicationContext as MyApp).currentActivity
////                      val intent_to_admin = Intent(applicationContext, AdminActivity::class.java)
////                      startActivity(intent_to_admin)
//                            val builder: AlertDialog.Builder = AlertDialog.Builder(currentActivity)
//                            builder.setTitle("已經超時，是否回到主頁面？")
//                            builder.setNegativeButton("NO", DialogInterface.OnClickListener { arg0, arg1 -> // TODO Auto-generated method stub
//
//                            })
//                            builder.setPositiveButton("YES", DialogInterface.OnClickListener { arg0, arg1 -> // TODO Auto-generated method stub
//                                cancel()
//                                val intent_to_main = Intent(applicationContext, MainActivity::class.java)
//                                startActivity(intent_to_main)
//                            })
//                            builder.show()
//                        } else {
//                            runOnUiThread {
//                                Toast.makeText(applicationContext, "error showing dialog", Toast.LENGTH_LONG).show()
//                            }
//                        }
//                    }
//                }
//
////                runOnUiThread {
////                    Toast.makeText(applicationContext, millisUntilFinished.toString(), Toast.LENGTH_LONG).show()
////                }
//            }
//
//            override fun onFinish() {
//                runOnUiThread {
////                    Toast.makeText(applicationContext, "done!", Toast.LENGTH_LONG).show()
//                    if(applicationContext is MyApp) {
//                        val currentActivity = (applicationContext as MyApp).currentActivity
////                      val intent_to_admin = Intent(applicationContext, AdminActivity::class.java)
////                      startActivity(intent_to_admin)
//                        val builder: AlertDialog.Builder = AlertDialog.Builder(currentActivity)
//                        builder.setTitle("已經超時，正在導回主頁面")
//                        builder.setPositiveButton("YES", DialogInterface.OnClickListener { arg0, arg1 -> // TODO Auto-generated method stub
//                            val intent_to_main = Intent(applicationContext, MainActivity::class.java)
//                            startActivity(intent_to_main)
//                        })
//                        builder.show()
//                    }
//                    else{
//                        runOnUiThread {
//                            Toast.makeText(applicationContext, "error showing dialog", Toast.LENGTH_LONG).show()
//                        }
//                    }
//                }
//            }
//        }.start()
    }

    private fun hasStatusFile(): Boolean {

        val filename = "status.txt"
        val file = File(this.getFilesDir(), filename)

        return file.exists()
    }

    private fun initStatus(w: Int, h: Int, status: Array<IntArray>) {

        for (i in 0..h-1) {
            for (j in 0..w-1) {
                status[i][j] = 4    // ADMIN_AVAILABLE
            }
        }

    }

    private fun createStatusFile() {

        val filename = "status.txt"
        val file = File(this.getFilesDir(), filename)

        if (!file.exists()) {
            val fileContents = ""
            this.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(fileContents.toByteArray())
            }
        }

    }

    private fun saveStatusToTxt(w: Int, h: Int, status: Array<IntArray>) {

        // 2D-array to string
        val builder = StringBuilder()
        for (i in 0..h-1) {
            for (j in 0..w-1) {
                builder.append(status[i][j].toString());
                if (j < status[i].size - 1) {
                    builder.append(",")
                }
            }
            builder.append("\n")
        }

        val filename = "status.txt"
        val file = File(this.getFilesDir(), filename)

        // write the 2D-array string to status file
        val statusFilePath = file.absolutePath
        val writer = BufferedWriter(FileWriter(statusFilePath))
        writer.write(builder.toString());
        writer.close();

    }


//    ==================================================


    private fun hasModeFile(): Boolean {

        val filename = "mode.txt"
        val file = File(this.getFilesDir(), filename)

        return file.exists()
    }

    private fun createModeFile() {

        val filename = "mode.txt"
        val file = File(this.getFilesDir(), filename)

        if (!file.exists()) {
            val fileContents = ""
            this.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(fileContents.toByteArray())
            }
        }

    }

    private fun saveModeToTxt(mode: Int) {

        val builder = StringBuilder()
        builder.append(mode.toString())
        builder.append("\n")

        val filename = "mode.txt"
        val file = File(this.getFilesDir(), filename)

        // write the 2D-array string to status file
        val statusFilePath = file.absolutePath
        val writer = BufferedWriter(FileWriter(statusFilePath))
        writer.write(builder.toString());
        writer.close();

    }


}