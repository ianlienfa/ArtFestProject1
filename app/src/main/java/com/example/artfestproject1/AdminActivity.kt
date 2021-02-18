package com.example.artfestproject1

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.artfestproject1.databinding.ActivityAdminBinding
import com.example.artfestproject1.databinding.ActivityLoginBinding
import java.io.File

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAdminBinding.inflate(layoutInflater)
        val rootview = binding.root
        setContentView(rootview)

        binding.button.setOnClickListener {
            deleteUserPhotos()
            Toast.makeText(this, "Success delete user photos!", Toast.LENGTH_SHORT).show()
        }
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
}