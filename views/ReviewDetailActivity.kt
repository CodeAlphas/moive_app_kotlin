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
import com.example.movieapplication.database.DatabaseInstance
import com.example.movieapplication.models.Review
import com.example.movieapplication.repository.ReviewRepository
import com.example.movieapplication.utils.Utils
import com.example.movieapplication.viewmodels.ReviewViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ReviewDetailActivity : AppCompatActivity() {
    private lateinit var reviewTitleEdit: EditText
    private lateinit var reviewContentEdit: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var button: Button
    private lateinit var viewModel: ReviewViewModel
    private var reviewId = -1

    private lateinit var reviewDB: DatabaseReference
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_detail)

        supportActionBar!!.title = "영화 감상문 작성"

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
            button.text = "감상문 수정" // 사용자가 감상문 편집 모드로 진입할 시
        } else {
            button.text = "감상문 등록" // 사용자가 감상문 등록 모드로 진입할 시
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

        val userId = auth.currentUser?.uid.orEmpty()
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

                    reviewDB =
                        Firebase.database.reference.child("users").child(userId).child("reviews")
                            .child(reviewId.toString())
                    val review = mutableMapOf<String, Any>()
                    review["id"] = reviewId
                    review["title"] = currentReviewTitle
                    review["content"] = currentReviewContent
                    review["time"] = Utils.getCurrentDate()
                    review["rating"] = currentRating
                    reviewDB.updateChildren(review)
                    // 서버에 수정된 감상문 정보 저장(Firebase Realtime Database)

                    Toast.makeText(this, "감상문이 수정되었습니다.", Toast.LENGTH_LONG).show()
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

                    val dao = DatabaseInstance.getInstance(application).reviewDao()
                    val repository = ReviewRepository(dao)
                    val id = repository.insertTransaction(updateReview)

                    reviewDB =
                        Firebase.database.reference.child("users").child(userId).child("reviews")
                            .child(id.toString())
                    val review = mutableMapOf<String, Any>()
                    review["id"] = id
                    review["title"] = currentReviewTitle
                    review["content"] = currentReviewContent
                    review["time"] = Utils.getCurrentDate()
                    review["rating"] = currentRating
                    reviewDB.updateChildren(review)
                    // 서버에 새로운 감상문 정보 저장(Firebase Realtime Database)

                    Toast.makeText(this, "감상문이 등록되었습니다.", Toast.LENGTH_LONG).show()
                    val intent = Intent(applicationContext, ReviewMainActivity::class.java)
                    intent.putExtra("type", "Edit")
                    startActivity(intent)
                    this.finish()
                } else {
                    Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}