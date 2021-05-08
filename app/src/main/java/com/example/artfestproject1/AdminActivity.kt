package com.example.artfestproject1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.artfestproject1.databinding.ActivityAdminBinding
import com.example.artfestproject1.databinding.ActivityLoginBinding
import java.io.*

class AdminActivity : AppCompatActivity() {
    protected var mMyApp: MyApp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAdminBinding.inflate(layoutInflater)
        val rootview = binding.root
        setContentView(rootview)

        // app reference
        mMyApp = this.applicationContext as MyApp


        binding.gridViewOpen.setOnClickListener {
            val intent_to_gridview = Intent(this, GridViewActivity::class.java)
            startActivity(intent_to_gridview)
        }

        binding.vipOpen.setOnClickListener {
//            val intent_to_vip_list = Intent(this, VipListActivity::class.java)
//            startActivity(intent_to_vip_list)
            val intent_to_nfc = Intent(this, NfcActivity::class.java)
            intent_to_nfc.putExtra("fromWhere", "admin")
            Toast.makeText(this, "靠卡感應以新增至VIP！", Toast.LENGTH_LONG).show()
            startActivity(intent_to_nfc)
        }

//        binding.button.setOnClickListener {
//            deleteUserPhotos()
//            Toast.makeText(this, "Success delete user photos!", Toast.LENGTH_SHORT).show()
//        }

//        binding.button2.setOnClickListener {
//            val row: Int = binding.editTextRow.text.toString().toInt()
//            val col: Int = binding.editTextCol.text.toString().toInt()
//            val s: Int = getStatus(row, col)
//            binding.textViewStatus.text = textStatus(s)
//        }

        // The code is not quite clean...
//        binding.buttonSetTo1.setOnClickListener {
//            val row: Int = binding.editTextRow.text.toString().toInt()
//            val col: Int = binding.editTextCol.text.toString().toInt()
//            setStatus(row, col, 1)
//            binding.textViewStatus.text = textStatus(1)
//        }
//        binding.buttonSetTo2.setOnClickListener {
//            val row: Int = binding.editTextRow.text.toString().toInt()
//            val col: Int = binding.editTextCol.text.toString().toInt()
//            setStatus(row, col, 2)
//            binding.textViewStatus.text = textStatus(2)
//        }
//        binding.buttonSetTo3.setOnClickListener {
//            val row: Int = binding.editTextRow.text.toString().toInt()
//            val col: Int = binding.editTextCol.text.toString().toInt()
//            setStatus(row, col, 3)
//            binding.textViewStatus.text = textStatus(3)
//        }
//        binding.buttonSetTo4.setOnClickListener {
//            val row: Int = binding.editTextRow.text.toString().toInt()
//            val col: Int = binding.editTextCol.text.toString().toInt()
//            setStatus(row, col, 4)
//            binding.textViewStatus.text = textStatus(4)
//        }
    }

    private fun deleteUserPhotos() {
        var dir: File = filesDir
        dir = File(dir, "Images")
        val children: Array<String> = dir.list()
        for (i in children.indices) {
            if (children[i].contains("2021")) {
                File(dir, children[i]).delete()
            }
        }
    }

    private fun getStatus(row: Int, col: Int): Int {
        // TODO: Remove hardcoding
        val w: Int = 33
        val h: Int = 11
        val status = Array(h) { IntArray(w) }
        readStatusFromTxt(w, h, status)
        // Don't know why currently...
        return status[col][row]
        // return status[row][col]
    }

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

    private fun textStatus(s: Int): String {
        when (s) {
            1 -> return "USER_PRINTED"
            2 -> return "USER_AVAILABLE"
            3 -> return "ADMIN_PRINTED"
            4 -> return "ADMIN_AVAILABLE"
        }
        return "ERROR"
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