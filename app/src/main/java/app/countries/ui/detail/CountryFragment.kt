package app.countries.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import app.countries.databinding.FragmentCountryDetailsBinding
import app.countries.util.ViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CountryFragment : Fragment() {

    private lateinit var binding: FragmentCountryDetailsBinding
    private val viewModel by viewModels<CountryViewModel>()
    private val args: CountryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCountryDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.queryCountry(args.countryId)
        observeCountryData()

        binding.retryButton.setOnClickListener {
            viewModel.queryCountry(args.countryId)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeCountryData() {
        viewModel.country.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    showLoading()
                }
                is ViewState.Success -> {
                    (activity as AppCompatActivity?)!!.supportActionBar?.title = response.value?.name
                    binding.countryName.text = "${response.value?.name} (${response.value?.code})"
                    binding.continentName.text = "${response.value?.continent?.name} (${response.value?.continent?.code})"
                    binding.capitalName.text = response.value?.capital
                    binding.currencyName.text = response.value?.currency
                    binding.countryFlag.text = response.value?.emoji
                    binding.phoneName.text = response.value?.phone
                    binding.languagesName.text = response.value?.languages?.map { it.name }?.joinToString(",")
                    showContent()
                }
                is ViewState.Error -> {
                    showError()
                }
            }
        }
    }

    private fun showLoading(){
        binding.loadingCountry.visibility = View.VISIBLE
        binding.countryError.visibility = View.GONE
        binding.countryFlag.visibility = View.GONE
        binding.countryInfo.visibility = View.GONE
    }

    private fun showContent(){
        binding.loadingCountry.visibility = View.GONE
        binding.countryError.visibility = View.GONE
        binding.countryFlag.visibility = View.VISIBLE
        binding.countryInfo.visibility = View.VISIBLE

    }

    private fun showError(){
        binding.loadingCountry.visibility = View.GONE
        binding.countryFlag.visibility = View.GONE
        binding.countryInfo.visibility = View.GONE
        binding.countryError.visibility = View.VISIBLE
    }
}
