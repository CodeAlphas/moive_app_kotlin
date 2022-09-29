package com.example.movieapplication.views

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.movieapplication.R
import com.example.movieapplication.models.Review
import com.example.movieapplication.utils.Utils
import com.example.movieapplication.viewmodels.ReviewViewModel

class ReviewDetailActivity : AppCompatActivity() {
    lateinit var reviewTitleEdit: EditText
    lateinit var reviewContentEdit: EditText
    lateinit var ratingBar: RatingBar
    lateinit var button: Button
    lateinit var viewModel: ReviewViewModel
    var reviewId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_detail)

        supportActionBar!!.title = "영화 리뷰 작성"

        reviewTitleEdit = findViewById(R.id.titleEditText)
        reviewContentEdit = findViewById(R.id.contentEditText)
        ratingBar = findViewById(R.id.reviewRatingBar)
        button = findViewById(R.id.button)

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(ReviewViewModel::class.java)

        val reviewType = intent.getStringExtra("reviewType")
        if (reviewType.equals("Edit")) {
            val reviewTitle = intent.getStringExtra("reviewTitle")
            val reviewContent = intent.getStringExtra("reviewContent")
            val reviewRating = intent.getDoubleExtra("rating", 0.0)
            reviewId = intent.getIntExtra("reviewId", -1)
            reviewTitleEdit.setText(reviewTitle)
            reviewContentEdit.setText(reviewContent)
            ratingBar.rating = reviewRating.toFloat() / 2
            button.text = "리뷰 수정" // 사용자가 리뷰 편집 모드로 진입할 시
        } else {
            button.text = "리뷰 등록" // 사용자가 리뷰 등록 모드로 진입할 시
        }

        reviewTitleEdit.setOnKeyListener { view, i, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                Utils.hideKeyboard(this, reviewTitleEdit)
                true
            } else {
                false
            }
        } // 사용자가 제목을 입력하고 enter키를 누르면 소프트 키보드를 화면에서 내림

        reviewTitleEdit.setOnFocusChangeListener { view, b ->
            if (!b) { Utils.hideKeyboard(this, view) }
        }

        reviewContentEdit.setOnFocusChangeListener { view, b ->
            if (!b) { Utils.hideKeyboard(this, view) }
        }

        button.setOnClickListener {
            val currentReviewTitle = reviewTitleEdit.text.toString()
            val currentReviewContent = reviewContentEdit.text.toString()
            val currentRating = ratingBar.rating.toDouble() * 2

            if (reviewType.equals("Edit")) {
                if (currentReviewTitle.isNotEmpty() && currentReviewContent.isNotEmpty()) {
                    val updateReview = Review(
                        currentReviewTitle,
                        currentReviewContent,
                        Utils.getCurrentDate(),
                        currentRating
                    )
                    updateReview.id = reviewId
                    viewModel.updateReview(updateReview)
                    Toast.makeText(this, "리뷰가 수정되었습니다.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(applicationContext, ReviewMainActivity::class.java))
                    this.finish()
                } else {
                    Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_LONG).show()
                }
            } else {
                if (currentReviewTitle.isNotEmpty() && currentReviewContent.isNotEmpty()) {
                    val updateReview = Review(
                        currentReviewTitle,
                        currentReviewContent,
                        Utils.getCurrentDate(),
                        currentRating
                    )
                    viewModel.insertReview(updateReview)
                    Toast.makeText(this, "리뷰가 등록되었습니다.", Toast.LENGTH_LONG).show()
                    startActivity(Intent(applicationContext, ReviewMainActivity::class.java))
                    this.finish()
                } else {
                    Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}