package com.example.movieapplication.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapplication.R
import com.example.movieapplication.adapters.SearchMoviesRecyclerViewAdapter
import com.example.movieapplication.models.MoviesFromServer
import com.example.movieapplication.utils.ItemDecorator
import com.example.movieapplication.utils.Utils
import com.example.movieapplication.viewmodels.FragmentSearchMovieListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentSearchMovie : Fragment() {
    lateinit var searchMovierecyclerView: RecyclerView
    lateinit var searchMoviesRecyclerViewAdapter: SearchMoviesRecyclerViewAdapter
    lateinit var floatingActionButton: FloatingActionButton
    var query: String = "" // editText 뷰를 통해 검색된 영화 이름

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.search_movie_fragment, container, false)
        val searchTextBox = view.findViewById<EditText>(R.id.editTextSearch)
        val searchButton = view.findViewById<Button>(R.id.buttonSearch)

        initRecyclerView(view)

        floatingActionButton = view.findViewById<FloatingActionButton>(R.id.floatingButton)
        floatingActionButton.setOnClickListener {
            startActivity(Intent(requireContext(), ReviewMainActivity::class.java))
        }

        searchMovierecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    floatingActionButton.show() // 리싸이클러뷰 스크롤이 정지되어 있으면 플로팅 액션 버튼을 보이게 한다
                } else {
                    floatingActionButton.hide() // 리싸이클러뷰 스크롤이 움직이면 플로팅 액션 버튼을 숨긴다
                }
            }
        })

        searchTextBox.setOnKeyListener { view, i, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                query = searchTextBox.text.toString() // 소프트 키보드에서 enter키를 누르면 해당 정보를 읽어와 query에 저장
                if (query.isNotEmpty() && query.isNotBlank()) {
                    sendRequest(searchTextBox.text.toString())
                } // query가 비어있지 않거나 화이트 스페이스로 이루어져 있지 않을 경우에만 TMDB 서버에 해당 문자열로 이루어진 영화 정보를 요청
                Utils.hideKeyboard(requireContext(), searchTextBox)
                true
            } else {
                false
            }
        }

        searchButton.setOnClickListener {
            query = searchTextBox.text.toString()
            if (query.isNotEmpty() && query.isNotBlank()) {
                sendRequest(searchTextBox.text.toString())
            } // query가 비어있지 않거나 화이트 스페이스로 이루어져 있지 않을 경우에만 TMDB 서버에 해당 문자열로 이루어진 영화 정보를 요청
            searchTextBox.clearFocus()
            Utils.hideKeyboard(requireContext(), searchTextBox)
        }

        searchTextBox.setOnFocusChangeListener { view, b ->
            if (!b) {
                Utils.hideKeyboard(requireContext(), view)
            }
        } // 검색창이 아닌 화면의 다른 곳을 선택하면(검색창이 focus를 잃으면) 소프트 키보드를 화면에서 내림
        return view
    }

    private fun initRecyclerView(view: View) {
        searchMovierecyclerView = view.findViewById(R.id.searchMovieRecyclerView)
        // 리싸이클러 뷰의 아이템들을 GridLayout 방식으로 배치
        searchMovierecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        // ItemDecorator 클래스를 이용하여 리싸이클러뷰 아이템들 사이의 간격 조정
        searchMovierecyclerView.addItemDecoration(
            ItemDecorator((Utils.getScreenWidth(requireContext()) - 360) / 3, requireContext())
        )
        searchMoviesRecyclerViewAdapter = SearchMoviesRecyclerViewAdapter(requireContext())
        searchMovierecyclerView.adapter = searchMoviesRecyclerViewAdapter
    }

    // 검색한 영화에 대한 정보를 TMDB 서버에 요청하고 해당정보를 리싸이클러뷰에 보여주는 메소드
    private fun sendRequest(query: String) {
        val viewModel = ViewModelProvider(this).get(FragmentSearchMovieListViewModel::class.java)
        viewModel.makeApiCall(query)
        viewModel.getSearchMovieListObserver()
            .observe(viewLifecycleOwner, Observer<MoviesFromServer> {
                if (it != null) {
                    searchMoviesRecyclerViewAdapter.setUpdatedData(it.results)

                } else {
                    Log.d("alpha test", "에러 발생")
                }
            })
    }
}

