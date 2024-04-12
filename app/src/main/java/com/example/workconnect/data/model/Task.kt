package com.example.workconnect.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Parcelize
data class Task(
    val id: String? = null,
    val projectId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val taskCompletionState: String? = null,
    val dateTime: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a")),
    val creatorName: String? = null,
    val points: String? = null,
    val workerName: String? = null
) : Parcelable

