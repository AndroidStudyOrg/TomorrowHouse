package org.shop.tomorrowhouse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.shop.tomorrowhouse.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }


    }
}