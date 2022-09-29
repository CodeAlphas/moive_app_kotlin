package com.example.movieapplication.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.movieapplication.models.Review

// 영화 리뷰정보 테이블에 접근할 수 있는 메소드를 선언해 놓은 인터페이스
@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(review: Review)

    @Delete
    fun delete(review: Review)

    @Update
    fun update(review: Review)

    @Query("SELECT * FROM reviewTable ORDER BY id DESC")
    fun getAll(): LiveData<List<Review>>
}
