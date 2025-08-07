package com.dorjiyangzom.cookmateapp.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CategoryMVVMTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun viewModel_initializesLiveData() {
        val viewModel = CategoryMVVM()
        val liveData = viewModel.observeCategories()
        assertNotNull("LiveData should not be null", liveData)
    }
}
