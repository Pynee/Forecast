package org.duckdns.pynetti.forecast.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ForecastDataDao {

    @Query("SELECT * from forecastdata")
    fun getForecastData(): LiveData<ForecastData>

    //@Query("SELECT data FROM forecastdata")
    //fun getForecastData(name: String): HashMap<String,LinkedHashMap<String,String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(forecastData: ForecastData)

    @Update
    suspend fun update(forecastData: ForecastData)

    @Query("DELETE FROM Forecastdata")
    suspend fun deleteAll()

    @Delete
    fun delete(forecastData: ForecastData)
}