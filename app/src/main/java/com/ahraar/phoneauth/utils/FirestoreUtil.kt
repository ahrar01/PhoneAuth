package com.ahraar.phoneauth.utils

import com.google.firebase.firestore.FirebaseFirestore


object FirestoreUtil {

    val firestoreInstance: FirebaseFirestore by lazy {

        val firebaseFirestore = FirebaseFirestore.getInstance()

        firebaseFirestore
    }


}