package com.example.recetapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.recetapp.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {
    private lateinit var rvAdapter: CategoryAdapter
    private lateinit var dataLists: ArrayList<Recipe>
    private val binding by lazy {
        ActivityCategoryBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.tittle.text = intent.getStringExtra("TITTLE")
        // Set the category title inside the tittle TextView
        binding.tittle.text = intent.getStringExtra("TITTLE")

        setUpRecyclerView()

        // System bar insets handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.go_up)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.goBackHome.setOnClickListener {
            finish()  // Close the activity and return to the previous screen
        }
    }

    private fun setUpRecyclerView() {
        dataLists = ArrayList()
        binding.rvCategory.layoutManager =
            LinearLayoutManager(this)

        // Initialize the database
        var db = Room.databaseBuilder(this@CategoryActivity, AppDatabase::class.java, "db_name")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .createFromAsset("recipe.db")
            .build()

        // Retrieve all recipes from the database
        var daoObject = db.getDao()
        var recipes = daoObject.getAll()

        // Filter recipes based on the selected category
        for (i in recipes!!.indices) {
            if (recipes[i]!!.category.contains(intent.getStringExtra("CATEGORY")!!)) {
                dataLists.add(recipes[i]!!)
            }
        }

        // Set the adapter outside the loop and notify it of the data change
        rvAdapter = CategoryAdapter(dataLists, this)
        binding.rvCategory.adapter = rvAdapter
        rvAdapter.notifyDataSetChanged() // Notify adapter to update the UI
    }
}
