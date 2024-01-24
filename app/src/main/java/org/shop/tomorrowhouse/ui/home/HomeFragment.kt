package org.shop.tomorrowhouse.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import org.shop.tomorrowhouse.data.ArticleModel
import org.shop.tomorrowhouse.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var articleAdapter: HomeArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        articleAdapter = HomeArticleAdapter {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToArticleFragment(articleId = it.articleId.orEmpty()))
        }

        binding.homeRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = articleAdapter
        }

        Firebase.firestore.collection("articles").get().addOnSuccessListener { result ->
            val list = result.map {
                it.toObject<ArticleModel>()
            }
            articleAdapter.submitList(list)
        }.addOnFailureListener {

        }

        setUpWriteButton()
    }

    private fun setUpWriteButton() {
        binding.writeButton.setOnClickListener {
            if (Firebase.auth.currentUser != null) {
                val action = HomeFragmentDirections.actionHomeFragmentToWriteArticleFragment()
                findNavController().navigate(action)
            } else {
                Snackbar.make(binding.root, "로그인 후 사용해 주세요", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}