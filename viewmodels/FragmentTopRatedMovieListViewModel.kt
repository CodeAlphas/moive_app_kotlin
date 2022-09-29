package com.example.movieapplication.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapplication.models.MoviesFromServer
import com.example.movieapplication.networks.RetroInstance
import com.example.movieapplication.networks.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentTopRatedMovieListViewModel : ViewModel() {

    private var allMovies: MutableLiveData<MoviesFromServer> = MutableLiveData()

    fun getTopRatedMovieListObserver(): MutableLiveData<MoviesFromServer> {
        return allMovies
    }

    fun makeApiCall() {
        viewModelScope.launch(Dispatchers.IO) {
            val retroInstance =
                RetroInstance.getRetrofitInstance().create(RetrofitService::class.java)
            val response = retroInstance.getTopRatedMovieList()
            allMovies.postValue(response)
        }
    } // TMDB 서버로 높은 평점의 영화 정보를 요청하고 해당 정보를 받아오는 메소드
}