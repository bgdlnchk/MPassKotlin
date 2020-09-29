package com.my.admin.password_manager.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.kobakei.ratethisapp.RateThisApp
import com.my.admin.password_manager.R
import com.my.admin.password_manager.models.Password

class MainActivity : AppCompatActivity(){

    //Create InterstitialAd
    private lateinit var interstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Integrate RateThisApp
        RateThisApp.onCreate(this)
        RateThisApp.showRateDialogIfNeeded(this)
        //Set the parameters when RateThisApp Window will be shown
        val config = RateThisApp.Config(3, 5)
        RateThisApp.init(config)

        //Initialize AdMob
        MobileAds.initialize(this, getString(R.string.Your_AdMob_ID))

        //Initialize Ad Mob InterstitialAd
        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = getString(R.string.Your_Ad_ID)
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }

        //Initialize SQL Database and CardViews
        PasswordsActivity.dbHandler = DBHandler(this, null)
        val generateCard = findViewById<CardView>(R.id.generate_card);
        val passwordsCard = findViewById<CardView>(R.id.passwords_card);
        val addCard = findViewById<CardView>(R.id.add_card);
        val changePinCard = findViewById<CardView>(R.id.change_pin_card);

        //Set onClickListeners for CardViews
        generateCard.setOnClickListener({
            createGenerateDialog()
        })
        passwordsCard.setOnClickListener {
            val intent = Intent(this, PasswordsActivity::class.java)
            startActivity(intent)
        }
        addCard.setOnClickListener({
            createAddDialog()
        })
        changePinCard.setOnClickListener {
            val intent = Intent(this, ChangePinActivity::class.java)
            startActivity(intent)
        }
    }

    //Create a dialog with random password
    private fun createGenerateDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.generate_password_dialog,null)
        val passwordValue = dialogView.findViewById<TextView>(R.id.password_value)
        val passwordName = dialogView.findViewById<EditText>(R.id.password_name)
        //Set random password to TextView
        passwordValue.setText(generateRandomPassword())
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)
        //Add buttons to dialog
        dialogBuilder.setNegativeButton("cancel", { _, _ -> })
        dialogBuilder.setPositiveButton("save", { _, _ -> })
        val generatePasswordDialog = dialogBuilder.create()
        generatePasswordDialog.show()
        generatePasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
            //Chech if password's name isn't empty
            if (passwordName.text.isEmpty()) {
                Toast.makeText(baseContext, "Password name not valid", Toast.LENGTH_SHORT).show()
            }else {
                //Add a new password to database
                val password = Password()
                password.passwordName = passwordName.text.toString()
                password.passwordValue = passwordValue.text.toString()
                PasswordsActivity.dbHandler.addPassword(this, password)
                generatePasswordDialog.cancel()
                //Show InterstitialAd
                if(interstitialAd.isLoaded)
                    interstitialAd.show()
            }
        }
    }

    //Create a dialog with user's custom password
    private fun createAddDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.add_password_dialog,null)
        val passwordValue = dialogView.findViewById<EditText>(R.id.password_value)
        val passwordName = dialogView.findViewById<EditText>(R.id.password_name)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)
        //Add buttons to dialog
        dialogBuilder.setNegativeButton("cancel", { _, _ -> })
        dialogBuilder.setPositiveButton("save", { _, _ -> })
        val addPasswordDialog = dialogBuilder.create()
        addPasswordDialog.show()
        addPasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
            //Chech if password's value and password's name isn't empty
            if (passwordValue.text.isEmpty() or passwordName.text.isEmpty()) {
                Toast.makeText(baseContext, "Please, fill in all the fields", Toast.LENGTH_SHORT).show()
            }else {
                //Add a new password to database
                val password = Password()
                password.passwordName = passwordName.text.toString()
                password.passwordValue = passwordValue.text.toString()
                PasswordsActivity.dbHandler.addPassword(this, password)
                addPasswordDialog.cancel()
                if(interstitialAd.isLoaded)
                    interstitialAd.show()
            }
        }
    }

    //Generate random 10-symbols password
    private fun generateRandomPassword(): String {
        val chars = getString(R.string.chars)
        var password = ""
        for (i in 0..9) {
            password += chars[Math.floor(Math.random() * chars.length).toInt()]
        }
        return password
    }

}