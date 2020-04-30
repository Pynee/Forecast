package org.duckdns.pynetti.forecast.data

import androidx.room.*
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "ForecastData")
data class ForecastData(
    @PrimaryKey
    val id: Int, var timeStamp: String,
    var name: String,
    var region: String,
    var country: String,
    var data: HashMap<String, LinkedHashMap<String, String>>
) {
    constructor(
        timeStamp: String,
        name: String,
        region: String,
        country: String,
        data: HashMap<String, LinkedHashMap<String, String>>
    ) : this(1, timeStamp, name, region, country, data)
}
