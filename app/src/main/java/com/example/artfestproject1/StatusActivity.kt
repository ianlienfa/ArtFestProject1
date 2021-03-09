package com.example.artfestproject1

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.artfestproject1.databinding.ActivityAdminBinding
import com.example.artfestproject1.databinding.ActivityStatusBinding
import java.io.*


class StatusActivity : AppCompatActivity() {

    lateinit var selectedImage: ImageView
    lateinit var textViewPosition: TextView

    lateinit var buttonSetTo1: Button
    lateinit var buttonSetTo2: Button
    lateinit var buttonSetTo3: Button
    lateinit var buttonSetTo4: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        selectedImage = findViewById(R.id.selectedImage)
        textViewPosition = findViewById(R.id.textViewPosition)

        val intent = intent
        selectedImage.setImageResource(intent.getIntExtra("image", 0))
        textViewPosition.text = intent.getIntExtra("position", 0).toString()


        buttonSetTo1 = findViewById(R.id.buttonSetTo1)
        buttonSetTo2 = findViewById(R.id.buttonSetTo2)
        buttonSetTo3 = findViewById(R.id.buttonSetTo3)
        buttonSetTo4 = findViewById(R.id.buttonSetTo4)
        // TODO: need to change
        val w: Int = 10
        val position = intent.getIntExtra("position", 0)
        // The code is not quite clean...
        buttonSetTo1.setOnClickListener {
            val col: Int = position % w
            val row: Int = (position - col) / w
            setStatus(col, row, 1)
//            Log.d("Admin", 1.toString())
        }
        buttonSetTo2.setOnClickListener {
            val col: Int = position % w
            val row: Int = (position - col) / w
            setStatus(col, row, 2)
//            Log.d("Admin", 2.toString())
        }
        buttonSetTo3.setOnClickListener {
            val col: Int = position % w
            val row: Int = (position - col) / w
            setStatus(col, row, 3)
//            Log.d("Admin", 3.toString())
        }
        buttonSetTo4.setOnClickListener {
            val col: Int = position % w
            val row: Int = (position - col) / w
            setStatus(col, row, 4)
//            Log.d("Admin", 4.toString())
        }
    }

    // TODO: package them!

    private fun setStatus(row: Int, col: Int, newStatus: Int) {
        // TODO: Remove hardcoding
        val w: Int = 10
        val h: Int = 10
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
}