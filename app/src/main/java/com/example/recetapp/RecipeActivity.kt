package com.example.recetapp

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.recetapp.databinding.ActivityRecipeBinding

class RecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeBinding
    var imgCrop = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        Glide.with(this).load(intent.getStringExtra("img")).into(binding.itemImage)
        binding.tittle.text = intent.getStringExtra("tittle")
        binding.stepData.text = intent.getStringExtra("des")
        var ing = intent.getStringExtra("ing")?.split("\n".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        binding.time.text = ing?.get(0)

        for (i in 1 until ing!!.size) {
                binding.ingData.text = """${binding.ingData.text} 🔴 ${ing[i]}""".trimIndent()
        }
        binding.step.background= null
        

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.fullScreen.setOnClickListener {
            if (imgCrop) {
                binding.itemImage.scaleType = ImageView.ScaleType.FIT_CENTER
                Glide.with(this).load(intent.getStringExtra("img")).into(binding.itemImage)
                binding.fullScreen.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
                binding.shade.visibility = ImageView.GONE
                imgCrop = false
            } else {
                binding.itemImage.scaleType = ImageView.ScaleType.CENTER_CROP
                Glide.with(this).load(intent.getStringExtra("img")).into(binding.itemImage)
                binding.fullScreen.setColorFilter(null)
                binding.shade.visibility = ImageView.VISIBLE
                imgCrop = true
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}