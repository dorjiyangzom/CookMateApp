package com.dorjiyangzom.cookmateapp.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.dorjiyangzom.cookmateapp.data.pojo.Category
import com.dorjiyangzom.cookmateapp.data.pojo.CategoryResponse
import com.dorjiyangzom.cookmateapp.data.retrofit.RetrofitInstance
import com.dorjiyangzom.cookmateapp.data.retrofit.FoodApi
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class CategoryMVVMTest {

    // Runs LiveData instantly
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var mockApi: FoodApi

    @Mock
    lateinit var mockCall: Call<CategoryResponse>

    @Mock
    lateinit var observer: Observer<List<Category>>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // Swap out real Retrofit API with our mock
        RetrofitInstance.foodApi = mockApi
    }

    @Test
    fun `getCategories should post values on success`() {
        val categoryList = listOf(Category("1", "Beef", "desc", "thumb.jpg"))
        val response = CategoryResponse(categoryList)

        // Mock API call behavior
        Mockito.`when`(mockApi.getCategories()).thenReturn(mockCall)
        Mockito.doAnswer { invocation ->
            val callback: Callback<CategoryResponse> = invocation.getArgument(0)
            callback.onResponse(mockCall, Response.success(response))
            null
        }.`when`(mockCall).enqueue(Mockito.any())

        val viewModel = CategoryMVVM()
        viewModel.observeCategories().observeForever(observer)

        Mockito.verify(observer).onChanged(categoryList)
    }

    @Test
    fun `getCategories should handle failure`() {
        Mockito.`when`(mockApi.getCategories()).thenReturn(mockCall)
        Mockito.doAnswer { invocation ->
            val callback: Callback<CategoryResponse> = invocation.getArgument(0)
            callback.onFailure(mockCall, Throwable("Network error"))
            null
        }.`when`(mockCall).enqueue(Mockito.any())

        val viewModel = CategoryMVVM()
        viewModel.observeCategories().observeForever(observer)

        // In failure case, LiveData should remain null or empty
        Mockito.verify(observer, Mockito.never()).onChanged(Mockito.anyList())
    }
}
