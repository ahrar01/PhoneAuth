package com.ahraar.phoneauth.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.ahraar.phoneauth.R
import com.ahraar.phoneauth.databinding.LoginFragmentBinding
import com.ahraar.phoneauth.ui.otp_verify.OTPVerifyFragmentDirections
import com.ahraar.phoneauth.utils.AuthUtil
import com.ahraar.phoneauth.utils.eventbus_events.KeyboardEvent
import es.dmoral.toasty.Toasty
import org.greenrobot.eventbus.EventBus

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //check if user has previously logged in
        if (AuthUtil.firebaseAuthInstance.currentUser != null) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        binding.generateBtn.setOnClickListener {
            sendOtp()
        }

    }

    private fun sendOtp() {
        EventBus.getDefault().post(KeyboardEvent())

        val options = navOptions {
            anim {
                enter = R.anim.slide_in_right
                exit = R.anim.slide_out_left
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }

        if (binding.phoneNumberText.text.isEmpty() || binding.phoneNumberText.text.length < 10){
            Toasty.info(
                requireContext(),
                "Please Enter your 10 digit number",
                Toast.LENGTH_SHORT
            ).show()
        }else{

            val action = LoginFragmentDirections.actionLoginFragmentToOTPVerifyFragment(binding.phoneNumberText.text.toString())

            this@LoginFragment.findNavController()
                .navigate(action, options)
        }

    }

}