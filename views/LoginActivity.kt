package com.example.movieapplication.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.movieapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var id: String
    private lateinit var pw: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val idEditText = findViewById<EditText>(R.id.id_input)
        val pwEditText = findViewById<EditText>(R.id.pw_input)
        val loginButton = findViewById<TextView>(R.id.login_btn)
        val joinButton = findViewById<TextView>(R.id.join_btn)

        auth = Firebase.auth
        loginButton.setOnClickListener {
            id = idEditText.text.toString()
            pw = pwEditText.text.toString()

            if (id == "" || pw == "") {
                Toast.makeText(this, "로그인에 실패했습니다. 아이디 또는 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                auth.signInWithEmailAndPassword(id, pw)
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))

                            val userId = auth.currentUser?.uid.orEmpty()
                            val currentUserDB =
                                Firebase.database.reference.child("Users").child(userId)
                            val user = mutableMapOf<String, Any>()
                            user["userId"] = userId
                            currentUserDB.updateChildren(user)

                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "로그인에 실패했습니다. 아이디 또는 비밀번호를 다시 확인해주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } // 이메일 주소와 비밀번호로 로그인(Firebase Authentication)
        }

        joinButton.setOnClickListener {
            startActivity(Intent(this, JoinActivity::class.java))
        }
    }
}