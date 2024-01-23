package org.shop.tomorrowhouse.ui.article

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import org.shop.tomorrowhouse.data.ArticleModel
import org.shop.tomorrowhouse.databinding.FragmentWriteArticleBinding
import java.util.UUID

class WriteArticleFragment : Fragment() {
    private lateinit var binding: FragmentWriteArticleBinding

    private var selectedUri: Uri? = null
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                selectedUri = uri
                binding.photoImageView.setImageURI(uri)
                binding.addButton.isVisible = false
                binding.deleteButton.isVisible = true
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteArticleBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startPicker()
        setUpPhotoImageView()
        setUpDeleteButton()
        setUpSubmitButton()
        setUpBackButton()
    }

    private fun startPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun setUpPhotoImageView() {
        binding.photoImageView.setOnClickListener {
            // 처음에도 시작하고, photoImageView를 눌렀을 때에도 시작
            if (selectedUri == null) {
                startPicker()
            }
        }
    }

    private fun setUpDeleteButton() {
        binding.deleteButton.setOnClickListener {
            binding.photoImageView.setImageURI(null)
            selectedUri = null
            binding.deleteButton.isVisible = false
            binding.addButton.isVisible = true
        }
    }

    private fun setUpSubmitButton() {
        binding.submitButton.setOnClickListener {
            showProgress()
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                uploadImage(uri = photoUri, successHandler = {
                    // FireStore에 데이터 업로드
                    uploadArticle(it, binding.descriptionEditText.text.toString())
                }, errorHandler = {
                    Snackbar.make(binding.root, "이미지 업로드에 실패했습니다.", Snackbar.LENGTH_LONG).show()
                    hideProgress()
                })
            } else {
                Snackbar.make(binding.root, "이미지가 선택되지 않았습니다.", Snackbar.LENGTH_SHORT).show()
                hideProgress()
            }
        }
    }

    private fun setUpBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigate(WriteArticleFragmentDirections.actionBack())
        }
    }

    private fun showProgress() {
        binding.progressBarLayout.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBarLayout.isVisible = false
    }

    private fun uploadImage(
        uri: Uri,
        successHandler: (String) -> Unit,
        errorHandler: (Throwable?) -> Unit
    ) {
        val fileName = "${UUID.randomUUID()}.png"
        Firebase.storage.reference.child("articles/photo").child(fileName)
            .putFile(uri).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 이미지가 성공적으로 업로드 됐으니까 다운로드 URL을 받아올 수 있다
                    Log.e("WriteArticleFrag - UpLoad Success", task.toString())
                    Firebase.storage.reference.child("articles/photo/$fileName").downloadUrl.addOnSuccessListener {
                        Log.e("WriteArticleFrag - DownLoad Success", it.toString())
                        successHandler(it.toString())
                    }.addOnFailureListener {
                        errorHandler(it)
                    }
                } else {
                    // Error Handler
                    errorHandler(task.exception)
                }
            }
    }

    private fun uploadArticle(photoUrl: String, description: String) {
        val articleId = UUID.randomUUID().toString()
        val articleModel = ArticleModel(
            articleId = articleId,
            createdAt = System.currentTimeMillis(),
            description = description,
            imageUrl = photoUrl
        )
        Firebase.firestore.collection("articles").document(articleId).set(articleModel)
            .addOnSuccessListener {
                findNavController().navigate(WriteArticleFragmentDirections.actionWriteArticleFragmentToHomeFragment())
                hideProgress()
            }.addOnFailureListener {
                it.printStackTrace()
                Snackbar.make(binding.root, "글 작성에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
                hideProgress()
            }
    }
}