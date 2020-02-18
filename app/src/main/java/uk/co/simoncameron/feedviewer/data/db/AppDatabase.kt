package uk.co.simoncameron.feedviewer.data.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import uk.co.simoncameron.feedviewer.data.user.UserDao
import java.util.concurrent.Executors

@Database(entities = arrayOf(UserEntity::class), version = 1)
@TypeConverters(ConvertersDb::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "db")
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        Executors.newSingleThreadExecutor().execute {
                            getInstance(context).userDao().insertAll(*PREPOPULATE_DATA.toTypedArray())
                        }
                    }
                })
                .build()

        val PREPOPULATE_DATA = listOf(
            UserEntity("premium", "password", UserRole.ROLE_PREMIUM),
            UserEntity("user", "password", UserRole.ROLE_NORMAL),
            UserEntity("user1", "password", UserRole.ROLE_NORMAL)
        )
    }
}

class ConvertersDb {
    @TypeConverter
    fun userRoleToInt(value: UserRole) = value.dbCode
    @TypeConverter
    fun intToUserRole(value: Int) =
        UserRole.values().first { it.dbCode == value }
}