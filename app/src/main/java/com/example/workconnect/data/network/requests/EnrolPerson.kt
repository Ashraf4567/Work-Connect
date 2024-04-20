package com.example.workconnect.data.network.requests

import java.io.File

data class EnrolPerson(
    val name:String,
    val store: String = "1",
    val collections: String = "persons",
    val photos: File? = null
)
