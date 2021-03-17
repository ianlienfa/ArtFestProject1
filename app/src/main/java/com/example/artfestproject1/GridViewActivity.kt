package com.example.artfestproject1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*


// ==============================
//        Code reference
// ==============================
// GridView Tutorial With Examples In Android
// https://abhiandroid.com/ui/gridview
// ===========
// How to create GridView Layout in an Android App using Kotlin?
// https://www.tutorialspoint.com/how-to-create-gridview-layout-in-an-android-app-using-kotlin
// ==============================

class GridViewActivity : AppCompatActivity() {
    protected var mMyApp: MyApp? = null
    lateinit var gridView: GridView
//    private var photoImages = intArrayOf(R.drawable.test1, R.drawable.test2, R.drawable.test3,
//            R.drawable.test4, R.drawable.test5, R.drawable.test6, R.drawable.test7, R.drawable.test8,
//        R.drawable.test9, R.drawable.test10)

    private val photoImages: MutableList<Int> = ArrayList()
    private val photoColors: MutableList<Int> = ArrayList()

    // val bigImages: MutableList<Drawable> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_view)

        // app reference
        mMyApp = this.applicationContext as MyApp

        // For testing
        // Log.d("Admin", R.drawable.small_image0.toString())

        initPhotoImages()
        initPhotoColors()

        gridView = findViewById(R.id.simpleGridView)
        // Create an object of CustomAdapter and set Adapter to GirdView
        val mainAdapter = MainAdapter(this@GridViewActivity, photoImages, photoColors)
        gridView.adapter = mainAdapter
        // Implement setOnItemClickListener event on GridView
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val intent = Intent(this@GridViewActivity, StatusActivity::class.java)
            intent.putExtra("image", photoImages[position])
            intent.putExtra("color", photoColors[position])
            intent.putExtra("position", position)
            startActivity(intent)
        }

        /*

        // TODO: Remove hardcoding
        val w: Int = 10
        val h: Int = 10
        for (i in 0..h-1) {
            for (j in 0..w-1) {
                val dirname = "Images"
                val dir = File(this.getFilesDir(), dirname)
                val imgFile = File(this.getFilesDir(), "[$i][$j].jpg")
                val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                val d: Drawable = BitmapDrawable(resources, myBitmap)
                bigImages.add(d)
            }
        }

        // For testing
        Log.d("Admin", bigImages.toString())

         */
    }

     override fun onRestart() {
        super.onRestart()

        // For testing
        // Log.d("Admin", R.drawable.small_image0.toString())

        initPhotoImages()
        initPhotoColors()

        gridView = findViewById(R.id.simpleGridView)
        // Create an object of CustomAdapter and set Adapter to GirdView
        val mainAdapter = MainAdapter(this@GridViewActivity, photoImages, photoColors)
        gridView.adapter = mainAdapter
        // Implement setOnItemClickListener event on GridView
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val intent = Intent(this@GridViewActivity, StatusActivity::class.java)
            intent.putExtra("image", photoImages[position])
            intent.putExtra("color", photoColors[position])
            intent.putExtra("position", position)
            startActivity(intent)
        }
    }

    private fun initPhotoImages() {

        photoImages.clear()

        for (i in 0..99) {
            val filename = "small_image$i"
            val id = resources.getIdentifier(filename, "drawable", packageName)
            photoImages.add(id)
        }
    }

    private fun initPhotoColors() {

        photoColors.clear()

        for (i in 0..99) {
            // TODO: change w & h
            val w = 10
            val h = 10
            val y = i % w
            val x = (i - y) / w

            // TODO: can change color
            val USER_PRINTED: Int = 0x3312ff52      // green
            val USER_AVAILABLE: Int = 0x33ffffff    // black
            val ADMIN_PRINTED: Int = 0x33e3256b     // purple
            val ADMIN_AVAILABLE: Int = 0x33ffd90f   // yellow
            when (getStatus(y, x)) {
                1 -> photoColors.add(USER_PRINTED)
                2 -> photoColors.add(USER_AVAILABLE)
                3 -> photoColors.add(ADMIN_PRINTED)
                4 -> photoColors.add(ADMIN_AVAILABLE)
            }

            // For testing
            // Log.d("Admin", getStatus(y, x).toString())
        }
    }

    // ==============================

    private fun getStatus(row: Int, col: Int): Int {
        // TODO: Remove hardcoding
        val w: Int = 10
        val h: Int = 10
        val status = Array(h) { IntArray(w) }
        readStatusFromTxt(w, h, status)
        // Don't know why currently...
        return status[col][row]
        // return status[row][col]
    }

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