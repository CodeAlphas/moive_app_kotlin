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
import com.example.movieapplication.adapters.TopRatedMoviesRecyclerViewAdapter
import com.example.movieapplication.databinding.TopRatedMovieListFragmentBinding
import com.example.movieapplication.models.MoviesFromServer
import com.example.movieapplication.utils.ItemDecorator
import com.example.movieapplication.utils.Utils
import com.example.movieapplication.utils.Utils.Companion.TAG
import com.example.movieapplication.viewmodels.MovieViewModel

class FragmentTopRatedMovieList : Fragment() {

    private var _binding: TopRatedMovieListFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var topRatedMoviesRecyclerViewAdapter: TopRatedMoviesRecyclerViewAdapter
    private lateinit var viewModel: MovieViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TopRatedMovieListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initViewModel()
        initFloatingActionButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        // 리싸이클러 뷰의 아이템들을 GridLayout 방식으로 배치
        binding.topRatedMovieRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        // ItemDecorator 클래스를 이용하여 리싸이클러뷰 아이템들 사이의 간격 조정
        binding.topRatedMovieRecyclerView.addItemDecoration(
            ItemDecorator((Utils.getScreenWidth(requireContext()) - 360) / 3, requireContext())
        )
        topRatedMoviesRecyclerViewAdapter = TopRatedMoviesRecyclerViewAdapter(requireContext())
        binding.topRatedMovieRecyclerView.adapter = topRatedMoviesRecyclerViewAdapter
        binding.topRatedMovieRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.floatingButton.show() // 리싸이클러뷰 스크롤이 정지되어 있으면 플로팅 액션 버튼을 보이게 한다
                } else {
                    binding.floatingButton.hide() // 리싸이클러뷰 스크롤이 움직이면 플로팅 액션 버튼을 숨긴다
                }
            }
        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
        viewModel.makeTopRatedMovieListApiCall()
        viewModel.allTopMovies
            .observe(viewLifecycleOwner, Observer<MoviesFromServer> {
                if (it != null) {
                    topRatedMoviesRecyclerViewAdapter.setUpdatedData(it.results)
                    binding.floatingButton.visibility = View.VISIBLE
                } else {
                    // Log.d(TAG, "에러 발생")
                }
            })
    }

    private fun initFloatingActionButton() {
        binding.floatingButton.setOnClickListener {
            startActivity(Intent(requireContext(), ReviewMainActivity::class.java))
        }
    }
}