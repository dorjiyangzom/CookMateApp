package com.dorjiyangzom.cookmateapp.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.dorjiyangzom.cookmateapp.data.db.Repository
import com.dorjiyangzom.cookmateapp.data.pojo.MealDB
import com.dorjiyangzom.cookmateapp.data.pojo.MealDetail
import com.dorjiyangzom.cookmateapp.data.pojo.RandomMealResponse
import com.dorjiyangzom.cookmateapp.data.retrofit.FoodApi
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.*
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.Application
import org.junit.runner.RunWith

@RunWith(MockitoJUnitRunner::class)
class DetailsMVVMTest {

    @get:Rule
    val instantRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository: Repository

    @Mock
    lateinit var foodApi: FoodApi

    @Mock
    lateinit var call: Call<RandomMealResponse>

    @Mock
    lateinit var observer: Observer<List<MealDetail>>

    @Mock
    lateinit var application: Application

    lateinit var viewModel: DetailsMVVM

    @Before
    fun setup() {
        // Replace static RetrofitInstance reference
        com.dorjiyangzom.cookmateapp.data.retrofit.RetrofitInstance.foodApi = foodApi

        // Use a partial mock for DetailsMVVM to inject mock repository
        viewModel = Mockito.spy(DetailsMVVM(application))
        // Replace repository field with our mock
        val repoField = DetailsMVVM::class.java.getDeclaredField("repository")
        repoField.isAccessible = true
        repoField.set(viewModel, repository)
    }

    @Test
    fun `getMealById updates LiveData on success`() {
        val mealDetails = listOf(MealDetail("1", "Pizza", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""))
        val response = RandomMealResponse(mealDetails)

        Mockito.`when`(foodApi.getMealById("1")).thenReturn(call)
        Mockito.doAnswer {
            val callback: Callback<RandomMealResponse> = it.getArgument(0)
            callback.onResponse(call, Response.success(response))
            null
        }.`when`(call).enqueue(Mockito.any())

        viewModel.observeMealDetail().observeForever(observer)

        viewModel.getMealById("1")

        Mockito.verify(observer).onChanged(mealDetails)
    }

    @Test
    fun `isMealSavedInDatabase returns true when meal exists`() = runBlocking {
        val meal = MealDB("1", "Pizza", "desc", "img.jpg")
        Mockito.`when`(repository.getMealById("1")).thenReturn(meal)

        val result = viewModel.isMealSavedInDatabase("1")

        Assert.assertTrue(result)
    }

    @Test
    fun `isMealSavedInDatabase returns false when meal does not exist`() = runBlocking {
        Mockito.`when`(repository.getMealById("1")).thenReturn(null)

        val result = viewModel.isMealSavedInDatabase("1")

        Assert.assertFalse(result)
    }
}
