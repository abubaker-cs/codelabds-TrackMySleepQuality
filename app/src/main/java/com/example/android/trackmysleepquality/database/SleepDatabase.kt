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

        //
        // @Volatile:
        // Its value will never be cached, and all R/W tasks will be done from the main memory (RAM).
        // This helps in avoiding opening multiple connections to database, thus improving app's performance
        //
        // INSTANCE:
        // It means that to avoid any conflict, changes being made by "a thread" will be
        // immediately visible to "other threads". So, only one thread can apply updates at a time.
        //
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        // Required by "Database Builder"
        fun getInstance(context: Context): SleepDatabase {

            // Allows only "one thread" to access this block at time, its purpose is same as
            // mentioned before, that is to avoid any conflict between multiple threads
            synchronized(this) {

                // We are here taking advantage of Kotlin's "smart cast" feature
                // by copying the current value of INSTANCE to the LOCAL variable.
                var instance = INSTANCE

                // If no database with this name exists
                if (instance == null) {

                    // Create the database by using:
                    // 1. Context
                    // 2. Our DATABASE class (it has references to Table Structure & DAO/CRUD Operations)
                    // 3. Desired NAME of the DATABASE
                    //
                    // Destroy & Rebuild data in the Table (MIGRATION STRATEGY)
                    // To avoid type-mismatch error, we are using fallbackToDestructiveMigration
                    // as our "Migration Strategy" to destroy and rebuild the database.
                    //
                    // Normally, we have to provide a "MIGRATION OBJECT", which will define how
                    // all existing rows with the old schema should be converted to the
                    // new rows based on the updated schema.
                    //
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SleepDatabase::class.java,
                            "sleep_history_database"
                    )
                            .fallbackToDestructiveMigration()
                            .build()

                    // Assign our LOCAL instance to the actual INSTANCE
                    INSTANCE = instance
                }

                // Final step
                return instance
            }

        }

    }

}