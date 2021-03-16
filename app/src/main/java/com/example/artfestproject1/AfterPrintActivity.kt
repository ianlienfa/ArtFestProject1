package com.example.artfestproject1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.artfestproject1.databinding.ActivityAfterPrintBinding

class AfterPrintActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_print)

        val rowArray = charArrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J')
        val row = intent.getIntExtra("row", 0)
        val col = intent.getIntExtra("col", 0)
//        Log.d("Admin", row.toString())
//        Log.d("Admin", col.toString())

        val textViewNumber: TextView = findViewById(R.id.textViewNumber)
        val c: Char = rowArray[row]
        textViewNumber.text = "$c$col"
    }
}