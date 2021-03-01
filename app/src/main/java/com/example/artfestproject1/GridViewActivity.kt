package com.example.artfestproject1

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
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

    lateinit var gridView: GridView
    private var photoImages = intArrayOf(R.drawable.test1, R.drawable.test2, R.drawable.test3,
            R.drawable.test4, R.drawable.test5, R.drawable.test6, R.drawable.test7, R.drawable.test8,
        R.drawable.test9, R.drawable.test10)
    // val bigImages: MutableList<Drawable> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid_view)

        gridView = findViewById(R.id.simpleGridView)
        // Create an object of CustomAdapter and set Adapter to GirdView
        val mainAdapter = MainAdapter(this@GridViewActivity, photoImages)
        gridView.adapter = mainAdapter
        // Implement setOnItemClickListener event on GridView
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val intent = Intent(this@GridViewActivity, StatusActivity::class.java)
            intent.putExtra("image", photoImages[position])
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
}