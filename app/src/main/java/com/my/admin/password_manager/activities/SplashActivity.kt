package com.my.admin.password_manager.activities

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.MobileAds
import com.my.admin.password_manager.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //Create SharedPreferences, which will be used for checking if user open the app first time
        val settings : SharedPreferences = getSharedPreferences("PREFS", 0)
        val passwordString = settings.getString("pinValue", "")

        if(passwordString == "") {
            //If user open the app first time, intent to CreatePinActivity
            val intent = Intent(this, CreatePinActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            //If two and more times, intent to EnterPinActivity
            val intent = Intent(this, EnterPinActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}
