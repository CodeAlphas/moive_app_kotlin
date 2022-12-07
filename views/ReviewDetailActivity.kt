package com.example.movieapplication.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.movieapplication.databinding.ActivityReviewDetailBinding
import com.example.movieapplication.models.Review
import com.example.movieapplication.utils.Utils
import com.example.movieapplication.viewmodels.ReviewViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ReviewDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReviewDetailBinding
    private lateinit var viewModel: ReviewViewModel
    private lateinit var reviewDB: DatabaseReference
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val userId: String by lazy { auth.currentUser?.uid.orEmpty() }
    private val storage: FirebaseStorage by lazy { Firebase.storage }
    private val reviewType: String? by lazy { intent.getStringExtra("reviewType") }
    private val readPermission: String by lazy { Manifest.permission.READ_EXTERNAL_STORAGE }
    private var reviewId: Int = -1
    private var selectedImageUri: Uri? = null
    private var imageUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.title = "영화 감상문 작성"

        initViewContent()
        initTitleEditText()
        initImageView()
        initContentEditText()
        initSaveButton()
    }

    private fun initViewContent() {
        if (reviewType.equals("Edit")) {
            val reviewTitle = intent.getStringExtra("reviewTitle")
            val reviewImage = intent.getStringExtra("reviewImage")
            val reviewContent = intent.getStringExtra("reviewContent")
            val reviewRating = intent.getDoubleExtra("rating", 0.0)
            imageUri = reviewImage!!
            reviewId = intent.getIntExtra("reviewId", -1)

            binding.titleEditText.setText(reviewTitle)
            if (reviewImage != "") {
                Glide
                    .with(this)
                    .load(reviewImage)
                    .centerCrop()
                    .into(binding.imageView)
            } // storage에 이미지를 저장한 경우
            binding.contentEditText.setText(reviewContent)
            binding.reviewRatingBar.rating = reviewRating.toFloat() / 2
            binding.button.text = "감상문 수정" // 사용자가 감상문 편집 모드로 진입할 시
        } else {
            binding.button.text = "감상문 등록" // 사용자가 감상문 등록 모드로 진입할 시
        }
    }

    private fun initTitleEditText() {
        binding.titleEditText.setOnKeyListener { view, i, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                Utils.hideKeyboard(this, binding.titleEditText)
                true
            } else {
                false
            }
        } // 사용자가 제목을 입력하고 enter키를 누르면 소프트 키보드를 화면에서 내림

        binding.titleEditText.setOnFocusChangeListener { view, b ->
            if (!b) {
                Utils.hideKeyboard(this, view)
            }
        }
    }

    private fun initImageView() {
        binding.imageView.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    readPermission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    getPhoto()
                } // 외부저장소 접근 권한이 잘 부여되어있을 때, 갤러리에서 사진을 선택
                shouldShowRequestPermissionRationale(readPermission) -> {
                    showPermissionPopup()
                } // 이전에 앱이 권한을 요청하고 사용자가 요청을 거부한 경우 교육용 팝업을 띄움
                else -> {
                    requestPermissions(arrayOf(readPermission), 1000)
                } // 권한 요청을 위한 팝업을 띄움
            }
        }
    }

    private fun showPermissionPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("더 무비앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("허용") { _, _ ->
                requestPermissions(arrayOf(readPermission), 1000)
            }
            .setNegativeButton("차단") { _, _ -> }
            .create()
            .show()
    }

    private fun initContentEditText() {
        binding.contentEditText.setOnFocusChangeListener { view, b ->
            if (!b) {
                Utils.hideKeyboard(this, view)
            }
        }
    }

    private fun initSaveButton() {
        binding.button.setOnClickListener {
            dataChanged()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPhoto() // 권한이 부여됨
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    } // requestPermissions의 처리 결과를 반환해주는 메소드

    private fun getPhoto() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activityResult.launch(intent)
    } // Storage Access Framework(SAF)의 기능을 이용해서 콘텐츠를 가져올 수 있는 안드로이드의 내장 엑티비티를 실행해주는 메소드

    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            showProgress()
            when (result.resultCode) {
                RESULT_OK -> {
                    // Toast.makeText(this, "사진을 가져왔습니다.", Toast.LENGTH_SHORT).show()
                    selectedImageUri = result?.data?.data
                    if (selectedImageUri != null) {
                        binding.imageView.setImageURI(selectedImageUri)

                        uploadPhotoToStorage(
                            successHandler = { uri ->
                                imageUri = uri
                                hideProgress()
                                Toast.makeText(this, "사진 업로드에 성공했습니다.", Toast.LENGTH_SHORT).show()
                            },
                            errorHandler = {
                                hideProgress()
                                Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        ) // 이미지를 Firebase Storage에 업로드
                    } else {
                        hideProgress()
                        Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {
                    hideProgress()
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun uploadPhotoToStorage(successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        val fileName = userId.substring(
            0,
            10
        ) + "${System.currentTimeMillis()}.png" // Storage에 저장될 File의 이름을 지정
        storage.reference.child("review/photo").child(fileName)
            .putFile(selectedImageUri!!)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storage.reference.child("review/photo").child(fileName).downloadUrl
                        .addOnSuccessListener { uri ->
                            successHandler(uri.toString())
                        }.addOnFailureListener {
                            errorHandler()
                        }
                } else {
                    errorHandler()
                }
            }
    } // 이미지를 Firebase Storage의 지정된 경로에 업로드해주는 메소드

    private fun dataChanged() {
        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(ReviewViewModel::class.java)

        val currentReviewTitle = binding.titleEditText.text.toString()
        val currentReviewContent = binding.contentEditText.text.toString()
        val currentRating = binding.reviewRatingBar.rating.toDouble() * 2

        if (reviewType.equals("Edit")) {
            if (currentReviewTitle.isNotBlank() && currentReviewContent.isNotBlank()) {
                val updateReview = Review(
                    currentReviewTitle,
                    imageUri,
                    currentReviewContent,
                    Utils.getCurrentDate(),
                    currentRating
                )
                updateReview.id = reviewId

                viewModel.updateReview(updateReview)

                updateFirebaseRealtimeDB(currentReviewTitle, currentReviewContent, currentRating)
                Toast.makeText(this, "감상문이 수정되었습니다.", Toast.LENGTH_LONG).show()
                returnToReviewMain()
            } else {
                Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_LONG).show()
            }
        } else {
            if (currentReviewTitle.isNotBlank() && currentReviewContent.isNotBlank()) {
                val updateReview = Review(
                    currentReviewTitle,
                    imageUri,
                    currentReviewContent,
                    Utils.getCurrentDate(),
                    currentRating
                )

                viewModel.insertTransaction(updateReview)
                viewModel.maxId
                    .observe(this, Observer { maxId ->
                        maxId?.let {
                            reviewId = it
                            updateFirebaseRealtimeDB(
                                currentReviewTitle,
                                currentReviewContent,
                                currentRating
                            )
                            Toast.makeText(this, "감상문이 등록되었습니다.", Toast.LENGTH_LONG).show()
                            returnToReviewMain()
                        }
                    })
            } else {
                Toast.makeText(this, "제목과 내용을 모두 입력해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateFirebaseRealtimeDB(
        currentReviewTitle: String,
        currentReviewContent: String,
        currentRating: Double
    ) {
        reviewDB =
            Firebase.database.reference.child("users").child(userId).child("reviews")
                .child(reviewId.toString())
        val review = mutableMapOf<String, Any>()
        review["id"] = reviewId
        review["image"] = imageUri
        review["title"] = currentReviewTitle
        review["content"] = currentReviewContent
        review["time"] = Utils.getCurrentDate()
        review["rating"] = currentRating
        reviewDB.updateChildren(review)
    } // 서버에 감상문 정보를 저장(Firebase Realtime Database)해주는 메소드

    private fun returnToReviewMain() {
        val intent = Intent(applicationContext, ReviewMainActivity::class.java)
        intent.putExtra("type", "Edit")
        startActivity(intent)
        this.finish()
    }

    private fun showProgress() {
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) // 화면 터치 막기
        binding.progressBar.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBar.isVisible = false
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) // 화면 터치 풀기
    }
}