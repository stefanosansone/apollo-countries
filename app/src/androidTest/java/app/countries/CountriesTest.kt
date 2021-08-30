/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.countries

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import app.countries.data.DataRepository
import app.countries.ui.detail.CountryFragment
import app.countries.ui.detail.CountryFragmentArgs
import app.countries.util.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class StatisticsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: DataRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun displayCountries() {
        //Verifica del caricamento dei paesi nella lista
        launchActivity()
        runBlocking {
            repository.getCountriesList()
        }
        onView(withText("Andorra")).check(matches(isDisplayed()))
    }

    @Test
    fun filterCountries() {
        //Filtro sui paesi. ATTENZIONE : Disabilitare le animazioni sul dispositivo !!
        launchActivity()
        runBlocking {
            repository.getCountriesList()
        }
        onView(withId(R.id.search)).perform(ViewActions.click())
        onView(withId(R.id.continent_filter)).perform(ViewActions.click())
        onView(withText("Antarctica")).inRoot(RootMatchers.isPlatformPopup())
            .perform(ViewActions.click())
        onView(withText("Bouvet Island")).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToCountryFragment() {
        //Test di click su un paese e visualizzazione del dettaglio
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val bundle = CountryFragmentArgs("AD").toBundle()
        launchFragmentInHiltContainer<CountryFragment>(bundle, R.style.Theme_MaterialComponents_DayNight_NoActionBar) {
            navController.setGraph(R.navigation.main_nav)
            navController.setCurrentDestination(R.id.countryList)
            Navigation.setViewNavController(requireView(), navController)
        }

        launchActivity()
        runBlocking {
            repository.getCountriesList()
        }

        onView(withText("Andorra")).perform(ViewActions.click())
        onView(withId(R.id.languages_name)).check(matches(isDisplayed()))
    }

}

private fun launchActivity(): ActivityScenario<MainActivity>? {
    val activityScenario = launch(MainActivity::class.java)
    activityScenario.onActivity { activity ->
        (activity.findViewById(R.id.rv_countries) as RecyclerView).itemAnimator = null
    }
    return activityScenario
}
