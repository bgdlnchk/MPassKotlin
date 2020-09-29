package com.my.admin.password_manager.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.goodiebag.pinview.Pinview
import com.my.admin.password_manager.R

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CreatePinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pin)

        //Initialize createPinView
        val createPinView = findViewById<Pinview>(R.id.createPinView)

        //Create listener for createPinView, which will work after full PIN entering
        createPinView.setPinViewEventListener { _, _ ->
            //Automatically close the pinView's keyboard after entering PIN
            val view : View = this.currentFocus
            val inputMethodManager : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
            //Get passwordValue string from SharedPreferences and pass PIN value in it
            val settings : SharedPreferences = getSharedPreferences("PREFS", 0)
            val editor : SharedPreferences.Editor = settings.edit()
            editor.putString("pinValue", createPinView.value)
            editor.apply()
            Toast.makeText(this, "Pin saving...", Toast.LENGTH_SHORT).show()
            //Open the MainActivity after entering PIN
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        }

    }
}
