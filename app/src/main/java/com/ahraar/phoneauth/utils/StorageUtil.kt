package com.ahraar.phoneauth.utils

import com.google.firebase.storage.FirebaseStorage

object StorageUtil {

    val storageInstance: FirebaseStorage by lazy {
        println("StorageUtil.:")
        FirebaseStorage.getInstance()
    }

}