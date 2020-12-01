package com.ahraar.phoneauth.utils

import com.google.firebase.auth.FirebaseAuth


object AuthUtil {

    val firebaseAuthInstance: FirebaseAuth by lazy {
        println("firebaseAuthInstance.:")
        FirebaseAuth.getInstance()
    }


    fun getAuthId(): String {
        return firebaseAuthInstance.currentUser?.uid.toString()
    }

    fun getCurrentUserName(): String {
        return firebaseAuthInstance.currentUser?.displayName.toString()
    }

    fun logOutUser() {
        return firebaseAuthInstance.signOut()
    }
}