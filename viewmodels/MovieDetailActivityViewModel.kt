package com.example.movieapplication.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapplication.models.CreditsFromServer
import com.example.movieapplication.models.VideosFromServer
import com.example.movieapplication.networks.RetroInstance
import com.example.movieapplication.networks.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieDetailActivityViewModel : ViewModel() {

    private var allVideos: MutableLiveData<VideosFromServer> = MutableLiveData()
    private var allCredits: MutableLiveData<CreditsFromServer> = MutableLiveData()

    fun getVideoListObserver(): MutableLiveData<VideosFromServer> {
        return allVideos
    }

    fun getCreditListObserver(): MutableLiveData<CreditsFromServer> {
        return allCredits
    }

    fun getVideoApiCall(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val retroInstance =
                RetroInstance.getRetrofitInstance().create(RetrofitService::class.java)
            val response = retroInstance.getVideosList(movieId = movieId)
            allVideos.postValue(response)
        }
    }  // TMDB 서버로 영화의 동영상 정보를 요청하고 해당 정보를 받아오는 메소드

    fun getCreditApiCall(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val retroInstance =
                RetroInstance.getRetrofitInstance().create(RetrofitService::class.java)
            val response = retroInstance.getCreditsList(movieId = movieId)
            allCredits.postValue(response)
        }
    }  // TMDB 서버로 영화 관계자들의 정보를 요청하고 해당 정보를 받아오는 메소드
}