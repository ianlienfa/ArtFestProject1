package com.example.artfestproject1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.artfestproject1.databinding.ActivityLoginBinding
import com.example.artfestproject1.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {
    protected var mMyApp: MyApp? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        val rootview = binding.root
        setContentView(rootview)

        // app reference
        mMyApp = this.applicationContext as MyApp

        binding.button.setOnClickListener {

            if (binding.textAccount.text.toString().equals("a") && binding.textPassword.text.toString().equals("s")) {
                Toast.makeText(this, "水啦！", Toast.LENGTH_SHORT).show()
                val intent_to_admin = Intent(this, AdminActivity::class.java)
                startActivity(intent_to_admin)
            } else {
                Toast.makeText(this, "別亂入啦xD", Toast.LENGTH_SHORT).show()
                val intent_to_main = Intent(this, MainActivity::class.java)
                startActivity(intent_to_main)
            }

        }
    }

    override fun onResume(){
        super.onResume()
        // app reference relink
        mMyApp!!.setCurrentActivity(this);
    }

}