package com.example.workconnect.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    var id: String? = null,
    var title: String? = null,
    var description: String? = null,
) : Parcelable
