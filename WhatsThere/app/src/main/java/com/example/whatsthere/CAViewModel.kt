package com.example.whatsthere

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.whatsthere.data.COLLECTION_USER
import com.example.whatsthere.data.Event
import com.example.whatsthere.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class CAViewModel @Inject constructor(

    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage

    ): ViewModel() {


    val inProgress = mutableStateOf(false)

    val popupNotification = mutableStateOf<Event<String>?>(null)
    val singedIn = mutableStateOf(false)

    fun onSignup(name: String, number: String, email: String, password: String){
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()){
            handleException(customMessage = "Please fill in all fields to continue")
            return
        }

        inProgress.value = true
        db.collection(COLLECTION_USER).whereEqualTo("number", number)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty)
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                               singedIn.value = true
                                //crate profile
                                createOrUpdateProfile(name = name, number = number)
                            }else
                                handleException(task.exception, "Sign up failed")
                        }
                else
                    handleException(customMessage = "Number already exist")
                inProgress.value = false
            }
            .addOnFailureListener{
                handleException(it)
            }

    }

    private fun createOrUpdateProfile(

        name: String? = null,
        number: String? = null,
        imageUrl: String? = null

    ){

        val uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name,
            number = number,
            imageUrl = imageUrl
        )

        uid?.let { uid ->

            inProgress.value = true
            db.collection(COLLECTION_USER).document(uid)
                .get()
                .addOnSuccessListener {
                    if (it.exists()){
                        //update existing user
                        it.reference.update(userData.toMap())
                            .addOnSuccessListener {
                                inProgress.value = false
                            }
                            .addOnFailureListener {
                                handleException(it, "Cannot update user")
                            }
                    }
                    else {
                        //Create new user
                        db.collection(COLLECTION_USER).document(uid).set(userData)
                        inProgress.value = false
                    }
                }
                .addOnFailureListener {
                    handleException(it, "Cannot retrevie user - no connection?")
                }

        }

    }

    private fun handleException(exception: Exception? = null, customMessage: String = ""){
        Log.e("Whats There", "App exception", exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage: $errorMsg"
        popupNotification.value = Event(message)

        inProgress.value = false
    }

// For popUp error test
//    init {
//        handleException(customMessage = "Test")
//    }
}