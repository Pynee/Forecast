package org.duckdns.pynetti.forecast.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "Favorites")
class Favorite(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    @TypeConverters(Converters::class)
    val latLng: LatLng
)
