package jatx.mydict.data.db

import android.content.Context
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import jatx.mydict.data.db.dao.WordDao
import jatx.mydict.data.db.entity.WordEntity

@Database(
    entities = [
        WordEntity::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): AppDatabase = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                Log.e("db", "building")
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room
            .databaseBuilder(
                context,
                AppDatabase::class.java, "words.db"
            )
            .allowMainThreadQueries()
            .build()
    }
}