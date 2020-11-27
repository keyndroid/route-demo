package com.android.based.data.myapplication.ui.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ItemList {
    @SerializedName("Quantity")
    @Expose
    var quantity: Int? = null

    @SerializedName("OrderName")
    @Expose
    var orderName: String? = null

    @SerializedName("PricePerUnit")
    @Expose
    var pricePerUnit: Double? = null

    private var totalPrice:Double=0.0

    public fun getTotalPrice(): Double {
        totalPrice = quantity!!*pricePerUnit!!
        return totalPrice
    }
}