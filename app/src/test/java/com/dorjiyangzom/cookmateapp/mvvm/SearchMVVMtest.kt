package com.dorjiyangzom.cookmateapp.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class SearchMVVMTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun viewModel_initializesCorrectly() {
        val viewModel = SearchMVVM()

        val liveData = viewModel.observeSearchLiveData()
        assertNotNull("LiveData should not be null", liveData)
    }
}
