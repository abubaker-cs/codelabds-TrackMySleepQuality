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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import java.lang.IllegalArgumentException

/**
 * This class uses a version of the same boilerplate code we have seen before.
 *
 * Provides the SleepDatabaseDao and context to the ViewModel.
 */
class SleepQualityViewModelFactory(

        // It takes the SAME arguments as ViewModel but extend as ViewModelProvider.Factory
        // 1. sleepNightKey
        // 2. dataSource
        private val sleepNightKey: Long,
        private val dataSource: SleepDatabaseDao) : ViewModelProvider.Factory {

    // override create(): It takes any class type as an argument and returns as ViewModel
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        // It checks the existence of our class() and returns an INSTANCE of it
        if (modelClass.isAssignableFrom(SleepQualityViewModel::class.java)) {
            // 1. sleepNightKey
            // 2. dataSource
            return SleepQualityViewModel(sleepNightKey, dataSource) as T
        }

        // Otherwise it throws an error
        throw  IllegalArgumentException("Unknown ViewModel Class")

    }
}