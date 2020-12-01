package com.ahraar.phoneauth.ui.otp_verify

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ahraar.phoneauth.utils.AuthUtil
import com.ahraar.phoneauth.utils.ErrorMessage
import com.ahraar.phoneauth.utils.LoadState
import com.ahraar.phoneauth.utils.Utils.toast
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit


class AuthViewModel : ViewModel() {
    val TAG by lazy { "AuthViewModel" }

    private val loadingState = MutableLiveData<LoadState>()


    private val verificationId: MutableLiveData<String> = MutableLiveData()

    val credential: MutableLiveData<PhoneAuthCredential> = MutableLiveData()

    private val taskResult: MutableLiveData<Task<AuthResult>> = MutableLiveData()

    private val dataExist = MutableLiveData(false)

    private val auth = AuthUtil.firebaseAuthInstance


    fun sendOtp(activity: Activity, mobile: String): LiveData<LoadState>  {
        val number = "+91$mobile"
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(listener)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        return loadingState

    }

     fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    taskResult.value = task
                    loadingState.value = LoadState.SUCCESS
                    fetchUser(task)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    loadingState.value = LoadState.FAILURE
                }
            }
    }

    fun fetchUser(taskId: Task<AuthResult>) {
        val db = FirebaseFirestore.getInstance()
        val user = taskId.result?.user
        val noteRef = db.document("Users/" + user?.uid)
        noteRef.get()
            .addOnSuccessListener { data ->
                if (data.exists()) {  //already created user
                    //save profile in preference
                    dataExist.value = true
                }
            }.addOnFailureListener {
                dataExist.value = false
                loadingState.value = LoadState.FAILURE
            }
    }

    fun getUserData(): LiveData<Boolean> {
        return dataExist
    }

    fun getVerificationCode(): MutableLiveData<String> {
        return verificationId
    }

    fun getTaskResult(): LiveData<Task<AuthResult>> {
        return taskResult
    }

    fun clearOldAuth(){
        credential.value=null
        taskResult.value=null
    }

    fun getCredential(): LiveData<PhoneAuthCredential> {
        return credential
    }

    fun setVCodeNull() {
        verificationId.value = null
    }


    private val listener = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted:$credential")
            this@AuthViewModel.credential.value = credential
            signInWithPhoneAuthCredential(credential)



//            Handler(Looper.getMainLooper()).postDelayed({
//                signInWithPhoneAuthCredential(credential)
//            }, 1000)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            loadingState.value = LoadState.FAILURE
            Log.e(TAG, "onVerificationFailed: ${e.message}")

            if (e is FirebaseAuthInvalidCredentialsException) {
                Log.e(TAG, " ${e.message}")

            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.e(TAG, " ${e.message}")
            }

        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(verificationId, token)
            loadingState.value = LoadState.LOADING
            Log.d(TAG, "onCodeSent:$verificationId")
            this@AuthViewModel.verificationId.value = verificationId
            Log.d(TAG, "Verification code sent successfully")
        }

        override fun onCodeAutoRetrievalTimeOut(p0: String) {
            super.onCodeAutoRetrievalTimeOut(p0)
            loadingState.value = null

        }
    }


    fun doneNavigating() {
        loadingState.value = null
    }

}

