package com.example.movieapplication.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapplication.models.MoviesFromServer
import com.example.movieapplication.networks.RetroInstance
import com.example.movieapplication.networks.RetrofitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentSearchMovieListViewModel : ViewModel() {

    private var allMovies: MutableLiveData<MoviesFromServer> = MutableLiveData()

    fun getSearchMovieListObserver(): MutableLiveData<MoviesFromServer> {
        return allMovies
    }

    fun makeApiCall(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val retroInstance =
                RetroInstance.getRetrofitInstance().create(RetrofitService::class.java)
            val response = retroInstance.getSearchedMovieList(query = query)
            allMovies.postValue(response)
        }
    } // TMDB 서버로 검색한 영화의 정보를 요청하고 해당 정보를 받아오는 메소드
}