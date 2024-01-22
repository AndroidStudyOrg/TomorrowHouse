package org.shop.tomorrowhouse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.shop.tomorrowhouse.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {
    private lateinit var binding: FragmentAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSignUpButton()
        setUpSignInOutButton()
    }

    override fun onStart() {
        super.onStart()

        if (Firebase.auth.currentUser == null) {
            // 로그아웃 상태
            initViewsToSignOutState()
        } else {
            // 로그인 상태
            initViewsToSignInState()
        }
    }

    private fun setUpSignUpButton() {
        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "이메일 또는 패스워드를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(binding.root, "회원가입에 성공했습니다.", Snackbar.LENGTH_SHORT).show()
                        initViewsToSignInState()
                    } else {
                        Snackbar.make(binding.root, "회원가입에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun setUpSignInOutButton() {
        binding.signInOutButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (Firebase.auth.currentUser == null) {
                // 로그인
                if (email.isEmpty() || password.isEmpty()) {
                    Snackbar.make(binding.root, "이메일 또는 패스워드를 입력해주세요.", Snackbar.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                Firebase.auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // 로그인 된 상태
                            initViewsToSignInState()
                        } else {
                            Snackbar.make(
                                binding.root,
                                "로그인에 실패했습니다. 이메일 또는 패스워드를 확인해주세요.",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                // 로그아웃
                Firebase.auth.signOut()
                initViewsToSignOutState()
            }
        }
    }

    private fun initViewsToSignInState() {
        binding.emailEditText.setText(Firebase.auth.currentUser?.email)
        binding.emailEditText.isEnabled = false
        binding.passwordEditText.isVisible = false
        binding.signInOutButton.text = resources.getString(R.string.signOut)
        binding.signUpButton.isEnabled = false
    }

    private fun initViewsToSignOutState() {
        binding.emailEditText.text.clear()
        binding.emailEditText.isEnabled = true
        binding.passwordEditText.text.clear()
        binding.passwordEditText.isVisible = true
        binding.signInOutButton.text = resources.getString(R.string.signIn)
        binding.signUpButton.isEnabled = true
    }
}