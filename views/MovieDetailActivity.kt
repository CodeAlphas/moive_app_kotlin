package com.example.movieapplication.views

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapplication.R
import com.example.movieapplication.adapters.CreditsRecyclerViewAdapter
import com.example.movieapplication.models.CreditsFromServer
import com.example.movieapplication.models.VideosFromServer
import com.example.movieapplication.utils.YOUTUBE_API_KEY
import com.example.movieapplication.viewmodels.MovieDetailActivityViewModel
import com.google.android.youtube.player.*

class MovieDetailActivity : AppCompatActivity(), YouTubePlayer.OnInitializedListener {
    lateinit var ratingBar: RatingBar
    lateinit var imageView: ImageView
    lateinit var textTitle: TextView
    lateinit var textRelease: TextView
    lateinit var textGrade: TextView
    lateinit var textOverView: TextView
    lateinit var textViewVideo: TextView
    lateinit var youtubePlayerFragment: YouTubePlayerFragment
    lateinit var creditsRecyclerView: RecyclerView
    lateinit var creditsRecyclerViewAdapter: CreditsRecyclerViewAdapter
    var youtubeVideoId: ArrayList<String> = ArrayList() // TMDB 서버로부터 받은 영화관련 동영상의 ID 정보

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        supportActionBar!!.title = "더무비"

        val poster = "https://image.tmdb.org/t/p/w500" + intent.getStringExtra("poster") // 영화 포스터 저장 경로
        val title = intent.getStringExtra("title") // 영화 제목
        val releaseDate = intent.getStringExtra("releaseDate") // 영화 개봉일자
        val overview = intent.getStringExtra("overview") // 영화 개요
        val voteAverage = intent.getDoubleExtra("voteAverage", 0.0) // 영화 평점
        val movieId = intent.getIntExtra("movieId", 0) // 영화 id

        ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        ratingBar.rating = voteAverage.toFloat() / 2
        imageView = findViewById<ImageView>(R.id.imagePoster)
        Glide.with(this)
            .load(poster)
            .into(imageView)
        textTitle = findViewById<TextView>(R.id.textTitle)
        textTitle.text = title
        textRelease = findViewById<TextView>(R.id.textRelease)
        textRelease.text = "$releaseDate 개봉"
        textGrade = findViewById<TextView>(R.id.textGrade)
        textGrade.text = "평점 : $voteAverage"
        textOverView = findViewById<TextView>(R.id.textOverview)
        textOverView.text = overview
        textViewVideo = findViewById<TextView>(R.id.textViewVideo)

        getCredits(movieId)
        checkVideo(movieId)
    }

    // 유튜브 플레이어 뷰에 동영상을 로드해주는 메소드
    private fun loadVideo() {
        youtubePlayerFragment =
            fragmentManager.findFragmentById(R.id.youtubePlayerViewFragment) as YouTubePlayerFragment
        youtubePlayerFragment.initialize(YOUTUBE_API_KEY, this)
    }

    override fun onInitializationSuccess(
        p0: YouTubePlayer.Provider?,
        p1: YouTubePlayer?,
        p2: Boolean
    ) {
        if (!p2) { p1?.cueVideos(youtubeVideoId) }
    }

    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        p1: YouTubeInitializationResult?
    ) {
        Log.d("alpha test", "에러 발생")
    }

    // 영화 관계자 정보를 TMDB 서버로부터 가져와 리싸이클러뷰에 넣어주는 메소드
    private fun getCredits(movieId: Int) {
        creditsRecyclerView = findViewById(R.id.creditsRecyclerView)
        creditsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) // 리싸이클러 뷰의 아이템들을 가로 방향으로 배열
        creditsRecyclerViewAdapter = CreditsRecyclerViewAdapter(this)
        creditsRecyclerView.adapter = creditsRecyclerViewAdapter

        val viewModel = ViewModelProvider(this).get(MovieDetailActivityViewModel::class.java)
        viewModel.getCreditApiCall(movieId)
        viewModel.getCreditListObserver()
            .observe(this, Observer<CreditsFromServer> {
                if (it != null) {
                    creditsRecyclerViewAdapter.setUpdatedData(it.cast)
                } else {
                    Log.d("alpha test", "에러 발생")
                }
            })
    }

    // 영화와 관련된 동영상 정보가 있는지 TMDB 서버에 확인하고 있으면 해당 정보로 유튜브 플레이어를 로드하는 메소드
    private fun checkVideo(movieId: Int) {

        val viewModel = ViewModelProvider(this).get(MovieDetailActivityViewModel::class.java)
        viewModel.getVideoApiCall(movieId)
        viewModel.getVideoListObserver()
            .observe(this, Observer<VideosFromServer> {
                if (it != null) {
                    val videoList = it.results
                    if (videoList.size > 0) {
                        videoList.forEach {
                            youtubeVideoId.add(it.key)
                        }
                        loadVideo()
                    }
                } else {
                    Log.d("alpha test", "에러 발생")
                }
            })
    }
}