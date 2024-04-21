package com.example.workconnect.ui.attendance

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workconnect.data.local.SessionManager
import com.example.workconnect.data.model.AttendanceHistory
import com.example.workconnect.data.model.AttendanceRecord
import com.example.workconnect.data.network.WebServices
import com.example.workconnect.utils.SingleLiveEvent
import com.example.workconnect.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    val webServices: WebServices,
    val sessionManager: SessionManager,
    private val db: FirebaseFirestore
): ViewModel() {

    val uiState = SingleLiveEvent<UiState>()

    val employeeId = sessionManager.getUserData()?.id.toString()

    val attendanceHistory = MutableLiveData<MutableList<AttendanceHistory>>()


    private suspend fun updateAttendanceHistory(employeeId: String, action: String, timestamp: Long) {
        // Assuming you have a Firestore collection named "attendance_history"
        db.collection("attendance_history")
            .document(employeeId)
            .collection("records")
            .add(mapOf("action" to action, "timestamp" to timestamp))
            .await()
    }
    // Function to calculate the duration between two timestamps
    private fun calculateDuration(checkInTime: Long, checkOutTime: Long): Long {
        return checkOutTime - checkInTime
    }

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
                        updateAttendanceHistory(employeeId, "checkIn", System.currentTimeMillis())
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
                        updateAttendanceHistory(employeeId, "checkOut", System.currentTimeMillis())
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

    fun recognizePersonFace(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestFile)

                val collections = "persons".toRequestBody("text/plain".toMediaTypeOrNull())

                val res = webServices.recognizePerson(collections , photoPart)


                    Log.e("recognizePersonFace", "Response : ${res.body()?.recognizeFaceResponse?.get(0)?.name}")


            } catch (e: Exception) {
                Log.e("response face", "Error: ${e.message}", e)
                uiState.postValue(UiState.ERROR)
            }
        }
    }

    fun getAttendanceHistory(id: String?) {
        uiState.postValue(UiState.LOADING)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Fetch attendance records from Firestore
                val querySnapshot = db.collection("attendance_history")
                    .document(id?:"")
                    .collection("records")
                    .orderBy("timestamp")
                    .get()
                    .await()

                // Process attendance records
                val records = mutableListOf<AttendanceRecord>()
                querySnapshot.documents.forEach { document ->
                    val action = document.getString("action")
                    val timestamp = document.getLong("timestamp")
                    if (action != null && timestamp != null) {
                        records.add(AttendanceRecord(action, timestamp))
                    }
                }

                // Calculate durations and format attendance history
                val formattedAttendanceHistory = mutableListOf<AttendanceHistory>()
                for (i in 0 until records.size step 2) {
                    if (i + 1 < records.size) {
                        val checkInTime = records[i].timestamp
                        val checkOutTime = records[i + 1].timestamp
                        val duration = calculateDuration(checkInTime, checkOutTime)

                        val checkInFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(checkInTime))
                        val checkOutFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(checkOutTime))
                        val durationFormatted = formatDuration(duration)

                        val attendanceHistory = AttendanceHistory(checkInFormatted, checkOutFormatted, durationFormatted)
                        formattedAttendanceHistory.add(attendanceHistory)
                    }
                }
                delay(1000)
                attendanceHistory.postValue(formattedAttendanceHistory)
                uiState.postValue(UiState.SUCCESS)

                // Log or post the formatted attendance history as needed
                formattedAttendanceHistory.forEach { Log.d("AttendanceHistory", it.toString()) }

            } catch (e: Exception) {
                Log.e("getAttendanceHistory", "Error: ${e.message}", e)
                uiState.postValue(UiState.ERROR)
                // Handle error
            }
        }
    }


    fun formatDuration(durationMillis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


}