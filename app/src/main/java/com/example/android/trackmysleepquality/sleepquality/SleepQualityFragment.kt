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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepQualityBinding

/**
 * Fragment that displays a list of clickable icons,
 * each representing a sleep quality rating.
 * Once the user taps an icon, the quality is set in the current sleepNight
 * and the database is updated.
 */
class SleepQualityFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepQualityBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_quality, container, false)

        val application = requireNotNull(this.activity).application

        // We need to retrieve arguments sent from SleepTrackerFragment using SafeArgs to
        // SleepQualityFragment using SleepQualityFragmentArgs and extracting from the Bundle:
        val arguments = SleepQualityFragmentArgs.fromBundle(requireArguments())

        // Reference to our DAO file (implementation of CRUD functions)
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        // Build Factory based on:
        // 1. Unique Key (sleepNightKey)
        // 2. Reference to DAO File for database
        val viewModelFactory = SleepQualityViewModelFactory(arguments.sleepNightKey, dataSource)

        // Retrieves a selected entry based on the provided Primary Key
        // Refer to: get() method in SleepDatabaseDao.kt file
        val sleepQualityViewModel = ViewModelProvider(this, viewModelFactory)
                .get(SleepQualityViewModel::class.java)

        // We will add the ViewModel to the binding object
        binding.sleepQualityViewModel = sleepQualityViewModel

        /**
         * Observer: navigateToSleepTracker
         * The purpose of this observer is to collect and  store user's provided rating for the "sleep quality"
         *
         * After completing this function:
         *
         * 1. Populate <variable> data in the fragment_sleep_quality.xml for
         *    connecting buttons onClick events with the Observer
         *
         * 2. Add following code in ImageViews which will act as icons for ranking sleep quality
         *    android:onClick="@{() -> sleepQualityViewModel.onSetSleepQuality(0)}"
         *
         *    Note: .onSetSleepQuality(***) will have values from:
         *    0 Very Bad
         *    1 Poor
         *    2 So so
         *    3 OK
         *    4 Pretty Good
         *    5 Excellent
         *
         */
        sleepQualityViewModel.navigateToSleepTracker.observe(viewLifecycleOwner, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(
                        SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment())
                sleepQualityViewModel.doneNavigating()
            }
        })

        return binding.root
    }

}
