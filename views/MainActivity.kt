package com.example.movieapplication.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import androidx.viewpager2.widget.ViewPager2
import com.example.movieapplication.R
import com.example.movieapplication.adapters.FragmentViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
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
}