package com.android.based.data.myapplication.listener

interface OrderListener {
    fun onAccept(position:Int)
    fun onReject(position:Int)
}