package com.janus.a2a1myplatdiarybrandanjones

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.janus.a2a1myplatdiarybrandanjones.dto.Plant
import com.janus.a2a1myplatdiarybrandanjones.service.PlantService
import com.janus.a2a1myplatdiarybrandanjones.ui.main.MainViewModel
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class PlantDataIntegrationTest {

    private lateinit var mvm: MainViewModel

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @Test
    fun confirmEasternRedbud_outputsEasternRedbud(){
        val plant = Plant("Cersis", "canadesis", "Eastern Redbud")
        Assert.assertEquals("Eastern Redbud", plant.toString())
    }

    @Test
    fun searchForEasternRedbud_returnEasternRedbud(){
        givenAFeedOfPlantDataAreAvailable()
        whenSearchForRedbud()
        thenResultContainsRedbud()
    }

    private fun givenAFeedOfPlantDataAreAvailable() {
        mvm = MainViewModel()
    }

    private fun whenSearchForRedbud() {
        mvm.fetchPlants("Redbud")
    }


    private fun thenResultContainsRedbud() {
        var redbudFound = false

        mvm.plants.observeForever{
            System.out.println("???? 3 ")

            Assert.assertNotNull(it)
            Assert.assertTrue(it.size > 0)
            it.forEach{
                System.out.println("$it")
                if(it.genus == "Cersis" && it.species == "canadesis" && it.common == "Eastern Redbud")
                    redbudFound = true
            }
            Assert.assertTrue(redbudFound)
        }
    }

    @Test
    fun searchForGarbage_returnNothing(){
        givenAFeedOfPlantDataAreAvailable()
        whenSearchForGarbage()
        thenGetZeroResults()
    }



    private fun whenSearchForGarbage() {
        mvm.fetchPlants("fedcdsdefefvfefr")

    }

    private fun thenGetZeroResults() {
        mvm.plants.observeForever{
            Assert.assertEquals(0, it.size)
        }
    }

}