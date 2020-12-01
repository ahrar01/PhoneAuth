package com.ahraar.phoneauth.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ahraar.phoneauth.R
import com.ahraar.phoneauth.databinding.ProfileFragmentBinding
import com.ahraar.phoneauth.utils.AuthUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var binding: ProfileFragmentBinding

    private val viewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }
    var user = AuthUtil.firebaseAuthInstance.currentUser!!

    private val firestoreDB: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getUserData()


    }

    private fun getUserData() {
        val docRef = firestoreDB.collection("User").document(user.uid)
        // Get the document, forcing the SDK to use the offline cache
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Document found in the offline cache
                val document = task.result
                binding.emailview.text = document?.data?.get("email").toString()
                binding.mobileview.text = user.phoneNumber
                binding.nameTv.text = document?.data?.get("name").toString()

                val profileUrl = document?.data?.get("profilePic").toString()

                Glide.with(requireActivity()).load(profileUrl)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.loading_animation)
                            .error(R.drawable.user_placeholder)
                    )
                    .into(binding.profilePic)

            }
        }
    }

}