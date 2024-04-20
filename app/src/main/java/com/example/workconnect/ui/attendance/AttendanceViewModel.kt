package com.example.workconnect.ui.attendance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workconnect.data.local.SessionManager
import com.example.workconnect.data.model.Attendance
import com.example.workconnect.data.network.WebServices
import com.example.workconnect.utils.SingleLiveEvent
import com.example.workconnect.utils.UiState
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    val webServices: WebServices,
    val sessionManager: SessionManager
): ViewModel() {

    val uiState = SingleLiveEvent<UiState>()


    fun checkInWithFace(file: File) {
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)

                val roomPart = "IT".toRequestBody("text/plain".toMediaTypeOrNull())

                val res = webServices.checkIn(roomPart, photoPart)

                if (res.isSuccessful && res.body()!=null){
                    if (res.body()!!.status.equals("failure")){
                        uiState.postValue(UiState.ERROR)
                        Log.e("checkIn", "Fail : ${res.body()}")
                    }else{
                        checkIn(sessionManager.getUserData()?.id.toString())
                        uiState.postValue(UiState.SUCCESS)

                    }

                    Log.e("checkIn", "Response : ${res.body()!!.status}")

                }
            } catch (e: Exception) {
                Log.e("response face", "Error: ${e.message}", e)
                uiState.postValue(UiState.ERROR)
            }
        }
    }

    fun checkOutWithFace(file: File) {
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)

                val roomPart = "IT".toRequestBody("text/plain".toMediaTypeOrNull())

                val res = webServices.checkOut(roomPart, photoPart)

                if (res.isSuccessful && res.body()!=null){
                    if (res.body()!!.status.equals("failure")){
                        uiState.postValue(UiState.ERROR)
                        Log.e("checkOut", "Fail : ${res.body()}")
                    }else{
                        checkOut(sessionManager.getUserData()?.id.toString())
                        uiState.postValue(UiState.SUCCESS)

                    }

                    Log.e("checkOut", "Response : ${res.body()!!.status}")

                }
            } catch (e: Exception) {
                Log.e("response face", "Error: ${e.message}", e)
                uiState.postValue(UiState.ERROR)
            }
        }
    }


    fun checkIn(employeeId: String) {
        // Use Luxand API (or your face recognition method) to identify employee
        // ...

        // Get current date and time
        val now = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDate = formatter.format(now)

        // Create Attendance object
        val attendance = Attendance(
            employeeId = employeeId,
            date = currentDate,
            checkInTime = currentDate
        )

        // Save attendance to Firestore
        val db = FirebaseFirestore.getInstance()
        val documentId = "$employeeId${currentDate}"
        db.collection("attendance").document(documentId).set(attendance)
            .addOnSuccessListener {
                // Attendance saved successfully
            }
            .addOnFailureListener { exception ->
                // Handle saving error
                Log.e("checkIn", "Error saving attendance: $exception")
            }
    }

    fun checkOut(employeeId: String) {

        // Get current date and time
        val now = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentTime = formatter.format(now)

        // Retrieve existing attendance document for today
        val db = FirebaseFirestore.getInstance()
        val documentId = "$employeeId${SimpleDateFormat("yyyy-MM-dd").format(now)}"
        db.collection("attendance").document(documentId).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Attendance document found, update checkOutTime and calculate duration
                    val attendance = documentSnapshot.toObject<Attendance>() ?: return@addOnSuccessListener

                    attendance.checkOutTime = currentTime
                    val checkInTimeInMillis = Date(attendance.checkInTime).time
                    val checkOutTimeInMillis = Date(currentTime).time
                    val durationInMinutes = (checkOutTimeInMillis - checkInTimeInMillis) / (1000 * 60)
                    attendance.durationInMinutes = durationInMinutes

                    // Update document with checkOutTime and duration
                    db.collection("attendance").document(documentId).set(attendance)
                        .addOnSuccessListener {
                            // Attendance updated successfully
                        }
                        .addOnFailureListener { exception ->
                            // Handle updating error
                            Log.e("checkOut", "Error updating attendance: $exception")
                        }
                } else {
                    // No attendance record for today, handle error (optional)
                    Log.e("checkOut", "No attendance record found for today")
                }
            }
            .addOnFailureListener { exception ->
                // Handle error retrieving document
                Log.e("checkOut", "Error retrieving attendance: $exception")
            }
    }


}