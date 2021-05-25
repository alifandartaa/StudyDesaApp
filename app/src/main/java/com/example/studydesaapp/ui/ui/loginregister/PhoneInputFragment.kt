package com.example.studydesaapp.ui.ui.loginregister

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studydesaapp.R
import com.example.studydesaapp.databinding.FragmentPhoneInputBinding
import com.example.studydesaapp.ui.DashboardActivity
import com.google.firebase.auth.FirebaseAuth

class PhoneInputFragment : Fragment() {

    private lateinit var fragmentPhoneInputBinding: FragmentPhoneInputBinding
    var textNumberPhone: String? = null
    lateinit var firebaseAuth: FirebaseAuth

    companion object {
        const val KEY_PHONE = "KEY_PHONE"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentPhoneInputBinding =
            FragmentPhoneInputBinding.inflate(layoutInflater, container, false)
        return fragmentPhoneInputBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            val intent = Intent(context, DashboardActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        fragmentPhoneInputBinding.btnPhoneInput.setOnClickListener {
            if (fragmentPhoneInputBinding.txtNumberPhone.text.toString() == "") {
                fragmentPhoneInputBinding.txtNumberPhone.error = "Harap isi nomor telepon"
            } else {
                textNumberPhone =
                    fragmentPhoneInputBinding.txtNumberPhone.text.toString().substring(1)
                enterNumberPhone(textNumberPhone)
            }
        }
    }

    private fun enterNumberPhone(textNumberPhone: String?) {
        val bundle = Bundle()
        bundle.putString(KEY_PHONE, textNumberPhone)
        val otpInputFragment = OtpInputFragment()
        otpInputFragment.arguments = bundle

        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.fragment_container, otpInputFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }
}