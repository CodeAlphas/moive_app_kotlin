package com.example.movieapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapplication.R
import com.example.movieapplication.models.CreditItem

// 영화 상세화면에서 등장인물 정보를 보여주는 리싸이클러뷰를 위한 어댑터
class CreditsRecyclerViewAdapter(
    val context: Context
) : RecyclerView.Adapter<CreditsRecyclerViewAdapter.ViewHolder>() {

    private var items = ArrayList<CreditItem>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val actorProfile: ImageView = itemView.findViewById(R.id.actorImageView)
        val actorCharacterName: TextView = itemView.findViewById(R.id.actorCharacterNameTextView)
        val actorRealName: TextView = itemView.findViewById(R.id.actorRealNameTextView)

        // 뷰와 데이터를 연결해주는 메소드
        fun bind(data: CreditItem) {
            val imageUrl = "https://image.tmdb.org/t/p/w500" + data.profile_path

            // 뷰에 Glide 라이브러리를 이용하여 이미지 로드
            Glide
                .with(context)
                .load(imageUrl)
                .centerCrop()
                .into(actorProfile)
            actorCharacterName.text = data.character
            actorRealName.text = data.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.actor_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items.get(position))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    // 리싸이클러뷰를 갱신해주는 메소드
    fun setUpdatedData(items: ArrayList<CreditItem>) {
        this.items = items
        notifyDataSetChanged()
    }
}