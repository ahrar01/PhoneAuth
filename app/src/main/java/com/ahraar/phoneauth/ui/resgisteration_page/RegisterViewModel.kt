package com.ahraar.phoneauth.ui.resgisteration_page

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ahraar.phoneauth.utils.FirestoreUtil
import com.ahraar.phoneauth.utils.LoadState
import com.ahraar.phoneauth.utils.StorageUtil
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import java.util.*

class RegisterViewModel : ViewModel() {
    val TAG by lazy { "FIRESTORE_VIEW_MODEL" }

    val uploadImageLoadStateMutableLiveData = MutableLiveData<LoadState>()
    private lateinit var mStorageRef: StorageReference

    private val user = Firebase.auth.currentUser


    fun uploadBannerImageByUri(name: String, email: String, data: Uri?) {
        //show upload ui
        uploadImageLoadStateMutableLiveData.value = LoadState.LOADING

        mStorageRef = StorageUtil.storageInstance.reference
        val ref = mStorageRef.child("profile/${user?.uid}")
        val uploadTask = data?.let { ref.putFile(it) }

        uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                uploadImageLoadStateMutableLiveData.value = LoadState.FAILURE
            }
            ref.downloadUrl
        }?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                saveImageUriInFirebase(name, email, downloadUri)
            } else {
                uploadImageLoadStateMutableLiveData.value = LoadState.FAILURE
            }
        }
    }

    //save download uri of image in the user document
    private fun saveImageUriInFirebase(name: String, email: String, downloadUri: Uri?) {

        val data = hashMapOf(
            "date" to Timestamp(Date()),
            "name" to name,
            "email" to email,
            "profilePic" to downloadUri.toString()
        )

        FirestoreUtil.firestoreInstance.collection("User").document(user!!.uid).set(data)
            .addOnSuccessListener {
                uploadImageLoadStateMutableLiveData.value = LoadState.SUCCESS
            }
            .addOnFailureListener {
                uploadImageLoadStateMutableLiveData.value = LoadState.FAILURE
            }

    }


}