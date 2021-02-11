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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.launch

/**
 * ViewModel for SleepTrackerFragment.
 */

// AndroidViewModel(application) = ViewModel() but more POWERFUL
// It takes the "application" context as a CONSTRUCTOR PARAMETER
// and makes it available as a PROPERTY
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    // This variable will store the data for current night.
    // We will be using its type as MutableLiveData<>() because we want to OBSERVE and UPDATE it.
    private var tonight = MutableLiveData<SleepNight?>()

    init {
        // We want to initialize tonight variable as soon as possible
        initializeTonight()
    }

    private fun initializeTonight() {

        // We will start a coroutine in ViewModelScope
        // Note: We are using lambda expressions (it is a function with any name)
        viewModelScope.launch {

            // We will fetch and store the value for tonight from the database
            tonight.value = getTonightFromDatabase()

        }

    }

    // It will return a nullable SleepNight,
    // if there is nothing then it will return an error because the function has to return something
    private suspend fun getTonightFromDatabase(): SleepNight? {

        // Get the newest night from the database
        var night = database.getTonight()

        // If the start & end times are not same, then it means that the night has been already
        // completed, thus assign the "null" value to the night variable
        if (night?.endTimeMill != night?.startTimeMilli) {
            night = null
        }

        // return final state of the night, as it can be null or actual data
        return night

    }

    // Click handler for the Start button
    //
    // Logic:
    // 1. Create a new SleepNight
    // 2. Insert it into the database
    // 3. Assign it to tonight
    //
    // Connect this function to the Start Button in Fragment file using:
    // android:onClick="@{() -> sleepTrackerViewModel.onStartTracking()}"
    //
    fun onStartTracking() {

        // viewModelScope = Because we need this result to continue and update the UI
        viewModelScope.launch {

            // Create a new SleepNight
            val newNight = SleepNight()

            // Insert it into the database
            insert(newNight)

            // Assign it to tonight
            tonight.value = getTonightFromDatabase()

        }
    }

    // Suspend = To complete this task using coroutine
    private suspend fun insert(night: SleepNight) {

        // We are using DAO interface to insert night into the database
        database.insert(night)

    }

}
