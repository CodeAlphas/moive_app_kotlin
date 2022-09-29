package com.example.movieapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapplication.R
import com.example.movieapplication.models.Review
import com.example.movieapplication.utils.ReviewClickDeleteInterface
import com.example.movieapplication.utils.ReviewClickInterface

// 영화 리뷰 표시화면에서 자신이 작성한 리뷰를 보여주는 리싸이클러뷰를 위한 어댑터
class ReviewRecyclerViewAdapter(
    var inflater: LayoutInflater,
    var context: Context,
    val reviewClickInterface: ReviewClickInterface,
    val reviewClickDeleteInterface: ReviewClickDeleteInterface
) : RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder>() {

    private val items = ArrayList<Review>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleTextView: TextView = itemView.findViewById<TextView>(R.id.reviewTitle1)
        val timeTextView: TextView = itemView.findViewById<TextView>(R.id.reviewedTime1)
        val ratingBar: RatingBar = itemView.findViewById<RatingBar>(R.id.reviewRatingBar)
        val deleteImageView: ImageView = itemView.findViewById<ImageView>(R.id.deleteImage1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.review_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleTextView.text = items.get(position).title
        holder.timeTextView.text = items.get(position).time
        holder.ratingBar.rating = (items.get(position).rating / 2).toFloat()

        holder.deleteImageView.setOnClickListener {
            reviewClickDeleteInterface.onDeleteIconClick(items.get(position))
        }

        holder.itemView.setOnClickListener {
            reviewClickInterface.onIconClick(items.get(position))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // 리싸이클러뷰를 갱신해주는 메소드
    fun updateReviewList(newReview: List<Review>) {
        items.clear()
        items.addAll(newReview)
        notifyDataSetChanged()
    }
}
