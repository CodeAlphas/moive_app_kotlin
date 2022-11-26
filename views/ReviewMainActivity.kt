package com.example.movieapplication.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapplication.R
import com.example.movieapplication.adapters.ReviewRecyclerViewAdapter
import com.example.movieapplication.database.DatabaseInstance
import com.example.movieapplication.models.Review
import com.example.movieapplication.repository.ReviewRepository
import com.example.movieapplication.utils.ReviewClickDeleteInterface
import com.example.movieapplication.utils.ReviewClickInterface
import com.example.movieapplication.viewmodels.ReviewViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ReviewMainActivity : AppCompatActivity(), ReviewClickInterface, ReviewClickDeleteInterface {
    private lateinit var reviewRecyclerView: RecyclerView
    private lateinit var addFloatingActionButton: FloatingActionButton
    private lateinit var reviewViewModel: ReviewViewModel

    private lateinit var reviewDB: DatabaseReference
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val userId: String by lazy { auth.currentUser?.uid.orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_main)

        supportActionBar!!.title = "영화 감상문"

        val type = intent.getStringExtra("type")
        initRecyclerView(type)

        addFloatingActionButton = findViewById(R.id.reviewAddFloatingButton)
        addFloatingActionButton.setOnClickListener {
            val intent = Intent(this@ReviewMainActivity, ReviewDetailActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    private fun initRecyclerView(type: String?) {
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView)
        reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        val reviewRecyclerViewAdapter =
            ReviewRecyclerViewAdapter(LayoutInflater.from(this), this, this, this)
        reviewRecyclerView.adapter = reviewRecyclerViewAdapter

        reviewViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(ReviewViewModel::class.java)

        reviewDB = Firebase.database.reference.child("users").child(userId).child("reviews")
        reviewDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!type.equals("Edit")) {
                    for (data in snapshot.children) {
                        val id = data.key.toString().toInt()
                        val title = data.child("title").value.toString()
                        val content = data.child("content").value.toString()
                        val time = data.child("time").value.toString()
                        val rating = data.child("rating").value.toString().toDouble()
                        val review = Review(title, content, time, rating)
                        review.id = id
                        reviewViewModel.insertReview(review)
                    }
                }
                reviewViewModel.allReview.observe(getContext()) { list ->
                    list?.let {
                        reviewRecyclerViewAdapter.updateReviewList(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }) // 서버에 저장된 감상문 정보 가져오기(Firebase Realtime Database)
    }

    private fun getContext(): LifecycleOwner {
        return this
    }

    override fun onDeleteIconClick(review: Review) {
        reviewViewModel.deleteReview(review)
        Toast.makeText(this, "감상문 ${review.title}가 삭제되었습니다.", Toast.LENGTH_LONG).show()

        reviewDB = Firebase.database.reference.child("users").child(userId).child("reviews")
            .child(review.id.toString())
        reviewDB.removeValue()
    } // 작성한 감상문 아이템에서 X 이미지를 누르면 발생하는 이벤트 처리를 위한 메소드

    override fun onIconClick(review: Review) {
        val intent = Intent(this@ReviewMainActivity, ReviewDetailActivity::class.java)
        intent.putExtra("reviewType", "Edit")
        intent.putExtra("reviewTitle", review.title)
        intent.putExtra("reviewContent", review.content)
        intent.putExtra("reviewId", review.id)
        intent.putExtra("rating", review.rating)
        startActivity(intent)
        this.finish()
    } // 작성한 감상문 아이템(X 이미지를 제외한 부분)을 누르면 발생하는 이벤트 처리를 위한 메소드

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.application_menu, menu)
        return true
    } // 앱바에 로그아웃 버튼 추가

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_action -> {
                auth.signOut() // 로그아웃(Firebase Authentication)

                val dao = DatabaseInstance.getInstance(application).reviewDao()
                val repository = ReviewRepository(dao)
                repository.deleteAll() // 로그아웃시 reviewTable의 데이터 삭제

                startActivity(Intent(applicationContext, LoginActivity::class.java))
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    } // 앱바의 로그아웃 버튼에 로그아웃 이벤트 추가
}