package com.example.artfestproject1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.artfestproject1.databinding.ActivityExplainBinding
import com.example.artfestproject1.databinding.ActivityMainBinding

class ExplainActivity : AppCompatActivity() {
    protected var mMyApp: MyApp? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityExplainBinding.inflate(layoutInflater)
        val rootview = binding.root
        setContentView(rootview)
        mMyApp = this.applicationContext as MyApp
        binding.camera.setOnClickListener{
            val intent_to_camera = Intent(this, ShowCamera::class.java)
            startActivity(intent_to_camera)
        }

        binding.sendOpen.setOnClickListener {
            // TODO:
            // val intent_to_login = Intent(this, LoginActivity::class.java)
            // startActivity(intent_to_login)
            // For testing
            val intenttosend = Intent(this, SendMailActivity::class.java)
            startActivity(intenttosend)
        }

    }
    override fun onResume(){
        super.onResume()
        // app reference relink
        mMyApp!!.setCurrentActivity(this);
    }

}