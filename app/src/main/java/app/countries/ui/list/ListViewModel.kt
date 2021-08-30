package app.countries.ui.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.exception.ApolloException
import app.countries.CountriesListQuery
import app.countries.data.DataRepository
import app.countries.util.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: DataRepository,
) : ViewModel() {

    private val _countriesList by lazy { MutableLiveData<ViewState<List<CountriesListQuery.Country>>>() }
    val countriesList: LiveData<ViewState<List<CountriesListQuery.Country>>>
        get() = _countriesList

    fun queryCountriesList() = viewModelScope.launch {
        _countriesList.postValue(ViewState.Loading())
        try {
            val response = repository.getCountriesList()
            _countriesList.postValue(ViewState.Success(response))
        } catch (e: ApolloException) {
            Log.d("ApolloException", "Failure", e)
            _countriesList.postValue(ViewState.Error("Error fetching countries"))
        }
    }
}