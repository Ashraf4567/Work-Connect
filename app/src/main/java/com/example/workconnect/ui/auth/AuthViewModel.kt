package com.example.workconnect.ui.auth

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workconnect.R
import com.example.workconnect.utils.SingleLiveEvent
import com.example.workconnect.utils.UiState
import com.example.workconnect.data.local.SessionManager
import com.example.workconnect.data.usersRepo.UsersRepository
import com.example.workconnect.data.model.User
import com.example.workconnect.data.network.WebServices
import com.example.workconnect.ui.tabs.chat.chatRoom.UserProvider
import com.example.workconnect.utils.UserResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    val sessionManager: SessionManager,
    val auth: FirebaseAuth,
    val usersRepository: UsersRepository,
    private val webServices: WebServices
) : ViewModel() {

    val userName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val passwordConfirmation = MutableLiveData<String>()
    var userType: String = ""

    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()
    val userNameError = MutableLiveData<String?>()
    val passwordConfirmationError = MutableLiveData<String?>()

    val userLiveData = MutableLiveData<User?>()
    val state = SingleLiveEvent<UiState>()
    val isManager = MutableLiveData<Boolean>()
    val uiState = SingleLiveEvent<UiState>()

//    var photos : File? = null

    fun checkUserType() {
        if (sessionManager.getUserData()?.type.equals(User.MANAGER)) {
            isManager.postValue(true)
        } else {
            isManager.postValue(false)
        }
    }


    fun onRadioGroupChanged(checkedId: Int) {
        when (checkedId) {
            R.id.employee -> userType = User.EMPLOYEE
            R.id.manager -> userType = User.MANAGER
        }
    }

    var isLoading = MutableLiveData<Boolean>()


    suspend fun logIn() {


        try {
            isLoading.postValue(true)
            if (validForm()) {
                val authResult = withContext(Dispatchers.IO) {
                    auth.signInWithEmailAndPassword(email.value!!, password.value!!).await()
                }

                // Check if authentication was successful
                if (authResult.user != null) {
                    val user = User(name = "Ashraf", email = email.value, id = authResult.user?.uid)

                    getUserData(user.id)

                } else {
                    state.postValue(UiState.ERROR)
                    // Handle authentication errors:
                }
            }
        } catch (e: Exception) {
            // Handle other non-authentication errors
            state.postValue(UiState.ERROR)
            e.printStackTrace()
        } finally {
            isLoading.postValue(false)
        }
    }

    fun addAccount() {
        if (!validAddAccountForm()) return
        // Start loading
        isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Perform the asynchronous operation, e.g., createUserWithEmailAndPassword
                val task = auth.createUserWithEmailAndPassword(email.value!!, password.value!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Handle successful account creation

                            val userId = task.result.user?.uid
                            val user = User(userId, userName.value, email.value, userType, 0)
                            Log.d("test save data", user.toString())

                            addUser(user)
                        } else {
                            // Handle failed account creation
                            state.postValue(UiState.ERROR)
                        }

                    }
                    .await() // Use await to suspend until the task completes
            } catch (e: Exception) {
                e.printStackTrace()
                state.postValue(UiState.ERROR)
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    fun enrolPersonFace(file: File) {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val photoPart = MultipartBody.Part.createFormData("photos", file.name, requestFile)

                val namePart = userName.value?.toRequestBody("text/plain".toMediaTypeOrNull())
                val storePart = "3".toRequestBody("text/plain".toMediaTypeOrNull())
                val collectionsPart = "persons".toRequestBody("text/plain".toMediaTypeOrNull())

                val res = webServices.enrolPerson(namePart!!, storePart, collectionsPart, photoPart)

                if (res.isSuccessful) {
                    isLoading.postValue(false)
                    val responseBody = res.body()
                    if (responseBody != null) {
                        // Handle successful response with body
                        Log.d("response face", "Enrollment successful! Response: ${responseBody}")
                    } else {
                        Log.e("response face", "Enrollment successful, but response body is null!")
                    }
                } else {
                    isLoading.postValue(false)
                    val errorBody = res.errorBody()?.string()
                    Log.e("response face", "Error: ${res.code()} - $errorBody")
                }
            } catch (e: Exception) {
                Log.e("response face", "Error: ${e.message}", e)
                isLoading.postValue(false)
            }
        }
    }







    suspend fun getUserData(id: String?) {
        uiState.postValue(UiState.LOADING)
        try {
            val result = usersRepository.getUserById(id)

            when (result) {
                is UserResult.Success -> {
                    val user = result.user
                    // Cache user data as session manager
                    userLiveData.postValue(user)
                    uiState.postValue(UiState.SUCCESS)
                    sessionManager.saveUserData(user)
                    UserProvider.user = user
                    checkUserType()
                    state.postValue(UiState.SUCCESS)
                }

                is UserResult.Failure -> {
                    Log.d("test save data", "Failed: ${result.exception.localizedMessage}")
                    uiState.postValue(UiState.ERROR)
                }

                else -> {}
            }
            Log.d("test save data", "End getUserData coroutine")
        } catch (e: CancellationException) {
            Log.d("test save data", "Coroutine canceled: ${e.localizedMessage}")
            e.printStackTrace() // Log the stack trace for debugging
        } catch (e: Exception) {
            Log.e("test save data", "Exception in getUserData coroutine: ${e.localizedMessage}")
        }

    }

    private fun addUser(user: User) = viewModelScope.launch {
        val result = usersRepository.insertUserToFirestore(user)
        if (result.isFailure) {
            state.postValue(UiState.ERROR)
        }
        if (result.isSuccess) {

            //enrolPersonFace()
            state.postValue(UiState.SUCCESS)
            Log.d("test save data", "saved in firestore" + result.isSuccess)
        }
    }

    private fun validAddAccountForm(): Boolean {
        var isValid = true
        if (userName.value.isNullOrBlank()) {
            //showError
            userNameError.postValue("Please enter name")
            isValid = false
        } else {
            userNameError.postValue(null)
        }
        if (email.value.isNullOrBlank()) {
            //showError
            emailError.postValue("Please enter your email")
            isValid = false
        } else {
            emailError.postValue(null)
        }
        if (password.value.isNullOrBlank()) {
            //showError
            passwordError.postValue("please enter password")
            isValid = false
        } else {
            passwordError.postValue(null)
        }
        if (passwordConfirmation.value.isNullOrBlank() ||
            passwordConfirmation.value != password.value
        ) {
            //showError
            passwordConfirmationError.postValue("passwords unmatched")
            isValid = false
        } else {
            passwordConfirmationError.postValue(null)
        }
        return isValid
    }

    private fun validForm(): Boolean {
        var isValid = true

        if (email.value.isNullOrBlank()) {
            //showError
            emailError.postValue("Please enter your email")
            isValid = false
        } else {
            emailError.postValue(null)
        }
        if (password.value.isNullOrBlank()) {
            //showError
            passwordError.postValue("please enter the password")
            isValid = false
        } else {
            passwordError.postValue(null)
        }

        return isValid
    }
}
