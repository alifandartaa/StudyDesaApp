package com.example.studydesaapp.ui.ui.loginregister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.studydesaapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var databaseReference: DatabaseReference? = null
    var database: FirebaseDatabase? = null
    private lateinit var fullname: String
    private lateinit var faculty: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(activityRegisterBinding.root)

        supportActionBar?.title = "Pendaftaran Akun"
        val phoneNumber = intent.getStringExtra("EXTRA_PHONE")
        fullname = activityRegisterBinding.txtFullName.text.toString()
        faculty = activityRegisterBinding.txtFaculty.text.toString()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("Mahasiswa")

        createUserData(activityRegisterBinding, phoneNumber)
    }

    private fun createUserData(
        activityRegisterBinding: ActivityRegisterBinding,
        phoneNumber: String?
    ) {
        activityRegisterBinding.btnSubmitRegister.setOnClickListener {
            val currentUser = auth.currentUser
            val currentUserDb = databaseReference?.child(phoneNumber!!)
            currentUserDb!!.child("name")
                .setValue(activityRegisterBinding.txtFullName.text.toString())
            currentUserDb.child("faculty")
                .setValue(activityRegisterBinding.txtFaculty.text.toString())
            currentUserDb.child("phone").setValue(phoneNumber)
            Toast.makeText(this@RegisterActivity, "Registration Success", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}