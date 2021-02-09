/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 * entities = Database's Table structure (Refer to Step 1 in SleepNight.kt file)
 * version = Whenever we change schema, then we will need to update the version
 * exportSchema = To keep schema version history backups
 */
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {

    // Reference to our DAO file (implementation of CRUD functions)
    // Refer to Step 2 in SleepDatabaseDao.kt file
    abstract val sleepDatabaseDao: SleepDatabaseDao

    // Allows clients to directly access methods (functions) for CRUD actions
    // without "instantiating the class", means When you create an object,
    // then you are creating an instance of a class, therefore "instantiating" a class.
    companion object {

        // @Volatile =
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        //
        fun getInstance(context: Context): SleepDatabase {

            //
            synchronized(this) {
                var instance = INSTANCE

                //
                if (instance == null) {

                    //
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SleepDatabase::class.java,
                            "sleep_history_database"
                    )
                            .fallbackToDestructiveMigration()
                            .build()

                    //
                    INSTANCE = instance
                }

                //
                return instance
            }

        }

    }

}