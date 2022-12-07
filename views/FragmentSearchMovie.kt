package com.example.movieapplication.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapplication.adapters.SearchMoviesRecyclerViewAdapter
import com.example.movieapplication.databinding.SearchMovieFragmentBinding
import com.example.movieapplication.models.MoviesFromServer
import com.example.movieapplication.utils.ItemDecorator
import com.example.movieapplication.utils.Utils
import com.example.movieapplication.utils.Utils.Companion.TAG
import com.example.movieapplication.viewmodels.MovieViewModel

class FragmentSearchMovie : Fragment() {

    private var _binding: SearchMovieFragmentBinding? = null
    private val binding get() = _binding!!
    private var query: String = "" // editText 뷰를 통해 검색된 영화 이름
    private lateinit var searchMoviesRecyclerViewAdapter: SearchMoviesRecyclerViewAdapter
    private lateinit var viewModel: MovieViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SearchMovieFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initFloatingActionButton()
        initSearchViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView() {
        // 리싸이클러 뷰의 아이템들을 GridLayout 방식으로 배치
        binding.searchMovieRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        // ItemDecorator 클래스를 이용하여 리싸이클러뷰 아이템들 사이의 간격 조정
        binding.searchMovieRecyclerView.addItemDecoration(
            ItemDecorator((Utils.getScreenWidth(requireContext()) - 360) / 3, requireContext())
        )
        searchMoviesRecyclerViewAdapter = SearchMoviesRecyclerViewAdapter(requireContext())
        binding.searchMovieRecyclerView.adapter = searchMoviesRecyclerViewAdapter
        binding.searchMovieRecyclerView.addOnScrollListener(object :
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

    private fun initFloatingActionButton() {
        binding.floatingButton.setOnClickListener {
            startActivity(Intent(requireContext(), ReviewMainActivity::class.java))
        }
    }

    private fun initSearchViews() {
        binding.editTextSearch.setOnKeyListener { view, i, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                sendRequest()
                Utils.hideKeyboard(requireContext(), binding.editTextSearch)
                true
            } else {
                false
            }
        }
        binding.editTextSearch.setOnFocusChangeListener { view, b ->
            if (!b) {
                Utils.hideKeyboard(requireContext(), view)
            }
        } // 검색창이 아닌 화면의 다른 곳을 선택하면(검색창이 focus를 잃으면) 소프트 키보드를 화면에서 내림

        binding.buttonSearch.setOnClickListener {
            sendRequest()
            binding.editTextSearch.clearFocus()
            Utils.hideKeyboard(requireContext(), binding.buttonSearch)
        }
    }

    // 검색한 영화에 대한 정보를 TMDB 서버에 요청하고 해당정보를 리싸이클러뷰에 보여주는 메소드
    private fun sendRequest() {
        query =
            binding.editTextSearch.text.toString() // 소프트 키보드에서 enter키를 누르면 해당 정보를 읽어와 query에 저장

        if (query.isNotBlank()) {
            viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
            viewModel.makeSearchMovieListApiCall(query)
            viewModel.allSearchMovies
                .observe(viewLifecycleOwner, Observer<MoviesFromServer> {
                    if (it != null) {
                        searchMoviesRecyclerViewAdapter.setUpdatedData(it.results)
                    } else {
                        // Log.d(TAG, "에러 발생")
                    }
                })
        } // query가 화이트 스페이스로 이루어져 있지 않을 경우에만 TMDB 서버에 해당 문자열로 이루어진 영화 정보를 요청
    }
}

