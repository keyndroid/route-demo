package com.android.based.data.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.based.data.myapplication.R
import com.android.based.data.myapplication.databinding.AdapterHomeBinding
import com.android.based.data.myapplication.listener.OrderListener
import com.android.based.data.myapplication.ui.model.OrderData


class OrderAdapter  (private val mOrderList: List<OrderData>) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    lateinit var orderListener: OrderListener

    public fun setOnOrderListener( orderListener: OrderListener){
        this.orderListener=orderListener
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(val binding: AdapterHomeBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        fun bind(position: Int) {
//            ItemList
            val orderData =mOrderList.get(position)
            binding.orderData=orderData
            val adapter = ItemAdapter(orderData.itemList)
            // Attach the adapter to the recyclerview to populate items
            // Attach the adapter to the recyclerview to populate items
            binding.rvItems.adapter = adapter
            // Set layout manager to position the items
            // Set layout manager to position the items
        }

        init {
            val layoutManager = LinearLayoutManager(itemView.context)
            binding.rvItems.layoutManager = layoutManager

            val dividerItemDecoration = DividerItemDecoration(
                binding.rvItems.getContext(),
                layoutManager.getOrientation()
            )
            binding.rvItems.addItemDecoration(dividerItemDecoration)
            binding.rvItems.isNestedScrollingEnabled=false
            binding.btAccept.setOnClickListener(this)
            binding.btReject.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (v?.equals(binding.btAccept)==true){
                orderListener.onAccept(adapterPosition)
            }else if (v?.equals(binding.btReject)==true){
                orderListener.onReject(adapterPosition)
            }
        }
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
//        val nameTextView = itemView.findViewById<TextView>(R.id.contact_name)
//        val messageButton = itemView.findViewById<Button>(R.id.message_button)
    }

    // ... constructor and member variables
    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
//        val contactView = inflater.inflate(R.layout.adapter_home, parent, false)
        // Return a new holder instance
        val binding: AdapterHomeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.getContext()),
            R.layout.adapter_home, parent, false
        )
        return ViewHolder(binding)
//        return ViewHolder(contactView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: OrderAdapter.ViewHolder, position: Int) {
        // Get the data model based on position
//        val contact: Contact = mContacts.get(position)
        viewHolder.bind(position)

        // Set item views based on your views and data model
//        val textView = viewHolder.nameTextView
//        textView.setText(contact.name)
//        val button = viewHolder.messageButton
//        button.text = if (contact.isOnline) "Message" else "Offline"
//        button.isEnabled = contact.isOnline
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mOrderList.size
    }
}