package org.duckdns.pynetti.forecast.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val favoritesDao: FavoriteDao = AppDatabase.getDatabase(application).favoriteDao()
    private val forecastDataDao: ForecastDataDao =
        AppDatabase.getDatabase(application).forecastDataDao()
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allFavorites: LiveData<List<Favorite>>
    val forecastData: LiveData<ForecastData>

    init {
        allFavorites = favoritesDao.getFavorites()
        forecastData = forecastDataDao.getForecastData()
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(favorite: Favorite) = viewModelScope.launch(Dispatchers.IO) {
        favoritesDao.insert(favorite)
    }

    fun insert(forecastData: ForecastData) = viewModelScope.launch(Dispatchers.IO) {
        forecastDataDao.insert(forecastData)
    }

    fun update(forecastData: ForecastData, liveData: LiveData<ForecastData> = this.forecastData) =
        viewModelScope.launch(Dispatchers.IO) {
            if (liveData.value != null) {
                forecastDataDao.update(forecastData)
            } else {
                forecastDataDao.insert(forecastData)
            }
        }


}