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

package com.example.android.trackmysleepquality.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import kotlinx.coroutines.launch

/**
 * We record the sleep quality and navigate back to the sleep tracker fragment.
 */
class SleepQualityViewModel(

        // This class takes a sleepNightKey and database as arguments
        // We need to pass in the database from the factory
        private val sleepNightKey: Long = 0L,
        val database: SleepDatabaseDao) : ViewModel() {

    //
    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()

    //
    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker

    //
    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    /**
     * Click Handler: onSetSleepQuality()
     */
    fun onSetSleepQuality(quality: Int) {

        // Launch a coroutine in the viewModelScope
        viewModelScope.launch {

            // Get "tonight" using the "sleepNightKey"
            val tonight = database.get(sleepNightKey) ?: return@launch

            // Set the sleep quality
            tonight.sleepQuality = quality

            // Update the database
            database.update(tonight)

            // Setting this state variable to true will alert the observer and trigger navigation.
            _navigateToSleepTracker.value = true
        }
    }
}
