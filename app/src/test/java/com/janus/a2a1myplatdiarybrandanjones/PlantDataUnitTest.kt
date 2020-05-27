package com.janus.a2a1myplatdiarybrandanjones

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.janus.a2a1myplatdiarybrandanjones.dto.Plant
import com.janus.a2a1myplatdiarybrandanjones.service.PlantService
import com.janus.a2a1myplatdiarybrandanjones.ui.main.MainViewModel
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PlantDataUnitTest {

    private lateinit var mvm: MainViewModel

    @get:Rule
    var rule = InstantTaskExecutorRule()

    var plantService = mockk<PlantService>()

    @Test
    fun confirmEasternRedbud_outputsEasternRedbud(){
        val plant = Plant("Cersis", "canadesis", "Eastern Redbud")
        assertEquals("Eastern Redbud", plant.toString())
    }

    @Test
    fun searchForEasternRedbud_returnEasternRedbud(){
        givenAFeedOfMockedPlantDataAreAvailable()
        whenSearchForRedbud()
        thenResultContainsRedbud()
    }

    private fun givenAFeedOfMockedPlantDataAreAvailable() {
        mvm = MainViewModel()
        createMockData()
    }

    private fun createMockData() {
        val allPlantsLiveData = MutableLiveData<ArrayList<Plant>>()
        val allPlants = ArrayList<Plant>()

        val redbud = Plant("Cersis", "canadesis", "Eastern Redbud")
        val redOak = Plant("Quercus", "rubra", "Red Oak")
        val whiteOak = Plant("Quercus", "alba", "White Oak")
        allPlants.add(redbud)
        allPlants.add(redOak )
        allPlants.add(whiteOak)

        allPlantsLiveData.postValue(allPlants)

        //every { plantService.fetchPlants(any()) } returns allPlantsLiveData //This gives results for any input.
        every { plantService.fetchPlants(or("Redbud", "Quercus")) } returns allPlantsLiveData //This gives all results for Redbud or Quercus
        every { plantService.fetchPlants(not(or("Redbud", "Quercus"))) } returns MutableLiveData<ArrayList<Plant>>() //This gives aa new list if input is not dbud or Quercus

        mvm.plantService = plantService
    }

    private fun whenSearchForRedbud() {
        mvm.fetchPlants("Redbud")
    }


    private fun thenResultContainsRedbud() {
        var redbudFound = false

        mvm.plants.observeForever{
            assertNotNull(it)
            assertTrue(it.size > 0)
            it.forEach{
                if(it.genus == "Cersis" && it.species == "canadesis" && it.common == "Eastern Redbud")
                    redbudFound = true
            }
        }
        assertTrue(redbudFound)
    }

    @Test
    fun searchForGarbage_returnNothing(){
        givenAFeedOfMockedPlantDataAreAvailable()
        whenSearchForGarbage()
        thenGetZeroResults()
    }

    private fun whenSearchForGarbage() {
        mvm.fetchPlants("fedcdsdefefvfefr")

    }

    private fun thenGetZeroResults() {
        mvm.plants.observeForever{
            assertEquals(0, it.size)
        }
    }

}
