package com.example.artfestproject1

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.print.PrintManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.print.PrintHelper
import com.example.artfestproject1.MyImage.ImageGallery
import com.example.artfestproject1.databinding.ActivityShowBinding
import java.io.File

class ShowActivity: AppCompatActivity() {
    protected var mMyApp: MyApp? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityShowBinding.inflate(layoutInflater)
        val rootview = binding.root
        setContentView(rootview)
        mMyApp = this.applicationContext as MyApp
        binding.imageView.setImageURI(null);
        val previousIntent = this.intent
        val bundle = intent.extras
        val imageURI  = bundle!!.getString("imagepath")
        val newFilename  = bundle!!.getString("newFilename")
        val rowToSend = bundle!!.getInt("row")
        val colToSend = bundle!!.getInt("col")
        //val imageURI = previousIntent.getStringExtra("imageURI")
        val uri: Uri = Uri.parse(imageURI)
        binding.imageView.setImageURI(uri)
        binding.imageView.visibility = View.VISIBLE
        binding.print.setOnClickListener{
//            if(!newFilename.equals("")) {
//                val imgFile = File(ImageGallery.imageDirFile(this), newFilename)
//                val imgFilePath = imgFile.absolutePath
//                if(File(imgFilePath).exists()) {
//                    doPhotoPrint(imgFilePath)
//                }
//            }

            val intent_to_after_print = Intent(this, AfterPrintActivity::class.java)
            intent_to_after_print.putExtra("row", rowToSend)
            intent_to_after_print.putExtra("col", colToSend)
            intent_to_after_print.putExtra("imgFilePath", imageURI)
            startActivity(intent_to_after_print)
        }

    }
    private fun doPhotoPrint(filepath: String) {
        this.also { context ->
            PrintHelper(context).apply {
                scaleMode = PrintHelper.SCALE_MODE_FIT
            }.also { printHelper ->
                val bitmap = BitmapFactory.decodeFile(filepath)
                printHelper.colorMode = PrintHelper.COLOR_MODE_MONOCHROME
                printHelper.printBitmap("droids.jpg - test print", bitmap, PrintHelper.OnPrintFinishCallback {
                    Log.d("Print", "print finished.")
                })
            }
        }
        val printManager = baseContext.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printJobs = printManager.printJobs
        for(job in printJobs)
        {
            if(job.equals("droids.jpg - test print"))
            {
                if(job.isCompleted()){
                    Toast.makeText(getApplicationContext(), "print_complete", Toast.LENGTH_LONG).show();
                    Log.d("Print", "print_complete.")

                }
                else if(job.isFailed()){
                    Toast.makeText(getApplicationContext(), "print_failed", Toast.LENGTH_LONG).show();
                    Log.d("Print", "print_failed`b .")
                }
                else{
                    Log.d("Print", "else.")
                }
            }
        }
    }
    override fun onResume(){
        super.onResume()
        // app reference relink
        mMyApp!!.setCurrentActivity(this);
    }
}
