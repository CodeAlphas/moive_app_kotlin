package com.example.movieapplication.viewmodels

import androidx.lifecycle.*
import com.example.movieapplication.models.MoviesFromServer
import com.example.movieapplication.networks.RetroInstance
import com.example.movieapplication.networks.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentPopularMovieListViewModel : ViewModel() {

    private var allMovies: MutableLiveData<MoviesFromServer> = MutableLiveData()

    fun getPopularMovieListObserver(): MutableLiveData<MoviesFromServer> {
        return allMovies
    }

    fun makeApiCall() {
        viewModelScope.launch(Dispatchers.IO) {
            val retroInstance =
                RetroInstance.getRetrofitInstance().create(RetrofitService::class.java)
            val response = retroInstance.getPopularMovieList()
            allMovies.postValue(response)
        }
    } // TMDB 서버로 인기 영화 정보를 요청하고 해당 정보를 받아오는 메소드
}