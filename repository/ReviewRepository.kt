package com.example.movieapplication.repository

import androidx.lifecycle.LiveData
import com.example.movieapplication.database.ReviewDao
import com.example.movieapplication.models.Review

class ReviewRepository(private val reviewDao: ReviewDao) {

    val allReview: LiveData<List<Review>> = reviewDao.getAll()

    fun insert(review: Review) {
        reviewDao.insert(review)
    }

    fun delete(review: Review) {
        reviewDao.delete(review)
    }

    fun update(review: Review) {
        reviewDao.update(review)
    }
}