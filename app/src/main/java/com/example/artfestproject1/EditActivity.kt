package com.example.artfestproject1

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.print.PrintManager
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import androidx.print.PrintHelper
import com.example.artfestproject1.MyImage.ImageGallery
import com.example.artfestproject1.databinding.ActivityEditBinding
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.destination
import id.zelory.compressor.constraint.resolution
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bytedeco.opencv.opencv_core.Mat
import java.io.*
import java.util.*

class EditActivity : AppCompatActivity() {
    protected var mMyApp: MyApp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // app reference
        mMyApp = this.applicationContext as MyApp

        // file parameters
        var newFilename: String = ""
        val baseImgName = "[1][7].jpg"
        var imgFilePath: String = ""

        // Image parameters, htc: 3024 * 4032
        val crop_x = 32
        val crop_y = 200
        val CROP_WIDTH = 2000
        val CROP_HEIGHT = 2000
        val expected_pixel_w = 108
        val expected_pixel_h = 108

        // View binding
        val binding = ActivityEditBinding.inflate(layoutInflater)
        val rootview = binding.root
        val imageView = binding.imageView
        val loadingPanel = binding.progressBar
        val printButton = binding.printButton
        val compute_button = binding.computeButton
        val user_send_print_button = binding.userSendPrintButton
//        val finish_print_button = binding.finishPrintButton

        // Will be sent to AfterPrintActivity
        var rowToSend: Int = -1
        var colToSend: Int = -1


        // Set Content view
        setContentView(rootview)

        // 取得傳遞過來的資料
        val previousIntent = this.intent
        val bundle = intent.extras
        val imageURI  = bundle!!.getString("imageURI")
        var count = bundle!!.getInt("count")
        //val imageURI = previousIntent.getStringExtra("imageURI")
        val uri: Uri = Uri.parse(imageURI)
        Log.d("Editcount", count.toString())

        // get filename from URI
        var filename: String = uri.path.toString()
        val cut: Int = filename.lastIndexOf('/')
        if (cut != -1) {
            filename = filename.substring(cut + 1)
        }

        lifecycle.coroutineScope.launch {
            // Pretreatment -- Crop to square
            newFilename = doPhotoCrop(filename, baseContext, crop_x, crop_y, CROP_WIDTH, CROP_HEIGHT)

            // Pretreatment -- Compress

            runBlocking {
                newFilename = doPhotoCompress(newFilename, baseContext, expected_pixel_w, expected_pixel_h)
                Log.d("Thread", "1")
            }

            // Pretreatment -- Compress might leave some pixels, do some pruning
            Log.d("Thread", "2")
            newFilename = doPhotoCrop(newFilename, baseContext, 0, 0, expected_pixel_w, expected_pixel_h)

            // Prepare for the image show
            val imgFile = File(ImageGallery.imageDirFile(baseContext), newFilename)
            val imgFilePath = imgFile.absolutePath
            val newURI = Uri.parse(imgFilePath)

            // only update UI on UI thread, hide the loadingPanel and show the others
            binding.progressBar.post {
                binding.progressBar.visibility = GONE
            }
            binding.printButton.post {
                //binding.printButton.visibility = VISIBLE
            }
            binding.computeButton
            binding.computeButton.post {
                binding.computeButton.visibility = VISIBLE
            }
            binding.imageView.post {
                binding.imageView.setImageURI(newURI)
                binding.imageView.visibility = VISIBLE
            }

        }
        onemore_button.setOnClickListener{

            val intent_to_camera = Intent(this, ShowCamera::class.java)
            count+=1
            if (count==1){
                val context = applicationContext
                val text = "第二次拍攝開始"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, text, duration)
                toast.show()
                intent_to_camera.putExtra("countback", count);
                startActivity(intent_to_camera)

            }
            else if (count==2) {
                val context = applicationContext
                val text = "最後一次拍攝囉！"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, text, duration)
                toast.show()
                intent_to_camera.putExtra("countback", count);
                startActivity(intent_to_camera)
            }
            else
            {
                val context = applicationContext
                val text = "已拍攝三次囉!請按滿意繼續"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(context, text, duration)
                toast.show()
            }
        }

        compute_button.setOnClickListener{
            Thread(Runnable {

                // Load user photo
                val img_user = ImageGallery.stdLoadImg(newFilename, this)

                // put image for test
                ImageGallery.DIRPATH = ImageGallery.imageDirPath(this)+'/'

                // do algorithm
                System.out.println("${ImageGallery.DIRPATH}")
                val image = ImageGallery.stdLoadImg("rex.jpg", this)
                val imageGallery = ImageGallery(image, 108, 108)
                val img_base = ImageGallery.stdLoadImg(baseImgName, this)
                var img_new: Mat;
                val rand = (Math.random()*3) as Int;
                if(rand == 0)
                {
                    img_new = imageGallery.algorithm_BAI(img_user, img_base)
                }
                else if(rand == 1)
                {
                    img_new = imageGallery.algorithm_shiuan(img_user, img_base)
                }
                else
                {
                    img_new = imageGallery.algorithm_Tim(img_user, img_base)
                }
                ImageGallery.printImageDir(this)    // For Debug

                // end testing for algorithm ----

                // Write computed img
                newFilename = "cmp_"+newFilename
                ImageGallery.internalImgWrite(newFilename, img_new,this)
                //TODO:
                Log.d("Admin", imageGallery.get_Imgstatus(0, 0).toString())

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

                val intent_to_show = Intent(this, ShowActivity::class.java)
                var bundle = Bundle()

                bundle.putString("imagepath", imgFilePath.toString())
                bundle.putString("newFilename", newFilename.toString())
                intent_to_show.putExtras(bundle)
                startActivity(intent_to_show)



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

        // Compute & Print
        user_send_print_button.setOnClickListener {


            Thread(Runnable {

                // Load user photo
                val img_user = ImageGallery.stdLoadImg(newFilename, this)

                // put image for test
                ImageGallery.DIRPATH = ImageGallery.imageDirPath(this)+'/'

                // do algorithm
                System.out.println("${ImageGallery.DIRPATH}")
                val image = ImageGallery.stdLoadImg("rex.jpg", this)
                val imageGallery = ImageGallery(image, 108, 108)

                // insert
                // ==============================

                var w: Int = imageGallery.get_ImageGallery_width()
                var h: Int = imageGallery.get_ImageGallery_height()

                val status = Array(h) { IntArray(w) }
                val toPrintList: MutableList<Int> = ArrayList()

                // Get status
                if (!hasStatusFile()) {
                    initStatus(w, h, status)
                    createStatusFile()
                    saveStatusToTxt(w, h, status)
                } else {
                    readStatusFromTxt(w, h, status)
                }

                for (i in 0..h-1) {
                    for (j in 0..w-1) {
                        if (status[i][j] == 2) {    // USER_AVAILABLE
                            toPrintList.add(w * i + j)
                        }
                    }
                }

                val randomIndex: Int = (0..toPrintList.size).random()
                val x = randomIndex % w
                val y = (randomIndex - x) / w
                val baseImgName = "[$x][$y].jpg"
                val baseImgNameForDebug = "[$y][$x].jpg"
                // val baseImgName = "[1][7].jpg"

                rowToSend = y
                colToSend = x

                // For testing
                Log.d("Admin", baseImgNameForDebug)

                // Save back new status
                status[y][x] = 1                // USER_PRINTED
                saveStatusToTxt(w, h, status)

                val img_base = ImageGallery.stdLoadImg(baseImgName, this)
                val img_new = imageGallery.algorithm_BAI(img_user, img_base)
                ImageGallery.printImageDir(this)    // For Debug

                // end testing for algorithm ----

                // Write computed img
                newFilename = "cmp_"+newFilename
                ImageGallery.internalImgWrite(newFilename, img_new,this)
                //TODO:
                Log.d("Admin", imageGallery.get_Imgstatus(0, 0).toString())

                // Prepare for the image show
                val internalEntryPoint: File = this.getFilesDir()
                val imgDir = File(internalEntryPoint.absolutePath, "Images")
                val imgFile: File = File(imgDir, newFilename)
                imgFilePath = imgFile.absolutePath
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

//                if(!newFilename.equals("")) {
//                    val imgFile = File(ImageGallery.imageDirFile(this), newFilename)
//                    val imgFilePath = imgFile.absolutePath
//                    if(File(imgFilePath).exists()) {
//                        doPhotoPrint(imgFilePath)
//                    }
//                }

                val intent_to_after_print = Intent(this, AfterPrintActivity::class.java)
                intent_to_after_print.putExtra("row", rowToSend)
                intent_to_after_print.putExtra("col", colToSend)
                intent_to_after_print.putExtra("imgFilePath", imgFilePath)
                startActivity(intent_to_after_print)

            }).start()

        }

//        finish_print_button.setOnClickListener {
//            val intent_to_after_print = Intent(this, AfterPrintActivity::class.java)
//            intent_to_after_print.putExtra("row", rowToSend)
//            intent_to_after_print.putExtra("col", colToSend)
//            intent_to_after_print.putExtra("imgFilePath", imgFilePath)
//            startActivity(intent_to_after_print)
//        }
    }
//
//    private fun photoPretreatmentAndShow(binding: ActivityEditBinding, filename: String, w: Int, h: Int) {
//        lifecycleScope.launch {
//            // Crop Photo
//            newFilename = "cp_"+filename
//            var img_user: Mat = ImageGallery.internalImgRead(filename, baseContext)
//            img_user = ImageGallery.matCrop(img_user, crop_x, crop_y, CROP_WIDTH, CROP_WIDTH)
//            ImageGallery.internalImgWrite(newFilename, img_user,baseContext)
//            Log.d("name", newFilename)
//
//            // Compress photo
//            val internalEntryPoint = baseContext.filesDir
//            if(internalEntryPoint.canRead()){
//                // Get into Images
//                val imgDir = File(internalEntryPoint.absolutePath, "Images")
//                if (!imgDir.exists()) {
//                    Log.d("Write", "ImageGallery.internalWrite: Unable to write into internal storage.")
//                }
//                // Write Img
//                val imgfile = File(imgDir, filename)
//                val desFile = File(imgDir, "cmpr_"+filename)
//                newFilename = "cmpr_"+filename
//
//                // Compress and save
//                val compressedImageFile = Compressor.compress(baseContext, imgfile){
//                    resolution(w, h)
//                    destination(desFile)
//                }
//            }
//
//            // Compress might leave some pixels, do some pruning
//            img_user = ImageGallery.internalImgRead(newFilename, baseContext)
//            img_user = ImageGallery.matCrop(img_user, crop_x, crop_y, w, h)
//            ImageGallery.internalImgWrite(newFilename, img_user,baseContext)
//
//            Log.d("name", newFilename)
//            // Prepare for the image show
//            val imgFile = File(ImageGallery.imageDirFile(baseContext), newFilename)
//            val imgFilePath = imgFile.absolutePath
//            val newURI = Uri.parse(imgFilePath)
//
//            // only update UI on UI thread, hide the loadingPanel and show the others
//            binding.loadingPanel.post{
//                binding.loadingPanel.visibility = GONE
//            }
//            binding.printButton.post{
//                binding.printButton.visibility = VISIBLE
//            }
//            binding.computeButton
//            binding.computeButton.post{
//                binding.computeButton.visibility = VISIBLE
//            }
//            binding.imageView.post{
//                binding.imageView.setImageURI(newURI)
//                binding.imageView.visibility = VISIBLE
//            }
//        }
//    }
//
//

    private fun doPhotoCrop(filename: String, context: Context, crop_x: Int, crop_y: Int, CROP_WIDTH: Int, CROP_HEIGHT: Int): String{
        // Crop Photo
        var newFilename = "cp_"+filename
        while(!ImageGallery.getImageDir(context).canExecute())
        {
            Log.d("IO", "not yet")
        }
        if(ImageGallery.getImageDir(context).canExecute()) {
            var img_user: Mat = ImageGallery.internalImgRead(filename, context)
            if(img_user.cols() != CROP_WIDTH || img_user.rows() != CROP_HEIGHT) {
                img_user = ImageGallery.matCrop(img_user, crop_x, crop_y, CROP_WIDTH, CROP_HEIGHT)
            }
            ImageGallery.internalImgWrite(newFilename, img_user, context)
            Log.e("name", newFilename)
        }
        else{
            Log.d("File", filename+"Not ready yet")
            System.exit(1)
        }
        return newFilename
    }

    private suspend fun doPhotoCompress(filename: String, context: Context, w: Int, h: Int): String{
        val internalEntryPoint = context.filesDir
        var newFilename = ""
        if(internalEntryPoint.canRead()){
            // Get into Images
            val imgDir = File(internalEntryPoint.absolutePath, "Images")
            if (!imgDir.exists()) {
                Log.d("Write", "ImageGallery.internalWrite: Unable to write into internal storage.")
            }
            // Write Img
            val imgfile = File(imgDir, filename)
            val desFile = File(imgDir, "cmpr_"+filename)
            newFilename = "cmpr_"+filename

            // Compress and save
            val compressedImageFile = Compressor.compress(context, imgfile) {
                resolution(w, h)
                destination(desFile)
            }

        }
        return newFilename
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

    private fun hasStatusFile(): Boolean {

        val filename = "status.txt"
        val file = File(this.getFilesDir(), filename)

        return file.exists()
    }

    private fun initStatus(w: Int, h: Int, status: Array<IntArray>) {

        for (i in 0..h-1) {
            for (j in 0..w-1) {
                status[i][j] = 2    // USER_AVAILABLE
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

    // TODO: separate this function to several parts
    private fun readStatusFromTxt(w: Int, h: Int, status: Array<IntArray>) {

        val filename = "status.txt"
        val file = File(this.getFilesDir(), filename)
        val statusFilePath = file.absolutePath
        val reader: BufferedReader = BufferedReader(FileReader(statusFilePath))
        var line = ""
        var row: Int = 0
        for (line in reader.lineSequence()) {
            val cols = line.split(",").toTypedArray()
            var col: Int = 0
            for (c in cols) {
                status[row][col] = c.toInt()
                col++
            }
            row++
        }
        reader.close()

        // For testing
        // Log.d("Admin", Arrays.deepToString(status))

    }

    override fun onResume(){
        super.onResume()
        // app reference relink
        mMyApp!!.setCurrentActivity(this);
    }


}