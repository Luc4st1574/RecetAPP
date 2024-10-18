package com.example.recetapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.recetapp.databinding.ActivitySearchBinding
import kotlinx.coroutines.*

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var rvAdapter: SearchAdapter
    private lateinit var dataLists: ArrayList<Recipe>
    private lateinit var recipes: List<Recipe?>
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout with view binding
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable edge-to-edge system UI behavior
        enableEdgeToEdge()

        // Focus on the search bar
        binding.search.requestFocus()

        // Initialize Room database
        val db = Room.databaseBuilder(this@SearchActivity, AppDatabase::class.java, "db_name")
            .fallbackToDestructiveMigration()
            .createFromAsset("recipe.db")
            .build()
        val daoObject = db.getDao()

        // Fetch recipes asynchronously using Coroutines
        GlobalScope.launch(Dispatchers.IO) {
            recipes = daoObject.getAll() ?: emptyList()

            // Once the data is fetched, update UI on the main thread
            withContext(Dispatchers.Main) {
                setUpRecyclerView()
            }
        }

        // Search text listener with debouncing
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Cancel any previous search job
                searchJob?.cancel()

                // Launch a new search after a debounce delay
                searchJob = GlobalScope.launch(Dispatchers.Main) {
                    delay(300)  // Debounce for 300ms
                    s?.let {
                        if (it.isNotEmpty()) {
                            filterData(it.toString())  // Filter data
                        } else {
                            rvAdapter.filterList(ArrayList(recipes.filterNotNull()))  // Reset list
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Handle window insets for edge-to-edge design
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Go back button functionality
        binding.goBackHome.setOnClickListener {
            finish()  // Close the activity and return to the previous screen
        }
    }

    // Filter the data based on the search query
    private fun filterData(filterText: String) {
        val filterData = ArrayList<Recipe>()
        for (recipe in recipes) {
            recipe?.let {
                if (it.tittle.lowercase().contains(filterText.lowercase())) {
                    filterData.add(it)
                }
            }
        }
        rvAdapter.filterList(filterData)  // Update the RecyclerView with the filtered data
    }

    // Set up the RecyclerView with the initial data
    private fun setUpRecyclerView() {
        dataLists = ArrayList()
        binding.rvSearch.layoutManager = LinearLayoutManager(this)

        // Add only recipes that are marked as "Popular"
        for (recipe in recipes) {
            recipe?.let {
                if (it.category.contains("Popular")) {
                    dataLists.add(it)
                }
            }
        }
        rvAdapter = SearchAdapter(dataLists, this)
        binding.rvSearch.adapter = rvAdapter
    }
}
