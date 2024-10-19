package com.example.recetapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.recetapp.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HomeActivity : AppCompatActivity() {
    private lateinit var rvAdapter: PopularAdapter
    private lateinit var dataLists: ArrayList<Recipe>
    private lateinit var binding: ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize edge-to-edge and view binding
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerView()
        binding.search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        binding.salad.setOnClickListener{
            var myIntent = Intent(this@HomeActivity, CategoryActivity::class.java)
            myIntent.putExtra("TITTLE", "Ensaladas")  // This is what will be displayed in the tittle TextView
            myIntent.putExtra("CATEGORY", "Ensalada") // This filters the recipes by category
            startActivity(myIntent)
        }

        binding.mainDish.setOnClickListener{
            var myIntent = Intent(this@HomeActivity, CategoryActivity::class.java)
            myIntent.putExtra("TITTLE", "Platos Principales")
            myIntent.putExtra("CATEGORY", "Plato")
            startActivity(myIntent)
        }

        binding.drinks.setOnClickListener{
            var myIntent = Intent(this@HomeActivity, CategoryActivity::class.java)
            myIntent.putExtra("TITTLE", "Bebidas")
            myIntent.putExtra("CATEGORY", "Bebidas")
            startActivity(myIntent)
        }

        binding.dessert.setOnClickListener{
            var myIntent = Intent(this@HomeActivity, CategoryActivity::class.java)
            myIntent.putExtra("TITTLE", "Postres")
            myIntent.putExtra("CATEGORY", "Postres")
            startActivity(myIntent)
        }

        // Apply system bar padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Get the current logged-in user
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        if (currentUser != null) {
            // Extract the username from the email before '@'
            val email = currentUser.email
            val username = email?.substringBefore("@")
            binding.lblUser.text = "$username"
        } else {
            // If the user is not logged in, redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Set up the logout button
        binding.btnLogout.setOnClickListener {
            // Sign out from FirebaseAuth
            firebaseAuth.signOut()
            // Redirect to LoginActivity after signing out
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // Close the current activity
        }
    }

    private fun setUpRecyclerView() {
        dataLists = ArrayList()
        binding.rvPopular.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        var db = Room.databaseBuilder(this@HomeActivity, AppDatabase::class.java, "db_name")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .createFromAsset("recipe.db")
            .build()
        var daoObject = db.getDao()
        var recipes = daoObject.getAll()
        for (i in recipes!!.indices) {
            if (recipes[i]!!.category.contains("Popular")) {
                dataLists.add(recipes[i]!!)
            }
            rvAdapter = PopularAdapter(dataLists, this)
            binding.rvPopular.adapter = rvAdapter
        }
    }
}
