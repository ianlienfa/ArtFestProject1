package com.example.artfestproject1

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.print.PrintManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.print.PrintHelper
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class AfterPrintActivity : AppCompatActivity() {
    protected var mMyApp: MyApp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_after_print)

        // app reference
        mMyApp = this.applicationContext as MyApp

        var board: Int = readBoardFromTxt()

        val rowArray = charArrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L')
        val row = intent.getIntExtra("row", 0)
        val col = intent.getIntExtra("col", 0) + 1
        val imgFilePath = intent.getStringExtra("imgFilePath")
//        Log.d("Admin", row.toString())
//        Log.d("Admin", col.toString())

        // 一進到這個 activity 就送印
        if(File(imgFilePath).exists()) {
            doPhotoPrint(imgFilePath.toString())
        }

        val textViewNumber: TextView = findViewById(R.id.textViewNumber)
        val c: Char = rowArray[row]

//        textViewNumber.text = "$c$col"
        if (board == 1) {

            if (col > 11) {
                textViewNumber.text = "$c${col-11}"
            } else {
                textViewNumber.text = "左 $c$col"
                Toast.makeText(this, "記得到 admin 切換成 left / right 哦！", Toast.LENGTH_LONG).show()
            }

        } else if (board == 2) {

            if (col > 11) {
                textViewNumber.text = "右 $c${col-11}"
            } else {
                textViewNumber.text = "左 $c$col"
            }

        }

        val back_to_main_button: Button = findViewById(R.id.back_to_main)
        back_to_main_button.setOnClickListener {
            val intent_to_main = Intent(this, MainActivity::class.java)
            startActivity(intent_to_main)
        }
        val sendOpen: Button = findViewById(R.id.sendOpen)
        sendOpen.setOnClickListener {
            // TODO:
            // val intent_to_login = Intent(this, LoginActivity::class.java)
            // startActivity(intent_to_login)
            // For testing
            val intenttosend = Intent(this, SendMailActivity::class.java)
            startActivity(intenttosend)
        }

        val print_again_button: Button = findViewById(R.id.print_again_button)
        print_again_button.setOnClickListener {
//            val imgFilePath = intent.getStringExtra("imgFilePath")
//            Log.d("Admin", imgFilePath.toString())
            if(File(imgFilePath).exists()) {
                doPhotoPrint(imgFilePath.toString())
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

    private fun readBoardFromTxt(): Int {

        val filename = "board.txt"
        val file = File(this.getFilesDir(), filename)
        val statusFilePath = file.absolutePath
        val reader: BufferedReader = BufferedReader(FileReader(statusFilePath))
        var line = ""
        var row: Int = 0

        return reader.readLine().toInt()

    }


    override fun onResume(){
        super.onResume()
        // app reference relink
        mMyApp!!.setCurrentActivity(this);
    }

}