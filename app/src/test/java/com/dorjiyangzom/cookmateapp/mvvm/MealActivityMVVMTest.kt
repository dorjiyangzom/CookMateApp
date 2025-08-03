package com.dorjiyangzom.cookmateapp.mvvm

import android.util.Log
import androidx.lifecycle.*
import com.dorjiyangzom.cookmateapp.data.pojo.Meal
import com.dorjiyangzom.cookmateapp.data.pojo.MealsResponse
import com.dorjiyangzom.cookmateapp.data.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

// --- UI State Wrapper ---
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class MealActivityMVVM : ViewModel() {

    private val _meals = MutableLiveData<UiState<List<Meal>>>()
    val meals: LiveData<UiState<List<Meal>>> get() = _meals

    fun getMealsByCategory(category: String) {
        viewModelScope.launch {
            _meals.value = UiState.Loading
            try {
                val response: MealsResponse = RetrofitInstance.foodApi.getMealsByCategory(category)
                _meals.value = UiState.Success(response.meals)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching meals: ${e.message}", e)
                _meals.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    companion object {
        private const val TAG = "MealActivityMVVM"
    }
}
