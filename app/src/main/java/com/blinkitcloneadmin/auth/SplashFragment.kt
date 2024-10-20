package com.blinkitcloneadmin.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blinkitcloneadmin.activities.AdminMainActivity
import com.blinkitcloneadmin.R
import com.blinkitcloneadmin.utils.Utils
import com.blinkitcloneadmin.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private val viewModel : AuthViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Utils.setStatusBarColor(requireContext(), R.color.yellow, requireActivity())
        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                viewModel.isCurrentUser.collect{
                    if (it) {
                        startActivity(
                            Intent(
                                requireActivity(),
                                AdminMainActivity::class.java
                            )
                        )
                        requireActivity().finish()
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
                    }
                }
            }
        }, 3000)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

}