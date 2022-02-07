package com.seoplee.androidstudy.data.entity.passenger

data class Data (
    val _id : String,
    val name : String,
    val trips : Int,
    val airline : List<Airline>,
    val __v : Int
        )