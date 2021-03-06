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
import android.view.animation.Transformation
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
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

    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()

    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality


    // This variable will store the data for current night.
    // We will be using its type as MutableLiveData<>() because we want to OBSERVE and UPDATE it.
    private var tonight = MutableLiveData<SleepNight?>()

    /**
     * Get all nights, so the can be transformed and formatted for TextView
     */
    private val nights = database.getAllNights()

    // We are using formatNights() function from Utils.kt file
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

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
        if (night?.endTimeMilli != night?.startTimeMilli) {
            night = null
        }

        // return final state of the night, as it can be null or actual data
        return night

    }

    // Click handler: Start button
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

    /**
     * Click handler: Stop Button
     */
    fun onStopTracking() {
        viewModelScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)

            _navigateToSleepQuality.value = oldNight
        }
    }

    // Click handler: Cleaar
    fun onClear() {
        viewModelScope.launch {
            clear()
            tonight.value = null

            // Display the snackbar after clearing the database record
            _showSnackbarEvent.value = true
        }
    }

    // INSERT
    // Suspend = To complete this task using coroutine
    private suspend fun insert(night: SleepNight) {

        // We are using DAO interface to insert night into the database
        database.insert(night)

    }

    // UPDATE
    private suspend fun update(night: SleepNight) {

        //
        database.update(night)

    }

    // CLEAR
    private suspend fun clear() {
        //
        database.clear()
    }

    fun doneNavigation() {
        _navigateToSleepQuality.value = null
    }

    /**
     * By using the following snippet we are connecting our following methods with the buttons in the
     * XML file to logically handling buttons states:
     *
     * android:enabled="@{sleepTrackerViewModel.***}"
     *
     * 1. startButtonVisible
     * 2. stopButtonVisible
     * 3. clearButtonVisible
     */

    // Start - It should be enabled when tonight is null
    val startButtonVisible = Transformations.map(tonight) {
        it == null
    }

    // Stop - It should be enabled when tonight is NOT null
    val stopButtonVisible = Transformations.map(tonight) {
        it != null
    }

    // Clear - It should be ONLY enabled when database contains records for sleep nights.
    val clearButtonVisible = Transformations.map(nights) {
        it?.isNotEmpty()
    }

    /**
     * Encapsulated Event for snackbar
     *
     * After the user clears the database, show the user a confirmation using the Snackbar widget.
     * Deciding when to show the snackbar happens in the ViewModel.
     *
     * Following code will trigger the navigation:
     *
     */
    private var _showSnackbarEvent = MutableLiveData<Boolean>()
    val showSnackbarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

}
