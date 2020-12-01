package com.ahraar.phoneauth.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ahraar.phoneauth.R
import com.ahraar.phoneauth.databinding.HomeFragmentBinding
import com.ahraar.phoneauth.utils.AuthUtil

class HomeFragment : Fragment() {
    private lateinit var binding: HomeFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.logoutBtn.setOnClickListener {
            AuthUtil.logOutUser().also {
                this@HomeFragment.findNavController()
                    .navigate(R.id.action_homeFragment_to_loginFragment)
            }

        }

    }

}