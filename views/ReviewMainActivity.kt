package com.example.movieapplication.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapplication.R
import com.example.movieapplication.adapters.ReviewRecyclerViewAdapter
import com.example.movieapplication.models.Review
import com.example.movieapplication.utils.ReviewClickDeleteInterface
import com.example.movieapplication.utils.ReviewClickInterface
import com.example.movieapplication.viewmodels.ReviewViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReviewMainActivity : AppCompatActivity(), ReviewClickInterface, ReviewClickDeleteInterface {
    lateinit var reviewRecyclerView: RecyclerView
    lateinit var addFloatingActionButton: FloatingActionButton
    lateinit var reviewViewModel: ReviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_main)

        supportActionBar!!.title = "영화 리뷰 작성"

        initRecyclerView()

        addFloatingActionButton = findViewById(R.id.reviewAddFloatingButton)
        addFloatingActionButton.setOnClickListener {
            val intent = Intent(this@ReviewMainActivity, ReviewDetailActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    private fun initRecyclerView() {
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView)
        reviewRecyclerView.layoutManager = LinearLayoutManager(this)
        val reviewRecyclerViewAdapter =
            ReviewRecyclerViewAdapter(LayoutInflater.from(this), this, this, this)
        reviewRecyclerView.adapter = reviewRecyclerViewAdapter

        reviewViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(ReviewViewModel::class.java)
        reviewViewModel.allReview.observe(this) { list ->
            list?.let {
                reviewRecyclerViewAdapter.updateReviewList(it)
            }
        }
    }

    override fun onDeleteIconClick(review: Review) {
        reviewViewModel.deleteReview(review)
        Toast.makeText(this, "${review.title}가 삭제되었습니다.", Toast.LENGTH_LONG).show()
    } // 작성한 리뷰 아이템에서 X 이미지를 누르면 발생하는 이벤트 처리를 위한 메소드

    override fun onIconClick(review: Review) {
        val intent = Intent(this@ReviewMainActivity, ReviewDetailActivity::class.java)
        intent.putExtra("reviewType", "Edit")
        intent.putExtra("reviewTitle", review.title)
        intent.putExtra("reviewContent", review.content)
        intent.putExtra("reviewId", review.id)
        intent.putExtra("rating", review.rating)
        startActivity(intent)
        this.finish()
    } // 작성한 리뷰 아이템(X 이미지를 제외한 부분)을 누르면 발생하는 이벤트 처리를 위한 메소드
}