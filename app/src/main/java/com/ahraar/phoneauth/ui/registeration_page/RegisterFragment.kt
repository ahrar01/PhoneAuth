package com.ahraar.phoneauth.ui.registeration_page

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ahraar.phoneauth.R
import com.ahraar.phoneauth.databinding.RegisterFragmentBinding
import com.ahraar.phoneauth.utils.LoadState
import com.ahraar.phoneauth.utils.Utils
import com.ahraar.phoneauth.utils.hide
import com.ahraar.phoneauth.utils.show
import com.github.dhaval2404.imagepicker.ImagePicker
import es.dmoral.toasty.Toasty
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterFragment : Fragment() {

    private lateinit var binding: RegisterFragmentBinding

    private val viewModel by lazy { ViewModelProvider(this).get(RegisterViewModel::class.java) }

    private var uriData: Uri? = null
    private var IMAGE_STATUS = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RegisterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.selectProfilePic.setOnClickListener {
            selectImage()
        }

        binding.registerBtn.setOnClickListener {
            if (isFormValid()) {
                when {
                    Utils.isNoInternet(requireContext()) -> Utils.toast(
                        requireActivity(),
                        "No Internet Connection!"
                    )
                    else -> {
                        viewModel.uploadBannerImageByUri(
                            binding.userName.text.toString().trim(),
                            binding.emailEditText.text.toString().trim(),
                            uriData
                        )
                    }
                }

            }
        }

        //show loading layout while uploading
        viewModel.uploadImageLoadStateMutableLiveData.observe(
            viewLifecycleOwner,
            Observer { imageUploadState ->
                setBannerImageLoadUi(imageUploadState)
            })

    }

    private fun setBannerImageLoadUi(it: LoadState?) {
        when (it) {

            LoadState.SUCCESS -> {
                binding.registerBtn.isEnabled = true
                binding.loadingLayout.root.hide()
                this@RegisterFragment.findNavController()
                    .navigate(R.id.action_registerFragment_to_homeFragment)
                Toasty.success(requireContext(), "Upload Success!", Toast.LENGTH_SHORT, true)
                    .show()
            }
            LoadState.FAILURE -> {
                binding.registerBtn.isEnabled = true
                binding.loadingLayout.root.show()
                binding.issueLayout.root.hide()
                binding.issueLayout.textViewIssue.text = "Failed!! Please Try Again!!"
            }
            LoadState.LOADING -> {
                binding.registerBtn.isEnabled = false
                binding.loadingLayout.root.show()
                binding.issueLayout.root.hide()
            }
        }
    }

    private fun selectImage() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)         //Final image size will be less than 1 MB(Optional)
            .start()
    }

    private fun isFormValid(): Boolean {
        if (!IMAGE_STATUS) {
            Toasty.info(requireContext(), "Select A Profile Picture", Toast.LENGTH_LONG).show()
            return false
        }
        if (binding.userName.text.toString().trim().isEmpty()) {
            binding.userNameInput.error = "Enter Your Name"
            return false
        }
        if (binding.emailEditText.text.toString().trim().isEmpty()) {
            binding.userEmailInput.error = "Enter Email"
            return false
        } else if (!isEmailValid(binding.emailEditText.text.toString().trim())) {
            binding.userEmailInput.error = "Enter correct email"
            return false
        }

        return true

    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)\$"
        val pattern: Pattern = Pattern.compile(emailRegex)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            IMAGE_STATUS = true

            //upload image
            uriData = data?.data
            val thumb_filePath = File(data?.data?.path)

            //viewModel.uploadBannerImageByUri(data?.data)
            binding.profilePic.setImageBitmap(BitmapFactory.decodeFile(thumb_filePath.absolutePath))

        } else if (resultCode == ImagePicker.RESULT_ERROR) {

        } else {

        }

    }

}