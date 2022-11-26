package com.example.movieapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.movieapplication.database.DatabaseInstance
import com.example.movieapplication.models.Review
import com.example.movieapplication.repository.ReviewRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReviewViewModel(application: Application) : AndroidViewModel(application) {

    val allReview: LiveData<List<Review>>
    val repository: ReviewRepository

    init {
        val dao = DatabaseInstance.getInstance(application).reviewDao()
        repository = ReviewRepository(dao)
        allReview = repository.allReview
    }

    fun deleteReview(review: Review) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(review)
    } // 기존 사용자 영화 리뷰를 삭제하는 메소드

    fun updateReview(review: Review) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(review)
    } // 기존 사용자 영화 리뷰를 갱신하는 메소드

    fun insertReview(review: Review) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(review)
    } // 새로운 사용자 영화 리뷰를 추가하는 메소드

}