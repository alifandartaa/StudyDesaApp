package com.example.studydesaapp.ui.ui.loginregister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.studydesaapp.databinding.FragmentOtpInputBinding
import com.example.studydesaapp.ui.DashboardActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import java.util.concurrent.TimeUnit

class OtpInputFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    var numberPhone: String? = null
    private lateinit var otpInputBinding: FragmentOtpInputBinding

    companion object {
        const val KEY_PHONE = "KEY_PHONE"
        const val EXTRA_PHONE = "EXTRA_PHONE"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        otpInputBinding = FragmentOtpInputBinding.inflate(layoutInflater, container, false)
        return otpInputBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //instance firebase
        firebaseAuth = FirebaseAuth.getInstance()

        //instance reference
        database = FirebaseDatabase.getInstance()
        reference = database.getReference()

        val bundle = arguments
        if (bundle != null) {
            numberPhone = bundle.getString(KEY_PHONE)
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("Verification completed", "onVerificationCompleted:$credential")
                val code = credential.smsCode!!
                otpInputBinding.txtOtp.setText(code)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("Verification Failed", "onVerificationFailed", e)
                Toast.makeText(context, "Auth Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
            }
        }

        sendOtpCode("+62$numberPhone")

        otpInputBinding.btnOtpInput.setOnClickListener {
            if(otpInputBinding.txtOtp.text.toString() == ""){
                otpInputBinding.txtOtp.error = "Harap isi kode OTP"
            }else{
                val otpCode = otpInputBinding.txtOtp.text.toString()

                if (otpCode.isNotEmpty()) {
                    getCredential(otpCode)
                } else {
                    Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show()
                }
            }
        }

        otpInputBinding.btnResendOtp.setOnClickListener {
            resendOtpCode("+62$numberPhone", resendToken)
        }
    }

    private fun getCredential(otpCode: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otpCode)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    checkRegisteredUser(user)
                    Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, "Wrong OTP code", Toast.LENGTH_SHORT).show()
                        otpInputBinding.txtOtp.setText(" ")
                    }
                }
            }
    }

    private fun checkRegisteredUser(user: FirebaseUser?) {
        reference.child("Mahasiswa").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.child("+628123456787").exists()){
                if(snapshot.child(user?.phoneNumber!!.toString()).exists()){
                    val intent = Intent(context, DashboardActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }else{
                    val intent = Intent(context, RegisterActivity::class.java)
                    intent.putExtra(EXTRA_PHONE, user.phoneNumber!!.toString())
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Error", error.message)
            }
        })
    }

    private fun sendOtpCode(phone: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendOtpCode(phone: String, token: PhoneAuthProvider.ForceResendingToken){
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .setForceResendingToken(token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}