package com.dorjiyangzom.cookmateapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dorjiyangzom.cookmateapp.adapters.MealRecyclerAdapter
import com.dorjiyangzom.cookmateapp.data.pojo.MealDetail
import com.dorjiyangzom.cookmateapp.databinding.FragmentSearchBinding
import com.dorjiyangzom.cookmateapp.mvvm.SearchMVVM
import com.dorjiyangzom.cookmateapp.ui.activites.MealDetailesActivity
import com.dorjiyangzom.cookmateapp.ui.fragments.HomeFragment.Companion.MEAL_ID
import com.dorjiyangzom.cookmateapp.ui.fragments.HomeFragment.Companion.MEAL_STR
import com.dorjiyangzom.cookmateapp.ui.fragments.HomeFragment.Companion.MEAL_THUMB

class SearchFragment : Fragment() {

    private lateinit var myAdapter: MealRecyclerAdapter
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchMvvm: SearchMVVM

    private var mealId = ""
    private var mealStr = ""
    private var mealThumb = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myAdapter = MealRecyclerAdapter()
        searchMvvm = ViewModelProvider(this)[SearchMVVM::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSearchClick()
        observeSearchLiveData()
        setOnMealCardClick()
    }

    private fun setOnMealCardClick() {
        binding.searchedMealCard.setOnClickListener {
            val intent = Intent(requireContext(), MealDetailesActivity::class.java).apply {
                putExtra(MEAL_ID, mealId)
                putExtra(MEAL_STR, mealStr)
                putExtra(MEAL_THUMB, mealThumb)
            }
            startActivity(intent)
        }
    }

    private fun onSearchClick() {
        binding.icSearch.setOnClickListener {
            val query = binding.edSearch.text.toString().trim()
            if (query.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a meal name", Toast.LENGTH_SHORT).show()
            } else {
                searchMvvm.searchMealDetail(query, requireContext())
            }
        }
    }

    private fun observeSearchLiveData() {
        searchMvvm.observeSearchLiveData().observe(viewLifecycleOwner) { meal ->
            if (meal == null) {
                Toast.makeText(requireContext(), "No such meal", Toast.LENGTH_SHORT).show()
            } else {
                updateMealCard(meal)
            }
        }
    }

    private fun updateMealCard(meal: MealDetail) {
        mealId = meal.idMeal
        mealStr = meal.strMeal
        mealThumb = meal.strMealThumb

        Glide.with(requireContext())
            .load(meal.strMealThumb)
            .into(binding.imgSearchedMeal)

        binding.tvSearchedMeal.text = meal.strMeal
        binding.searchedMealCard.visibility = View.VISIBLE
    }
}
