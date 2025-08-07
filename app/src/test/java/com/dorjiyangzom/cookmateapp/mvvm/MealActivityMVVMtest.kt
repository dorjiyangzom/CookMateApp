package com.dorjiyangzom.cookmateapp.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MealActivityMVVMTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun viewModel_initializesLiveData() {
        val viewModel = MealActivityMVVM()
        val liveData = viewModel.observeMeal()

        assertNotNull("LiveData for meals should not be null", liveData)
    }
}
