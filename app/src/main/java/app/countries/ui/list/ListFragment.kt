package app.countries.ui.list

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import app.countries.R
import app.countries.databinding.FragmentCountryListBinding
import app.countries.util.ViewState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ListFragment : Fragment() {
    private lateinit var binding: FragmentCountryListBinding
    private val listAdapter by lazy { ListAdapter(mutableListOf()) }
    private val viewModel by viewModels<ListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Countries"
        binding = FragmentCountryListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCountries.adapter = listAdapter
        binding.rvCountries.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))

        viewModel.queryCountriesList()
        observeCountriesData()

        listAdapter.onItemClicked = { country ->
            findNavController().navigate(
                ListFragmentDirections.openCountryDetails(countryId = country.code)
            )
        }

        binding.filtersGroup.clearButton.setOnClickListener {
            binding.filtersGroup.continentList.text.clear()
            binding.filtersGroup.languageList.text.clear()
            filterAdapter()
        }

        binding.retryButton.setOnClickListener {
            viewModel.queryCountriesList()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.search -> {
            if(binding.filtersGroup.filtersLayout.visibility == View.VISIBLE){
                binding.filtersGroup.filtersLayout.visibility = View.GONE
            }else{
                binding.filtersGroup.filtersLayout.visibility = View.VISIBLE
            }
            true
        }
        else -> { super.onOptionsItemSelected(item) }
    }

    private fun filterAdapter(){
        listAdapter.filterList(binding.filtersGroup.continentList.text.toString(),
            binding.filtersGroup.languageList.text.toString())
    }

    private fun observeCountriesData() {
        //Non chiamo il repository dal fragment ma osservo i dati messi a disposizione dal viewModel
        // e gestisco lo stato della richiesta con una classe custom
        viewModel.countriesList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Loading -> {
                    binding.rvCountries.visibility = View.GONE
                    binding.listError.visibility = View.GONE
                    binding.loadingList.visibility = View.VISIBLE
                }
                is ViewState.Success -> {
                    val results = response.value

                    // Elenco di lingue e continenti dai paesi letti con la chiamata
                    val list = mutableListOf<String>()
                    results?.forEach { list.addAll(it.languages.map { it.name!! })}
                    setContinentsFilter(results!!.map { it.continent.name }.distinct().sorted())
                    setLanguagesFilter(list.distinct().sorted() as MutableList<String>)

                    listAdapter.list.addAll(results)

                    binding.loadingList.visibility = View.GONE
                    binding.listError.visibility = View.GONE
                    binding.rvCountries.visibility = View.VISIBLE
                    filterAdapter()
                }
                is ViewState.Error -> {
                    binding.loadingList.visibility = View.GONE
                    binding.rvCountries.visibility = View.GONE
                    binding.listError.visibility = View.VISIBLE
                }
            }
        }
    }

    //Assegno le liste ai menù a tendina nel riquadro dei filtri
    private fun setContinentsFilter(continentsList: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, continentsList)
        binding.filtersGroup.continentList.setAdapter(adapter)
        binding.filtersGroup.continentList.setOnItemClickListener { _, _, _, _ ->
            filterAdapter()
        }
    }

    private fun setLanguagesFilter(languagesList: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, languagesList)
        binding.filtersGroup.languageList.setAdapter(adapter)
        binding.filtersGroup.languageList.setOnItemClickListener { _, _, _, _ ->
            filterAdapter()
        }
    }
}