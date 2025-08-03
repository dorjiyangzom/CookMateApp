package com.dorjiyangzom.cookmateapp.ui.activites

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dorjiyangzom.cookmateapp.R
import com.dorjiyangzom.cookmateapp.data.pojo.MealDB
import com.dorjiyangzom.cookmateapp.data.pojo.MealDetail
import com.dorjiyangzom.cookmateapp.databinding.ActivityMealDetailesBinding
import com.dorjiyangzom.cookmateapp.mvvm.DetailsMVVM
import com.dorjiyangzom.cookmateapp.ui.fragments.HomeFragment.Companion.MEAL_ID
import com.dorjiyangzom.cookmateapp.ui.fragments.HomeFragment.Companion.MEAL_STR
import com.dorjiyangzom.cookmateapp.ui.fragments.HomeFragment.Companion.MEAL_THUMB
import com.google.android.material.snackbar.Snackbar

class MealDetailesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMealDetailesBinding
    private lateinit var detailsMVVM: DetailsMVVM

    private var mealId = ""
    private var mealStr = ""
    private var mealThumb = ""
    private var ytUrl = ""
    private lateinit var dtMeal: MealDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailsMVVM = ViewModelProvider(this)[DetailsMVVM::class.java]
        binding = ActivityMealDetailesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading()
        getMealInfoFromIntent()
        setUpViewWithMealInformation()
        setFloatingButtonStatus()

        detailsMVVM.getMealById(mealId)

        detailsMVVM.observeMealDetail().observe(this) { mealDetails ->
            if (!mealDetails.isNullOrEmpty()) {
                setTextsInViews(mealDetails[0])
                stopLoading()
            } else {
                stopLoading()
                Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.no_meal_details_found),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        binding.imgYoutube.setOnClickListener {
            if (ytUrl.isNotBlank()) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ytUrl)))
            }
        }

        binding.btnSave.setOnClickListener {
            if (isMealSavedInDatabase()) {
                deleteMeal()
                binding.btnSave.setImageResource(R.drawable.ic_baseline_save_24)
                Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.meal_deleted),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                saveMeal()
                binding.btnSave.setImageResource(R.drawable.ic_saved)
                Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.meal_saved),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun deleteMeal() {
        detailsMVVM.deleteMealById(mealId)
    }

    private fun setFloatingButtonStatus() {
        if (isMealSavedInDatabase()) {
            binding.btnSave.setImageResource(R.drawable.ic_saved)
        } else {
            binding.btnSave.setImageResource(R.drawable.ic_baseline_save_24)
        }
    }

    private fun isMealSavedInDatabase(): Boolean {
        return detailsMVVM.isMealSavedInDatabase(mealId)
    }

    private fun saveMeal() {
        val meal = MealDB(
            dtMeal.idMeal.toInt(),
            dtMeal.strMeal,
            dtMeal.strArea,
            dtMeal.strCategory,
            dtMeal.strInstructions,
            dtMeal.strMealThumb,
            dtMeal.strYoutube
        )
        detailsMVVM.insertMeal(meal)
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.visibility = View.GONE
        binding.imgYoutube.visibility = View.INVISIBLE
    }

    private fun stopLoading() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.btnSave.visibility = View.VISIBLE
        binding.imgYoutube.visibility = View.VISIBLE
    }

    private fun setTextsInViews(meal: MealDetail) {
        this.dtMeal = meal
        ytUrl = meal.strYoutube
        binding.apply {
            tvInstructions.text = getString(R.string.instructions_label)
            tvContent.text = meal.strInstructions
            tvAreaInfo.visibility = View.VISIBLE
            tvCategoryInfo.visibility = View.VISIBLE
            tvAreaInfo.text = getString(R.string.area_info, meal.strArea)
            tvCategoryInfo.text = getString(R.string.category_info, meal.strCategory)
            imgYoutube.visibility = View.VISIBLE
        }
    }

    private fun setUpViewWithMealInformation() {
        binding.apply {
            collapsingToolbar.title = mealStr
            Glide.with(applicationContext)
                .load(mealThumb)
                .into(imgMealDetail)
        }
    }

    private fun getMealInfoFromIntent() {
        val tempIntent = intent
        this.mealId = tempIntent.getStringExtra(MEAL_ID) ?: ""
        this.mealStr = tempIntent.getStringExtra(MEAL_STR) ?: ""
        this.mealThumb = tempIntent.getStringExtra(MEAL_THUMB) ?: ""
    }
}
