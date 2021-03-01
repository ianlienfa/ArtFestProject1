package com.example.artfestproject1

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class StatusActivity : AppCompatActivity() {

    lateinit var selectedImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        selectedImage = findViewById(R.id.selectedImage)
        val intent = intent
        selectedImage.setImageResource(intent.getIntExtra("image", 0));
    }
}