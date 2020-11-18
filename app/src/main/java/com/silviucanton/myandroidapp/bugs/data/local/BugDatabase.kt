package com.silviucanton.myandroidapp.bugs.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.silviucanton.myandroidapp.bugs.data.Bug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@Database(entities = [Bug::class], version = 1)
abstract class BugDatabase : RoomDatabase() {

    abstract fun bugDao(): BugDao

    companion object {
        @Volatile
        private var INSTANCE: BugDatabase? = null

        //        @kotlinx.coroutines.InternalCoroutinesApi()
        fun getDatabase(context: Context, scope: CoroutineScope): BugDatabase {
            val inst = INSTANCE
            if (inst != null) {
                return inst
            }
            val instance =
                Room.databaseBuilder(
                    context.applicationContext,
                    BugDatabase::class.java,
                    "bug_db"
                )
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
            INSTANCE = instance
            return instance
        }

        private class WordDatabaseCallback(private val scope: CoroutineScope) :
            RoomDatabase.Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        print("Database initialized...")
                    }
                }
            }
        }
    }

}