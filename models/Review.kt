package com.example.movieapplication.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// 영화 리뷰 정보를 저장하기 위한 테이블
@Entity(tableName = "reviewTable")
class Review(
    @ColumnInfo(name = "title") // 리뷰 제목
    val title: String,

    @ColumnInfo(name = "content") // 리뷰 내용
    val content: String,

    @ColumnInfo(name = "time") // 리뷰 작성/업데이트 시간
    val time: String,

    @ColumnInfo(name = "rating") // 영화 개인 평점
    val rating: Double
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0 // 리뷰 id
}