package com.dorjiyangzom.cookmateapp.ui.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.dorjiyangzom.cookmateapp.R
import com.dorjiyangzom.cookmateapp.adapters.MealRecyclerAdapter
import com.dorjiyangzom.cookmateapp.adapters.SetOnMealClickListener
import com.dorjiyangzom.cookmateapp.data.pojo.Meal
import com.dorjiyangzom.cookmateapp.databinding.ActivityCategoriesBinding
import com.dorjiyangzom.cookmateapp.mvvm.MealActivityMVVM
import com.dorjiyangzom.cookmateapp.ui.fragments.HomeFragment.Companion.CATEGORY_NAME
import com.dorjiyangzom.cookmateapp.ui.fragments.HomeFragment.Companion.MEAL_ID
import com.dorjiyangzom.cookmateapp.ui.fragments.HomeFragment.Companion.MEAL_STR
import com.dorjiyangzom.cookmateapp.ui.fragments.HomeFragment.Companion.MEAL_THUMB

class MealActivity : AppCompatActivity() {

    private lateinit var mealActivityMvvm: MealActivityMVVM
    private lateinit var binding: ActivityCategoriesBinding
    private lateinit var myAdapter: MealRecyclerAdapter
    private var categoryNme = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mealActivityMvvm = ViewModelProvider(this)[MealActivityMVVM::class.java]

        startLoading()
        prepareRecyclerView()

        // Fetch meals for the category
        mealActivityMvvm.getMealsByCategory(getCategory())

        // Observe meals using lambda
        mealActivityMvvm.observeMeal().observe(this) { meals ->
            hideLoading()
            if (meals.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "No meals in this category", Toast.LENGTH_SHORT).show()
                onBackPressedDispatcher.onBackPressed()
            } else {
                myAdapter.setCategoryList(meals)
                binding.tvCategoryCount.text = "$categoryNme : ${meals.size}"
            }
        }

        // Set click listener for meals
        myAdapter.setOnMealClickListener(object : SetOnMealClickListener {
            override fun setOnClickListener(meal: Meal) {
                val intent = Intent(applicationContext, MealDetailesActivity::class.java).apply {
                    putExtra(MEAL_ID, meal.idMeal)
                    putExtra(MEAL_STR, meal.strMeal)
                    putExtra(MEAL_THUMB, meal.strMealThumb)
                }
                startActivity(intent)
            }
        })
    }

    private fun hideLoading() {
        binding.apply {
            loadingGifMeals.visibility = View.INVISIBLE
            mealRoot.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.white))
        }
    }

    private fun startLoading() {
        binding.apply {
            loadingGifMeals.visibility = View.VISIBLE
            mealRoot.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.g_loading))
        }
    }

    private fun getCategory(): String {
        val category = intent.getStringExtra(CATEGORY_NAME) ?: ""
        categoryNme = category
        return category
    }

    private fun prepareRecyclerView() {
        myAdapter = MealRecyclerAdapter()
        binding.mealRecyclerview.apply {
            adapter = myAdapter
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        }
    }
}
