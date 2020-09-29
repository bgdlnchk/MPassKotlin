package com.my.admin.password_manager.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.my.admin.password_manager.models.Password
import com.my.admin.password_manager.activities.PasswordsActivity
import com.my.admin.password_manager.R
import kotlinx.android.synthetic.main.lo_password.view.*

class PasswordAdapter(private val mCtx: Context, private val passwords : ArrayList<Password>) : RecyclerView.Adapter<PasswordAdapter.ViewHolder>(){

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        //Initialize views from lo_password
        val cardView: CardView = itemView.findViewById(R.id.card_view)
        val copyBtn: Button = itemView.findViewById(R.id.copyButton)
        val txtPasswordName: TextView = itemView.txtPasswordName
        val txtPasswordValue: TextView = itemView.txtPasswordValue
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.lo_password,p0,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return passwords.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val password : Password = passwords[position]
        holder.txtPasswordName.text = password.passwordName
        holder.txtPasswordValue.text = password.passwordValue
        //Show "Delete dialog" after long click on password's card
        holder.cardView.setOnLongClickListener{
            val passwordName = password.passwordName
            //Create "Delete dialog"
            AlertDialog.Builder(mCtx)
                    .setTitle("Warning")
                    .setMessage("Are you sure to delete : $passwordName ?")
                    .setPositiveButton("Yes") { _, _ ->
                        if (PasswordsActivity.dbHandler.deletePassword(password.passwordID)){
                            //Delete a password from RecycleView
                            passwords.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, passwords.size)
                            Toast.makeText(mCtx, "Password $passwordName has been deleted", Toast.LENGTH_SHORT).show()
                        }else Toast.makeText(mCtx, "Error deleting", Toast.LENGTH_SHORT).show()

                    }
                    .setNegativeButton("No") { _, _ -> }
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .show()
            return@setOnLongClickListener true
        }

        //Set onClickListener on "copyBtn'
        holder.copyBtn.setOnClickListener {
            //Copy a passwordValue
            val passwordValue = password.passwordValue
            val clipboardManager : ClipboardManager = mCtx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val data : ClipData = ClipData.newPlainText("copy text", passwordValue)
            clipboardManager.primaryClip = data
            Toast.makeText(mCtx,"Password $passwordValue copied", Toast.LENGTH_SHORT).show()
        }
    }

}