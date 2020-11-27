package com.android.based.data.myapplication.util

import android.content.Context
import android.widget.TextView
import android.widget.TextView.BufferType
import androidx.databinding.BindingAdapter
import com.android.based.data.myapplication.ui.model.ItemList
import com.android.based.data.myapplication.ui.model.OrderData
import java.io.IOException
import java.io.InputStream
import java.lang.StringBuilder


object Utils {
    public const val DEFAULT_CURRENCY="$"
    fun loadJSONFromAsset(context: Context,jsonFile:String): String? {
        var json: String? = null
        json = try {
            val inputStream: InputStream = context.getAssets().open(jsonFile)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    @BindingAdapter("bind:time")
    @JvmStatic
    fun setOrderTime(view: TextView, orderData: OrderData?) {
        if (orderData!=null){
            val data=StringBuilder();
            data.append(orderData.deliveryTime)
            data.append(" ")
            data.append(orderData.deliveryTimeUnit)
            view.setText(data)
        }

    }

    @BindingAdapter("bind:qty")
    @JvmStatic
    fun setOrderQty(view: TextView, orderData: ItemList?) {
        if (orderData!=null){
            view.setText(orderData.quantity.toString())
        }

    }
    @BindingAdapter("unitPrice")
    @JvmStatic
    fun setOrderPrice(view: TextView, orderData: ItemList?) {
        if (orderData!=null){
            view.setText(DEFAULT_CURRENCY.plus(orderData.pricePerUnit.toString()))
        }

    }
    @BindingAdapter("totalPrice")
    @JvmStatic
    fun setItemPriceTotal(view: TextView, orderData: ItemList?) {
        if (orderData!=null){
            val pricTotal=orderData.getTotalPrice()
            view.setText(DEFAULT_CURRENCY.plus(pricTotal))
        }

    }
    @BindingAdapter("totalOrderPrice")
    @JvmStatic
    fun setTotalOrderPrice(view: TextView, orderData: OrderData?) {
        if (orderData!=null){
            val pricTotal=orderData.getOrderPrice()
            val orderPrice=String.format("%.2f", pricTotal)
            view.setText(DEFAULT_CURRENCY.plus(orderPrice))
        }

    }

}