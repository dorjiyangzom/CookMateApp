package com.dorjiyangzom.cookmateapp.mvvm

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.dorjiyangzom.cookmateapp.data.db.MealsDatabase
import com.dorjiyangzom.cookmateapp.data.db.Repository
import com.dorjiyangzom.cookmateapp.data.pojo.*
import com.dorjiyangzom.cookmateapp.data.retrofit.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ---- UI State Wrapper ----
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// ---- Repository ----
class FoodRepository(private val localRepo: Repository) {

    suspend fun getCategories(): CategoryResponse {
        return RetrofitInstance.foodApi.getCategories()
    }

    suspend fun getRandomMeal(): RandomMealResponse {
        return RetrofitInstance.foodApi.getRandomMeal()
    }

    suspend fun getMealsByCategory(category: String): MealsResponse {
        return RetrofitInstance.foodApi.getMealsByCategory(category)
    }

    suspend fun getMealById(id: String): RandomMealResponse {
        return RetrofitInstance.foodApi.getMealById(id)
    }

    suspend fun insertMeal(meal: MealDB) = localRepo.insertFavoriteMeal(meal)
    suspend fun deleteMeal(meal: MealDB) = localRepo.deleteMeal(meal)
    suspend fun deleteMealById(mealId: String) = localRepo.deleteMealById(mealId)
    suspend fun getMealFromDbById(mealId: String): MealDB? = localRepo.getMealById(mealId)
    fun getAllSavedMeals(): LiveData<List<MealDB>> = localRepo.mealList
}

// ---- MainFragMVVM ----
class MainFragMVVM(application: Application) : AndroidViewModel(application) {

    private val repository: FoodRepository

    private val _categories = MutableLiveData<UiState<CategoryResponse>>()
    val categories: LiveData<UiState<CategoryResponse>> get() = _categories

    private val _randomMeal = MutableLiveData<UiState<RandomMealResponse>>()
    val randomMeal: LiveData<UiState<RandomMealResponse>> get() = _randomMeal

    private val _mealsByCategory = MutableLiveData<UiState<MealsResponse>>()
    val mealsByCategory: LiveData<UiState<MealsResponse>> get() = _mealsByCategory

    init {
        val mealDao = MealsDatabase.getInstance(application).dao()
        repository = FoodRepository(Repository(mealDao))
        getRandomMeal()
        getAllCategories()
        getMealsByCategory("beef") // default
    }

    fun getAllCategories() {
        viewModelScope.launch {
            _categories.value = UiState.Loading
            try {
                val response = repository.getCategories()
                _categories.value = UiState.Success(response)
            } catch (e: Exception) {
                _categories.value = UiState.Error(e.message ?: "Error fetching categories")
            }
        }
    }

    fun getRandomMeal() {
        viewModelScope.launch {
            _randomMeal.value = UiState.Loading
            try {
                val response = repository.getRandomMeal()
                _randomMeal.value = UiState.Success(response)
            } catch (e: Exception) {
                _randomMeal.value = UiState.Error(e.message ?: "Error fetching random meal")
            }
        }
    }

    fun getMealsByCategory(category: String) {
        viewModelScope.launch {
            _mealsByCategory.value = UiState.Loading
            try {
                val response = repository.getMealsByCategory(category)
                _mealsByCategory.value = UiState.Success(response)
            } catch (e: Exception) {
                _mealsByCategory.value = UiState.Error(e.message ?: "Error fetching meals by category")
            }
        }
    }
}

// ---- DetailsMVVM ----
class DetailsMVVM(application: Application) : AndroidViewModel(application) {

    private val repository: FoodRepository

    private val _mealDetail = MutableLiveData<UiState<List<MealDetail>>>()
    val mealDetail: LiveData<UiState<List<MealDetail>>> get() = _mealDetail

    private val _mealBottomSheet = MutableLiveData<UiState<List<MealDetail>>>()
    val mealBottomSheet: LiveData<UiState<List<MealDetail>>> get() = _mealBottomSheet

    val savedMeals: LiveData<List<MealDB>>

    init {
        val mealDao = MealsDatabase.getInstance(application).dao()
        repository = FoodRepository(Repository(mealDao))
        savedMeals = repository.getAllSavedMeals()
    }

    fun insertMeal(meal: MealDB) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertMeal(meal)
        }
    }

    fun deleteMeal(meal: MealDB) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMeal(meal)
        }
    }

    fun deleteMealById(mealId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMealById(mealId)
        }
    }

    fun getMealById(id: String) {
        viewModelScope.launch {
            _mealDetail.value = UiState.Loading
            try {
                val response = repository.getMealById(id)
                _mealDetail.value = UiState.Success(response.meals)
            } catch (e: Exception) {
                _mealDetail.value = UiState.Error(e.message ?: "Error fetching meal details")
            }
        }
    }

    fun getMealByIdBottomSheet(id: String) {
        viewModelScope.launch {
            _mealBottomSheet.value = UiState.Loading
            try {
                val response = repository.getMealById(id)
                _mealBottomSheet.value = UiState.Success(response.meals)
            } catch (e: Exception) {
                _mealBottomSheet.value = UiState.Error(e.message ?: "Error fetching meal details for bottom sheet")
            }
        }
    }

    suspend fun isMealSavedInDatabase(mealId: String): Boolean {
        return withContext(Dispatchers.IO) {
            repository.getMealFromDbById(mealId) != null
        }
    }
}
