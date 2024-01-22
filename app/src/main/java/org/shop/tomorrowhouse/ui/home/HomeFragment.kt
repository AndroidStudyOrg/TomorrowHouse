package org.shop.tomorrowhouse.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import org.shop.tomorrowhouse.data.ArticleModel
import org.shop.tomorrowhouse.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

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

        val db = Firebase.firestore
        db.collection("articles").document("Jk9cTs8U1Ek1ezm0rYI1").get()
            .addOnSuccessListener { result ->
                val article = result.toObject<ArticleModel>()

                Log.e("HomeFragment - result", result.toString())
                Log.e("HomeFragment - getArticle", article.toString())
            }.addOnFailureListener {
                it.printStackTrace()
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