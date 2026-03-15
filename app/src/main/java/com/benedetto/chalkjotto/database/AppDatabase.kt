package com.benedetto.chalkjotto.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.benedetto.chalkjotto.database.achievement.Achievement
import com.benedetto.chalkjotto.database.achievement.AchievementDao
import com.benedetto.chalkjotto.database.achievement.AchievementId
import com.benedetto.chalkjotto.database.gamerecord.GameRecord
import com.benedetto.chalkjotto.database.gamerecord.GameRecordDao

class Converters {
    @TypeConverter
    fun fromAchievementId(value: AchievementId): String = value.name

    @TypeConverter
    fun toAchievementId(value: String): AchievementId = AchievementId.valueOf(value)
}

@Database(entities = [GameRecord::class, Achievement::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameRecordDao(): GameRecordDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `achievements` (`id` TEXT NOT NULL, `unlockedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chalkjotto_db"
                ).addMigrations(MIGRATION_1_2).build().also { INSTANCE = it }
            }
        }
    }
}
