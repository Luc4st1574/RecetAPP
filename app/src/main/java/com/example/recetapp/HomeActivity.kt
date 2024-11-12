package com.example.recetapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.bumptech.glide.Glide
import com.example.recetapp.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HomeActivity : AppCompatActivity() {
    private lateinit var rvAdapter: PopularAdapter
    private lateinit var dataList: ArrayList<Recipe>
    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize edge-to-edge and view binding
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerView()
        setupCategoryButtons()

        // Apply system bar padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.go_up)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        if (currentUser != null) {
            displayUserInfo(currentUser)
        } else {
            redirectToLogin()
        }

        // Set up the logout button
        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            redirectToLogin()
        }
    }

    private fun displayUserInfo(user: FirebaseUser) {
        // Display the user's display name or a default value
        val username = user.displayName ?: "User"
        binding.lblUser.text = username

        // Display the user's profile picture or a default placeholder
        val photoUrl = user.photoUrl
        if (photoUrl != null) {
            // Use Glide to load the profile picture into the ImageView
            Glide.with(this)
                .load(photoUrl)  // your image URL or resource
                .placeholder(R.drawable.ic_user) // default placeholder
                .error(R.drawable.ic_user) // error placeholder
                .circleCrop() // Apply circular crop to make it rounded
                .into(binding.profileImage)
        } else {
            // Set a default image if the user doesn't have a profile picture
            binding.profileImage.setImageResource(R.drawable.ic_user)
        }
    }

    private fun redirectToLogin() {
        // Redirect to LoginActivity and clear the activity stack
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupCategoryButtons() {
        binding.search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        binding.salad.setOnClickListener {
            val intent = Intent(this@HomeActivity, CategoryActivity::class.java)
            intent.putExtra("TITTLE", "Ensaladas")
            intent.putExtra("CATEGORY", "Ensalada")
            startActivity(intent)
        }

        binding.mainDish.setOnClickListener {
            val intent = Intent(this@HomeActivity, CategoryActivity::class.java)
            intent.putExtra("TITTLE", "Platos Principales")
            intent.putExtra("CATEGORY", "Plato")
            startActivity(intent)
        }

        binding.drinks.setOnClickListener {
            val intent = Intent(this@HomeActivity, CategoryActivity::class.java)
            intent.putExtra("TITTLE", "Bebidas")
            intent.putExtra("CATEGORY", "Bebidas")
            startActivity(intent)
        }

        binding.dessert.setOnClickListener {
            val intent = Intent(this@HomeActivity, CategoryActivity::class.java)
            intent.putExtra("TITTLE", "Postres")
            intent.putExtra("CATEGORY", "Postres")
            startActivity(intent)
        }
    }

    private fun setUpRecyclerView() {
        dataList = ArrayList()
        binding.rvPopular.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize Room database and fetch data
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "db_name")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .createFromAsset("recipe.db")
            .build()

        val daoObject = db.getDao()
        val recipes = daoObject.getAll()?.filterNotNull() ?: emptyList()  // Ensure non-null items in the list
        for (recipe in recipes) {
            if (recipe.category.contains("Popular")) {
                dataList.add(recipe)
            }
        }

        rvAdapter = PopularAdapter(dataList, this)
        binding.rvPopular.adapter = rvAdapter
    }
}
