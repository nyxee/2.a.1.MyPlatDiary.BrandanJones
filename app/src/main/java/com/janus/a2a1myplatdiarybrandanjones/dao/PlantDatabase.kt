package com.janus.a2a1myplatdiarybrandanjones.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.janus.a2a1myplatdiarybrandanjones.dto.Plant

@Database(entities = [Plant::class], version = 1)
abstract class PlantDatabase: RoomDatabase() {
    abstract fun localPlantDAO(): ILocalPlantDAO
}