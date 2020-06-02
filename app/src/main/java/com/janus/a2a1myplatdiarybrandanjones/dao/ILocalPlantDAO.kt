package com.janus.a2a1myplatdiarybrandanjones.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.janus.a2a1myplatdiarybrandanjones.dto.Plant
import kotlinx.coroutines.flow.Flow


@Dao
interface ILocalPlantDAO {

    @Query("SELECT * FROM plant")
    fun getAllPlants(): LiveData<List<Plant>>

    //OnConflict: what we do when we try to insert an entity with the same primary key as an existing one.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(plants: ArrayList<Plant>)

    @Delete
    fun delete(plant: Plant): Int
}

    //JUST SOME EXAMPLES OF CALLS..
//    @Query("SELECT * FROM Work")
//    fun getFavorites(): DataSource.Factory<Int, Work>
//
//    @Query("SELECT * FROM Work WHERE id = :id ")
//    fun getFavorite(id: String): LiveData<Work>
//
//    @Query("SELECT count(*) FROM Work")
//    fun getFavoriteCount(): LiveData<Int>
//
//    @Insert(onConflict = REPLACE)
//    fun addFavorite(work: Work)
//
//    @Delete
//    fun removeFavorite(work: Work)
//    @Query("SELECT * FROM Cheese ORDER BY name COLLATE NOCASE ASC")
//    fun allCheesesByName(): DataSource.Factory<Int, Cheese>
//    @Query("SELECT * FROM todos")

//    fun all(): Flow<List<ToDoEntity>>
//
//@Query("SELECT * FROM todos WHERE id = :modelId")
//fun find(modelId: String): Flow<ToDoEntity?>
//
//@Insert(onConflict = OnConflictStrategy.REPLACE)
//suspend fun save(vararg entities: ToDoEntity)
//
//@Delete
//suspend fun delete(vararg entities: ToDoEntity)

//@Delete
//fun delete(vararg notes: Note): Int //returns number of rows deletedciNHn38EyRc
//
//@Update
//fun update(vararg notes: Note): Int //returns number of rows inserted


