package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AppSettingsDao
import com.example.data.local.dao.CoinTransactionDao
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.SensitivityDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entities.AppSettingsEntity
import com.example.data.local.entities.CoinTransactionEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.SensitivityPresetEntity
import com.example.data.local.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        OrderEntity::class,
        CoinTransactionEntity::class,
        AppSettingsEntity::class,
        SensitivityPresetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun coinTransactionDao(): CoinTransactionDao
    abstract fun settingsDao(): AppSettingsDao
    abstract fun sensitivityDao(): SensitivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ff_zone_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
