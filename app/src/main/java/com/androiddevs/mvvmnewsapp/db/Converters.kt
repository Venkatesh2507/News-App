package com.androiddevs.mvvmnewsapp.db
import androidx.room.TypeConverter
import com.androiddevs.mvvmnewsapp.Source

class Converters {
    @TypeConverter
    fun fromSource(source: com.androiddevs.mvvmnewsapp.Source): String{
        return source.name
    }
    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name,name)
    }

}