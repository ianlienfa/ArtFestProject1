package com.example.artfestproject1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.artfestproject1.databinding.ActivityExplainBinding
import com.example.artfestproject1.databinding.ActivityMainBinding

class ExplainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityExplainBinding.inflate(layoutInflater)
        val rootview = binding.root
        setContentView(rootview)
        binding.camera.setOnClickListener{
            val intent_to_camera = Intent(this, ShowCamera::class.java)
            startActivity(intent_to_camera)
        }

    }

}