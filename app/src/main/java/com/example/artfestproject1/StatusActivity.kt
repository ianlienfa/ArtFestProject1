package com.example.artfestproject1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.artfestproject1.databinding.ActivityAdminBinding
import com.example.artfestproject1.databinding.ActivityStatusBinding
import kotlinx.android.synthetic.main.activity_status.*
import java.io.*


class StatusActivity : AppCompatActivity() {

    protected var mMyApp: MyApp? = null
    lateinit var selectedImage: ImageView
    lateinit var statusColor: ImageView
    lateinit var textViewPosition: TextView

    lateinit var buttonPrintThis: Button
    lateinit var buttonSetTo1: Button
    lateinit var buttonSetTo2: Button
    lateinit var buttonSetTo3: Button
    lateinit var buttonSetTo4: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        // app reference
        mMyApp = this.applicationContext as MyApp

        selectedImage = findViewById(R.id.selectedImage)
        statusColor = findViewById(R.id.statusColor)
        textViewPosition = findViewById(R.id.textViewPosition)

        val intent = intent
        val smallImage = intent.getIntExtra("image", 0)
        selectedImage.setImageResource(smallImage)
        statusColor.setBackgroundColor(intent.getIntExtra("color", 0))
        textViewPosition.text = intent.getIntExtra("position", 0).toString()

        buttonPrintThis = findViewById(R.id.buttonPrintThis)
        buttonSetTo1 = findViewById(R.id.buttonSetTo1)
        buttonSetTo2 = findViewById(R.id.buttonSetTo2)
        buttonSetTo3 = findViewById(R.id.buttonSetTo3)
        buttonSetTo4 = findViewById(R.id.buttonSetTo4)
        // TODO: need to change
        val w: Int = 33
        val position = intent.getIntExtra("position", 0)

        buttonPrintThis.setOnClickListener {
            val intent_to_show_camera = Intent(this, ShowCamera::class.java)
            val col: Int = position % w
            val row: Int = (position - col) / w
            intent_to_show_camera.putExtra("smallImage", "[$col][$row].jpg")
            intent_to_show_camera.putExtra("rowAdmin", row)
            intent_to_show_camera.putExtra("colAdmin", col)
            intent_to_show_camera.putExtra("fromAdmin", true)
            startActivity(intent_to_show_camera)
        }

        // The code is not quite clean...
        // TODO: the color is hard coding
        buttonSetTo1.setOnClickListener {
            val col: Int = position % w
            val row: Int = (position - col) / w
            setStatus(col, row, 1)
            statusColor.setBackgroundColor(0x3312ff52)
//            Log.d("Admin", 1.toString())
        }
        buttonSetTo2.setOnClickListener {
            val col: Int = position % w
            val row: Int = (position - col) / w
            setStatus(col, row, 2)
            statusColor.setBackgroundColor(0x33ffffff)
//            Log.d("Admin", 2.toString())
        }
        buttonSetTo3.setOnClickListener {
            val col: Int = position % w
            val row: Int = (position - col) / w
            setStatus(col, row, 3)
            statusColor.setBackgroundColor(0x33e3256b)
//            Log.d("Admin", 3.toString())
        }
        buttonSetTo4.setOnClickListener {
            val col: Int = position % w
            val row: Int = (position - col) / w
            setStatus(col, row, 4)
            statusColor.setBackgroundColor(0x33ffd90f)
//            Log.d("Admin", 4.toString())
        }
    }

    // TODO: package them!

    private fun setStatus(row: Int, col: Int, newStatus: Int) {
        // TODO: Remove hardcoding
        val w: Int = 33
        val h: Int = 11
        val status = Array(h) { IntArray(w) }
        readStatusFromTxt(w, h, status)
        // Don't know why currently...
        status[col][row] = newStatus
        saveStatusToTxt(w, h, status)
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

    override fun onResume(){
        super.onResume()
        // app reference relink
        mMyApp!!.setCurrentActivity(this);
    }

}