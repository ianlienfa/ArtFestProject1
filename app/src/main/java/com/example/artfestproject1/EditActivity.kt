package com.example.artfestproject1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.print.PrintManager
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.print.PrintHelper
import com.example.artfestproject1.MyImage.ImageGallery
import com.example.artfestproject1.databinding.ActivityEditBinding
import org.bytedeco.opencv.opencv_core.Mat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class EditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View binding
        val binding = ActivityEditBinding.inflate(layoutInflater)
        val rootview = binding.root
        val imageView = binding.imageView
        val loadingPanel = binding.loadingPanel
        val printButton = binding.printButton
        val compute_button = binding.computeButton

        // file parameters
        var newFilename: String = ""
        val baseImgName = "[1][7].jpg"

        // Image parameters
        val crop_x = 1500
        val crop_y = 1500
        val crop_width = 1000
        val crop_height = 1000

        // Set Content view
        setContentView(rootview)

        // 取得傳遞過來的資料
        val previousIntent = this.intent
        val imageURI = previousIntent.getStringExtra("imageURI")
        val uri: Uri = Uri.parse(imageURI)

        // get filename from URI
        var filename: String = uri.path.toString()
        val cut: Int = filename.lastIndexOf('/')
        if (cut != -1) {
            filename = filename.substring(cut + 1)
        }

        Thread(Runnable {

            var mat: Mat = ImageGallery.internalImgRead(filename, this)
            ImageGallery.internalImgWrite(filename, mat,this)

            // Prepare for the image show
            val imgFile = File(ImageGallery.imageDirFile(this), filename)
            val imgFilePath = imgFile.absolutePath
            val newURI = Uri.parse(imgFilePath)

            // only update UI on UI thread, hide the loadingPanel and show the others
            loadingPanel.post{
                loadingPanel.visibility = GONE
            }
            printButton.post{
                printButton.visibility = VISIBLE
            }
            compute_button.post{
                compute_button.visibility = VISIBLE
            }
            imageView.post{
                imageView.setImageURI(newURI)
                imageView.visibility = VISIBLE
            }
        }).start()

        compute_button.setOnClickListener{
            Thread(Runnable {

                // 轉換照片並存檔
                newFilename = "algorithm_"+filename
                var img_user: Mat = ImageGallery.internalImgRead(filename, this)
                img_user = ImageGallery.matCrop(img_user, crop_x, crop_y, crop_width, crop_height)

                // start testing for algorithm ----

                // put image for test
                ImageGallery.DIRPATH = ImageGallery.imageDirPath(this)+'/'

                // do algorithm
                System.out.println("${ImageGallery.DIRPATH}")
                val image = ImageGallery.stdLoadImg("rex.jpg", this)
                val imageGallery = ImageGallery(image, 108, 108)
                val img_base = ImageGallery.stdLoadImg(baseImgName, this)
                val img_new = imageGallery.algorithm_shiuan(img_user, img_base)
                ImageGallery.printImageDir(this)    // For Debug

                // end testing for algorithm ----

                // Write computed img
                ImageGallery.internalImgWrite(newFilename, img_new,this)

                // Prepare for the image show
                val internalEntryPoint: File = this.getFilesDir()
                val imgDir = File(internalEntryPoint.absolutePath, "Images")
                val imgFile: File = File(imgDir, newFilename)
                val imgFilePath = imgFile.absolutePath
                val newURI = Uri.parse(imgFilePath)

                // only update UI on UI thread, hide the loadingPanel and show the others
                loadingPanel.post{
                    loadingPanel.visibility = GONE
                }
                printButton.post{
                    printButton.visibility = VISIBLE
                }
                compute_button.post{
                    compute_button.visibility = VISIBLE
                }
                imageView.post{
                    imageView.setImageURI(newURI)
                    imageView.visibility = VISIBLE
                }

            }).start()
        }

        // Print Button
        printButton.setOnClickListener{
            if(!newFilename.equals("")) {
                val imgFile = File(ImageGallery.imageDirFile(this), newFilename)
                val imgFilePath = imgFile.absolutePath
                if(File(imgFilePath).exists()) {
                    doPhotoPrint(imgFilePath)
                }
            }
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
}