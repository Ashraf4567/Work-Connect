package com.example.workconnect.data.usersRepo


import android.util.Log
import com.example.workconnect.data.model.User
import com.example.workconnect.utils.UserResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    firebaseFirestore: FirebaseFirestore
) : UsersRepository {
    private val usersCollection = firebaseFirestore.collection(User.COLLECTION_NAME)
    override suspend fun insertUserToFirestore(user: User): Result<Unit> {
        return try {
            val userId = user.id ?: usersCollection.document().id
            usersCollection.document(userId).set(user)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }


    override suspend fun getUserById(userId: String?): UserResult {
        return try {
            val userSnapshot = usersCollection.document(userId ?: "").get().await()
            UserResult.Success(userSnapshot.toObject(User::class.java))
        } catch (exception: Exception) {
            Log.e("getUserById", "Exception: ${exception.localizedMessage}")
            UserResult.Failure(exception)
        }
    }

    override suspend fun getAllUsers(): List<User> {
        try {
            val querySnapshot = usersCollection.get().await()
            return querySnapshot.toObjects(User::class.java)
        } catch (e: FirebaseFirestoreException) {
            Log.e("getAllUsers", "FirebaseFirestoreException: ${e.message}", e)
            throw e // Re-throw the exception for proper handling in the ViewModel
        } catch (e: Exception) {
            Log.e("getAllUsers", "Unexpected exception: ${e.message}", e)
            throw Exception("An error occurred while retrieving users.") // Provide a user-friendly message
        }
    }



}