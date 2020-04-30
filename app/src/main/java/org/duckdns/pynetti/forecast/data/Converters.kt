package org.duckdns.pynetti.forecast.data

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    @TypeConverter
    fun toLatLng(values: String): LatLng? {
        return Gson().fromJson(values, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun toJson(latLng: LatLng?): String? {
        return Gson().toJson(listOf(latLng?.latitude, latLng?.longitude))
    }

    @TypeConverter
    fun toHashMap(values: String): HashMap<String, LinkedHashMap<String, String>> {
        return Gson().fromJson(
            values,
            object : TypeToken<HashMap<String, LinkedHashMap<String, String>>>() {}.type
        )
    }

    @TypeConverter
    fun toJson(data: HashMap<String, LinkedHashMap<String, String>>): String? {
        return Gson().toJson(data)
    }


}