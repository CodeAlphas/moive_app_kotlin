package com.example.movieapplication.networks

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroInstance {

    companion object {
        private const val BaseURL: String = "https://api.themoviedb.org/3/"

        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build() // TMDB 서버와의 통신을 위한 Retrofit 객체
        }
    }
} // 싱글톤 디자인 패턴