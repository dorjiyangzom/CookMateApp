package com.dorjiyangzom.cookmateapp.mvvm

import android.util.Log
import androidx.lifecycle.*
import com.dorjiyangzom.cookmateapp.data.pojo.MealDetail
import com.dorjiyangzom.cookmateapp.data.pojo.RandomMealResponse
import com.dorjiyangzom.cookmateapp.data.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class SearchMVVM : ViewModel() {

    private val _searchedMeal = MutableLiveData<UiState<MealDetail>>()
    val searchedMeal: LiveData<UiState<MealDetail>> get() = _searchedMeal

    fun searchMealDetail(name: String) {
        viewModelScope.launch {
            _searchedMeal.value = UiState.Loading
            try {
                val response: RandomMealResponse = RetrofitInstance.foodApi.getMealByName(name)

                val meal = response.meals?.firstOrNull()
                if (meal != null) {
                    _searchedMeal.value = UiState.Success(meal)
                } else {
                    _searchedMeal.value = UiState.Error("No such meal found")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error searching meal: ${e.message}", e)
                _searchedMeal.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    companion object {
        private const val TAG = "SearchMVVM"
    }
}
