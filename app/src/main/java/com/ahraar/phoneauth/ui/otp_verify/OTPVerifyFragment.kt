package com.ahraar.phoneauth.ui.otp_verify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ahraar.phoneauth.R
import com.ahraar.phoneauth.databinding.OTPVerifyFragmentBinding
import com.ahraar.phoneauth.utils.LoadState
import com.ahraar.phoneauth.utils.Utils
import com.ahraar.phoneauth.utils.Utils.toast
import com.google.firebase.auth.PhoneAuthProvider
import es.dmoral.toasty.Toasty

class OTPVerifyFragment : Fragment() {

    private lateinit var binding: OTPVerifyFragmentBinding

    private val viewModel by activityViewModels<AuthViewModel>()

    private val args: OTPVerifyFragmentArgs by navArgs()

    private var verificationCode = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OTPVerifyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        subscribeObservers()

        binding.verifyBtn.setOnClickListener {
            if (binding.otpTextView.text.isNullOrEmpty()){
                Toasty.info(requireContext(),"Please enter OTP")
            }else{
                verifyOTP(verificationCode,binding.otpTextView.text.toString())
            }
        }

    }

    private fun verifyOTP(verificationCode: String, OTP: String) {
        val credential = PhoneAuthProvider.getCredential(verificationCode, OTP)
        viewModel.signInWithPhoneAuthCredential(credential)
    }

    private fun subscribeObservers() {
        when {
            Utils.isNoInternet(requireContext()) -> toast(
                requireActivity(),
                "No Internet Connection!"
            )
            else -> {
                viewModel.sendOtp(requireActivity(), args.number).observe(viewLifecycleOwner,
                    Observer { loadState ->
                        when (loadState) {
                            LoadState.SUCCESS -> {
                                binding.otpProgressBar.visibility = View.GONE
                                viewModel.getUserData().observe(viewLifecycleOwner, {
                                    if (it) {
                                        this@OTPVerifyFragment.findNavController()
                                            .navigate(R.id.action_OTPVerifyFragment_to_homeFragment)
                                        viewModel.doneNavigating()
                                    } else {
                                        this@OTPVerifyFragment.findNavController()
                                            .navigate(R.id.action_OTPVerifyFragment_to_registerFragment2)
                                        viewModel.doneNavigating()
                                    }
                                })

                            }
                            LoadState.LOADING -> {
                                binding.otpProgressBar.visibility = View.VISIBLE

                            }
                            LoadState.FAILURE -> {
                                binding.verifyBtn.isEnabled = true
                                binding.otpProgressBar.visibility = View.GONE
                            }
                        }
                    })
            }
        }

        viewModel.getTaskResult().observe(viewLifecycleOwner,{ taskId->
            if (taskId!=null && viewModel.getCredential().value?.smsCode.isNullOrEmpty())
                viewModel.fetchUser(taskId)

        })

        viewModel.getVerificationCode().observe(viewLifecycleOwner,{
            verificationCode = it
        })


    }

}