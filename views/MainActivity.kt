package com.example.movieapplication.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

import androidx.viewpager2.widget.ViewPager2
import com.example.movieapplication.R
import com.example.movieapplication.adapters.FragmentViewPagerAdapter
import com.example.movieapplication.database.DatabaseInstance
import com.example.movieapplication.repository.ReviewRepository
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth =
        FirebaseAuth.getInstance() // private val auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        supportActionBar!!.title = "오늘의 인기 영화" // 앱바(액션바)의 기본 텍스트
        viewPager.adapter = FragmentViewPagerAdapter(this, 3) // 뷰페이저에 Adapter 장착

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab!!.position
                viewPager.currentItem = position

                when (position) {
                    0 -> supportActionBar!!.title = "오늘의 인기 영화"
                    1 -> supportActionBar!!.title = "높은 평점 영화"
                    else -> supportActionBar!!.title = "영화 검색"
                } // 탭 클릭시 해당 탭에 맞게 앱바(액션바)의 텍스트 변경
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        }) // 탭 레이아웃 설정

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "인기"
                1 -> tab.text = "높은 평점"
                2 -> tab.text = "검색"
            }
        }.attach() // 탭 레이아웃과 뷰페이저 연결
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } // 로그인되어 있지 않으면 로그인 화면으로 이동
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.application_menu, menu)
        return true
    } // 앱바에 로그아웃 버튼 추가

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_action -> {
                auth.signOut() // 로그아웃(Firebase Authentication)

                val dao = DatabaseInstance.getInstance(application).reviewDao()
                val repository = ReviewRepository(dao)
                repository.deleteAll() // 로그아웃시 reviewTable의 데이터 삭제

                startActivity(Intent(applicationContext, LoginActivity::class.java))
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    } // 앱바의 로그아웃 버튼에 로그아웃 이벤트 추가
}