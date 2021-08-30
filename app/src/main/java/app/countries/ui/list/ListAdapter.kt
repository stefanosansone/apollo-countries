package app.countries.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.countries.databinding.CountryItemBinding
import app.countries.CountriesListQuery

class ListAdapter(
    private val countries: List<CountriesListQuery.Country>
) : RecyclerView.Adapter<ListAdapter.ViewHolder>(), Filterable {

    var list = ArrayList<CountriesListQuery.Country>()
    var filterContinent: String = ""
    var filterLanguage: String = ""

    init {
        list = countries as ArrayList<CountriesListQuery.Country>
    }

    var onItemClicked: ((CountriesListQuery.Country) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            CountryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = list[position]
        holder.textId.text = country.code
        holder.textName.text = country.name
        holder.flag.text = country.emoji
        holder.itemView.setOnClickListener {
            onItemClicked?.invoke(country)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(binding: CountryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val textId: TextView = binding.countryId
        val textName: TextView = binding.countryName
        val flag: TextView = binding.countryFlag
        override fun toString(): String {
            return super.toString() + " '" + textName.text + "'"
        }
    }

    fun filterList(continent: String, language: String){
        filterContinent = continent
        filterLanguage = language
        filter.filter("filter")
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val resultList = ArrayList<CountriesListQuery.Country>()
                for (row in countries) {
                    val checkContinent = row.continent.name.contains(filterContinent)
                    val checkLanguage = row.languages.find { it.name!!.contains(filterLanguage) } != null
                    val condition = if(filterContinent != "" && filterLanguage == "") {
                        checkContinent
                    }else if(filterContinent == "" && filterLanguage != "") {
                        checkLanguage
                    }else if(filterContinent != "" && filterLanguage != ""){
                        checkContinent && checkLanguage
                    }else{
                        true
                    }
                    if (condition) {
                        resultList.add(row)
                    }
                }
                list = resultList
                val filterResults = FilterResults()
                filterResults.values = list
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                list = results?.values as ArrayList<CountriesListQuery.Country>
                notifyDataSetChanged()
            }
        }
    }

}