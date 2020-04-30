package org.duckdns.pynetti.forecast.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteDao {

    @Query("SELECT * from Favorites ORDER BY name ASC")
    fun getFavorites(): LiveData<List<Favorite>>

    @Query("SELECT * FROM Favorites WHERE name == :name")
    fun getFavoriteByName(name: String): List<Favorite>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: Favorite)

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()

    @Delete
    fun delete(favorite: Favorite)
}