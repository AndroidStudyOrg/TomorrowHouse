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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
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

        setupWriteButton()
        setupBookmarkButton()
        setupRecyclerView()
        fetchFirestoreData()
    }

    private fun setupWriteButton() {
        binding.writeButton.setOnClickListener {
            if (Firebase.auth.currentUser != null) {
                val action = HomeFragmentDirections.actionHomeFragmentToWriteArticleFragment()
                findNavController().navigate(action)
            } else {
                Snackbar.make(binding.root, "로그인 후 사용해 주세요", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBookmarkButton() {
        binding.bookmarkImageButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToBookmarkArticleFragment())
        }
    }

    private fun setupRecyclerView() {
        articleAdapter = HomeArticleAdapter(
            onItemClicked = {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToArticleFragment(
                        articleId = it.articleId.orEmpty()
                    )
                )
            }, onBookmarkClicked = { articleId, isBookmark ->
                val uid = Firebase.auth.currentUser?.uid ?: return@HomeArticleAdapter
                Firebase.firestore.collection("bookmark").document(uid).update(
                    "articleIds", if (isBookmark) {
                        FieldValue.arrayUnion(articleId)
                    } else {
                        FieldValue.arrayRemove(articleId)
                    }
                ).addOnFailureListener {
                    if (it is FirebaseFirestoreException && it.code == FirebaseFirestoreException.Code.NOT_FOUND) {
                        if (isBookmark) {
                            Firebase.firestore.collection("bookmark").document(uid).set(
                                hashMapOf(
                                    "articleIds" to listOf(articleId)
                                )
                            )
                        }
                    }
                }
            }
        )

        binding.homeRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = articleAdapter
        }
    }

    private fun fetchFirestoreData() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        Firebase.firestore.collection("bookmark").document(uid).get().addOnSuccessListener {
            Log.e("HomeFragment", "it: $it")
            val bookmarList = it.get("articleIds") as? List<*>
            Log.e("HomeFragment fetchFirestoreData", "BookmarkList: $bookmarList")

            Firebase.firestore.collection("articles").get().addOnSuccessListener { result ->
                val list =
                    result.map { snapshot -> snapshot.toObject<ArticleModel>() }.map { model ->
                        Log.e("HomeFragment ArticleItemMapping", "Model: $model")
                        ArticleItem(
                            articleId = model.articleId.orEmpty(),
                            description = model.description.orEmpty(),
                            imageUrl = model.imageUrl.orEmpty(),
                            isBookmark = bookmarList?.contains(model.articleId.orEmpty()) ?: false
                        )
                    }
                articleAdapter.submitList(list)
            }.addOnFailureListener {

            }
        }
    }
}