package org.shop.tomorrowhouse.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import org.shop.tomorrowhouse.data.ArticleModel
import org.shop.tomorrowhouse.databinding.FragmentBookmarkArticleBinding

class BookmarkArticleFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkArticleBinding
    private lateinit var bookmarkAdapter: BookmarkArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookmarkArticleBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setupWithNavController(findNavController())

        bookmarkAdapter = BookmarkArticleAdapter {
            findNavController().navigate(
                BookmarkArticleFragmentDirections.actionBookmarkArticleFragmentToArticleFragment(
                    it.articleId.orEmpty()
                )
            )
        }

        binding.articleRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = bookmarkAdapter
        }

        val uid = Firebase.auth.currentUser?.uid.orEmpty()
        Firebase.firestore.collection("bookmark").document(uid).get().addOnSuccessListener {
            val list = it.get("articleIds") as List<*>

            if (list.isNotEmpty()) {
                Firebase.firestore.collection("articles").whereIn("articleId", list).get()
                    .addOnSuccessListener { result ->
                        bookmarkAdapter.submitList(result.map { article -> article.toObject<ArticleModel>() })
                    }.addOnFailureListener { error ->
                        error.printStackTrace()
                    }
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }
}