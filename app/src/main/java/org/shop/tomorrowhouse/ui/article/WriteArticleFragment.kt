package org.shop.tomorrowhouse.ui.article

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
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
            } else {
                Log.d("PhotoPicker", "No media selected")
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

        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        binding.photoImageView.setOnClickListener {

        }

        binding.deleteButton.setOnClickListener {

        }

        binding.submitButton.setOnClickListener {
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                val fileName = "${UUID.randomUUID()}.png"
                Firebase.storage.reference.child("articles/photo").child(fileName)
                    .putFile(photoUri).addOnCompleteListener{ task ->
                        if (task.isSuccessful){
                            // 이미지가 성공적으로 업로드 됐으니까 다운로드 URL을 받아올 수 있다
                            Log.e("WriteArticleFrag - UpLoad Success", task.toString())
                            Firebase.storage.reference.child("articles/photo/$fileName").downloadUrl.addOnSuccessListener {
                                Log.e("WriteArticleFrag - DownLoad Success", it.toString())

                            }.addOnFailureListener {

                            }
                        }else{
                            // Error Handler
                            task.exception?.printStackTrace()
                        }
                    }
            } else {
                Snackbar.make(binding.root, "이미지가 선택되지 않았습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(WriteArticleFragmentDirections.actionBack())
        }
    }
}