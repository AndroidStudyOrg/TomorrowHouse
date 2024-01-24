package org.shop.tomorrowhouse.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.shop.tomorrowhouse.R
import org.shop.tomorrowhouse.databinding.ItemArticleBinding

class HomeArticleAdapter(
    private val onItemClicked: (ArticleItem) -> Unit,
    private val onBookmarkClicked: (String, Boolean) -> Unit
) :
    ListAdapter<ArticleItem, HomeArticleAdapter.ArticleViewHolder>(diffUtil) {
    inner class ArticleViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticleItem) {
            Glide.with(binding.thumnailImageView).load(article.imageUrl)
                .into(binding.thumnailImageView)
            binding.descriptionTextView.text = article.description

            binding.root.setOnClickListener {
                onItemClicked(article)
            }

            if (article.isBookmark) {
                binding.bookmarkImageButton.setBackgroundResource(R.drawable.baseline_bookmark_24)
            } else {
                binding.bookmarkImageButton.setBackgroundResource(R.drawable.baseline_bookmark_border_24)
            }

            binding.bookmarkImageButton.setOnClickListener {
                onBookmarkClicked.invoke(article.articleId, article.isBookmark.not())

                article.isBookmark = article.isBookmark.not()
                if (article.isBookmark) {
                    binding.bookmarkImageButton.setBackgroundResource(R.drawable.baseline_bookmark_24)
                } else {
                    binding.bookmarkImageButton.setBackgroundResource(R.drawable.baseline_bookmark_border_24)
                }
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
        val diffUtil = object : DiffUtil.ItemCallback<ArticleItem>() {
            // id와 같은 고유값 비교
            override fun areItemsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
                return oldItem.articleId == newItem.articleId
            }

            // 그냥 두개 비교
            override fun areContentsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}