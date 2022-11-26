package com.example.movieapplication.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.example.movieapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {

    private var id: String = ""
    private var pw1: String = ""
    private var pw2: String = ""
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val idEditText = findViewById<EditText>(R.id.id_input)
        val pwEditText1 = findViewById<EditText>(R.id.pw_input1)
        val pwEditText2 = findViewById<EditText>(R.id.pw_input2)
        val joinButton = findViewById<TextView>(R.id.join_btn)
        val loginButton = findViewById<TextView>(R.id.login_btn)

        idEditText.doAfterTextChanged { id = it.toString() }
        pwEditText1.doAfterTextChanged { pw1 = it.toString() }
        pwEditText2.doAfterTextChanged { pw2 = it.toString() }

        auth = Firebase.auth
        joinButton.setOnClickListener {
            if (pw1 == pw2) {
                if (id == "" || pw1 == "") {
                    Toast.makeText(this, "회원가입에 실패했습니다. 아이디 또는 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    auth.createUserWithEmailAndPassword(id, pw1)
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "회원가입에 성공했습니다. 로그인하기 버튼을 눌러 로그인해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    "이미 가입한 이메일이거나 회원가입에 실패했습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } // 이메일 주소와 비밀번호로 회원가입(Firebase Authentication)
                }
            } else {
                Toast.makeText(this, "회원가입에 실패했습니다. 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        loginButton.setOnClickListener { finish() }
    }
}