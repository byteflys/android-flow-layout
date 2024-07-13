package com.android.code

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.code.databinding.ActivityHomeBinding
import com.android.library.flowlayout.applyViewAdapter
import java.util.UUID

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        val simulated = mutableListOf<String>().apply {
            for (i in 0 until 30)
                add(UUID.randomUUID().toString())
        }
        binding.flowLayout.applyViewAdapter({ Button(this) }, simulated) { view, data ->
            view.text = data
            view.setPadding(15, 15, 15, 15)
            val lp = view.layoutParams
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }
}