package com.android.based.data.myapplication.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.based.data.myapplication.R
import com.android.based.data.myapplication.listener.OrderListener
import com.android.based.data.myapplication.pref.UserPref
import com.android.based.data.myapplication.ui.adapter.OrderAdapter
import com.android.based.data.myapplication.ui.model.OrderData
import com.android.based.data.myapplication.util.Utils
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Type

class HomeActivity : AppCompatActivity() {
    val userPref:UserPref by lazy { UserPref(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        if (userPref.getTrip()!=null){
            MapActivity.start(this)
            finish()
            return
        }

        val jsonData = Utils.loadJSONFromAsset(this, "OrderData.json")
        val gson = Gson()
//        val jsonOutput = "Your JSON String"
        val listType: Type = object : TypeToken<List<OrderData>>() {}.type
        val posts: MutableList<OrderData> = gson.fromJson(jsonData, listType)
        // Initialize contacts
//        val contacts = Contact.createContactsList(20)
        // Create adapter passing in the sample user data
        // Create adapter passing in the sample user data
        val adapter = OrderAdapter(posts)
        // Attach the adapter to the recyclerview to populate items
        // Attach the adapter to the recyclerview to populate items
        rvContacts.adapter = adapter
        // Set layout manager to position the items
        // Set layout manager to position the items
        rvContacts.layoutManager = LinearLayoutManager(this)
        adapter.setOnOrderListener(object : OrderListener {
            override fun onAccept(position: Int) {
                confirmOrder(posts.get(position))
            }

            override fun onReject(position: Int) {
                openDialog(object : OnReject {
                    override fun onReject() {
                        posts.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }
                })
            }
        })
    }

    private fun confirmOrder(orderData: OrderData) {
        val time = orderData.deliveryTime!!
        val timeUnit = orderData.deliveryTimeUnit!!
        val initialPos = 47
        val endPos = initialPos + time.toString().length + timeUnit.length + 1
        val message = getString(R.string.lbl_confirm_time, time, timeUnit)
        val str = SpannableStringBuilder(message)
        str.setSpan(
            StyleSpan(Typeface.BOLD),
            initialPos,
            endPos,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val dialgBuilder = MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.ttl_accept_order))
            .setMessage(str)
            .setView(R.layout.view_accept)
//            .setNegativeButton(getString(R.string.lbl_cancel),null)
//            .setPositiveButton(getString(R.string.lbl_reject),null)
        // Add customization options here
        ;
        val alertDialog = dialgBuilder.create()


        alertDialog.setOnShowListener {
            val buttonCacenl = alertDialog.findViewById<MaterialButton>(R.id.btCancel)
            val btAccept = alertDialog.findViewById<MaterialButton>(R.id.btAccept)
            buttonCacenl?.setOnClickListener {
                alertDialog.dismiss()
            }
            btAccept?.setOnClickListener {

                userPref.saveTrip(orderData)
                alertDialog.dismiss()
                MapActivity.start(this)
                finish()
            }
        }

        alertDialog.show()
    }

    private fun openDialog(onReject: OnReject) {

        val dialgBuilder = MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.ttl_reject))
            .setMessage(getString(R.string.ttl_reject_description))
            .setView(R.layout.view_reject)
//            .setNegativeButton(getString(R.string.lbl_cancel),null)
//            .setPositiveButton(getString(R.string.lbl_reject),null)
        // Add customization options here
        ;
        val alertDialog = dialgBuilder.create()


        alertDialog.setOnShowListener {
            val buttonCacenl = alertDialog.findViewById<MaterialButton>(R.id.btCancel)
            val buttonReject = alertDialog.findViewById<MaterialButton>(R.id.btReject)
            buttonCacenl?.setOnClickListener {
                alertDialog.dismiss()
            }
            buttonReject?.setOnClickListener {
                onReject.onReject()
                alertDialog.dismiss()
            }
        }

        alertDialog.show()
    }

    interface OnReject {
        fun onReject()
    }
    companion object {
        fun start(context: Context){
            context.startActivity(Intent(context,HomeActivity::class.java))
        }
    }
}
