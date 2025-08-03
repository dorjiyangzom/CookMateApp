package com.dorjiyangzom.cookmateapp.data.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dorjiyangzom.cookmateapp.data.pojo.MealDB
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class MealsDatabaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: MealsDatabase
    private lateinit var dao: Dao
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        // Use in-memory database for testing
        db = Room.inMemoryDatabaseBuilder(
            context,
            MealsDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = db.dao()
    }

    @Test
    fun `getInstance should return same singleton`() {
        val instance1 = MealsDatabase.getInstance(context)
        val instance2 = MealsDatabase.getInstance(context)

        assertSame(instance1, instance2, "getInstance should return same singleton instance")
    }

    @Test
    fun `insert and read meal from database`() = runBlocking {
        val meal = MealDB(
            idMeal = "1234",
            strMeal = "Beef Stew",
            strMealThumb = "https://example.com/image.jpg"
        )

        dao.insertMeal(meal)

        val savedMeals = dao.getAllMeals()
        assertNotNull(savedMeals)
        assertEquals(1, savedMeals.size)
        assertEquals("Beef Stew", savedMeals[0].strMeal)
    }

    @After
    fun tearDown() {
        db.close()
    }
}
