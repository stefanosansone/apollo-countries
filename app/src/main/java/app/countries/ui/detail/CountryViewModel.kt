package app.countries.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.exception.ApolloException
import app.countries.CountryDetailQuery
import app.countries.data.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import app.countries.util.ViewState
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@HiltViewModel
class CountryViewModel @Inject constructor(
    private val repository: DataRepository,
) : ViewModel() {

    private val _country by lazy { MutableLiveData<ViewState<CountryDetailQuery.Country>>() }
    val country: LiveData<ViewState<CountryDetailQuery.Country>>
        get() = _country

    fun queryCountry(id: String) = viewModelScope.launch {
        _country.postValue(ViewState.Loading())
        try {
            val response = repository.getCountryDetail(id)
            _country.postValue(ViewState.Success(response))
        } catch (ae: ApolloException) {
            Log.d("ApolloException", "Failure", ae)
            _country.postValue(ViewState.Error("Error fetching country"))
        }
    }

}