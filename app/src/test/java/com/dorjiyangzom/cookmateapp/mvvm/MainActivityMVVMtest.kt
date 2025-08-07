package com.dorjiyangzom.cookmateapp.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MainFragMVVMTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun viewModel_initializesLiveData() {
        val viewModel = MainFragMVVM()

        assertNotNull("Random meal LiveData should not be null", viewModel.observeRandomMeal())
        assertNotNull("Category LiveData should not be null", viewModel.observeCategories())
        assertNotNull("Meals by category LiveData should not be null", viewModel.observeMealByCategory())
    }
}
