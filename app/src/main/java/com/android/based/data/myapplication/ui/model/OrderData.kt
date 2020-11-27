package com.android.based.data.myapplication.ui.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OrderData {
    @SerializedName("ItemList")
    @Expose
    var itemList: List<ItemList> = ArrayList()

    @SerializedName("OrderId")
    @Expose
    var orderId: String? = null

    @SerializedName("DeliveryTime")
    @Expose
    var deliveryTime: Int? = null

    @SerializedName("DeliveryTimeUnit")
    @Expose
    var deliveryTimeUnit: String? = null

    @SerializedName("StoreLocation")
    @Expose
    var storeLocation: String? = null

    @SerializedName("DeliveryLocation")
    @Expose
    var deliveryLocation: String? = null

    private var totalOrderPrice=0.0


    public fun getOrderPrice(): Double {
        var counter=0.0
        for (item in  itemList!!){
            counter+=item.getTotalPrice()
        }
        totalOrderPrice=counter
        return totalOrderPrice
    }

}