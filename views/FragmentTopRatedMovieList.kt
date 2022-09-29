package com.example.movieapplication.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapplication.R
import com.example.movieapplication.adapters.TopRatedMoviesRecyclerViewAdapter
import com.example.movieapplication.models.MoviesFromServer
import com.example.movieapplication.utils.ItemDecorator
import com.example.movieapplication.utils.Utils
import com.example.movieapplication.viewmodels.FragmentTopRatedMovieListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentTopRatedMovieList : Fragment() {

    lateinit var topRatedMovieRecyclerView: RecyclerView
    lateinit var topRatedMoviesRecyclerViewAdapter: TopRatedMoviesRecyclerViewAdapter
    lateinit var floatingActionButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.top_rated_movie_list_fragment, container, false)

        initRecyclerView(view)
        initViewModel()

        floatingActionButton = view.findViewById<FloatingActionButton>(R.id.floatingButton)
        floatingActionButton.setOnClickListener {
            startActivity(Intent(requireContext(), ReviewMainActivity::class.java))
        }

        topRatedMovieRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    floatingActionButton.show() // 리싸이클러뷰 스크롤이 정지되어 있으면 플로팅 액션 버튼을 보이게 한다
                } else {
                    floatingActionButton.hide() // 리싸이클러뷰 스크롤이 움직이면 플로팅 액션 버튼을 숨긴다
                }
            }
        })
        return view
    }

    private fun initRecyclerView(view: View) {
        topRatedMovieRecyclerView = view.findViewById(R.id.topRatedMovieRecyclerView)
        // 리싸이클러 뷰의 아이템들을 GridLayout 방식으로 배치
        topRatedMovieRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        // ItemDecorator 클래스를 이용하여 리싸이클러뷰 아이템들 사이의 간격 조정
        topRatedMovieRecyclerView.addItemDecoration(
            ItemDecorator((Utils.getScreenWidth(requireContext()) - 360) / 3, requireContext())
        )
        topRatedMoviesRecyclerViewAdapter = TopRatedMoviesRecyclerViewAdapter(requireContext())
        topRatedMovieRecyclerView.adapter = topRatedMoviesRecyclerViewAdapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this).get(FragmentTopRatedMovieListViewModel::class.java)
        viewModel.makeApiCall()
        viewModel.getTopRatedMovieListObserver()
            .observe(viewLifecycleOwner, Observer<MoviesFromServer> {
                if (it != null) {
                    topRatedMoviesRecyclerViewAdapter.setUpdatedData(it.results)
                    floatingActionButton.visibility = View.VISIBLE
                } else {
                    Log.d("alpha test", "에러 발생")
                }
            })
    }
}