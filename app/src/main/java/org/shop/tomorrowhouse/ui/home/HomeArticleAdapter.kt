package org.shop.tomorrowhouse.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.shop.tomorrowhouse.data.ArticleModel
import org.shop.tomorrowhouse.databinding.ItemArticleBinding

class HomeArticleAdapter(private val onItemClicked: (ArticleModel) -> Unit) :
    ListAdapter<ArticleModel, HomeArticleAdapter.ArticleViewHolder>(diffUtil) {
    inner class ArticleViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticleModel) {
            Glide.with(binding.thumnailImageView).load(article.imageUrl)
                .into(binding.thumnailImageView)
            binding.descriptionTextView.text = article.description

            binding.root.setOnClickListener {
                onItemClicked(article)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ItemArticleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {
            // id와 같은 고유값 비교
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem.articleId == newItem.articleId
            }

            // 그냥 두개 비교
            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}