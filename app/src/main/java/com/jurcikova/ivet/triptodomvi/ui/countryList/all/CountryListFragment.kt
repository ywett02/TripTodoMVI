package com.jurcikova.ivet.triptodomvi.ui.countryList.all

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jurcikova.ivet.triptodomvi.R
import com.jurcikova.ivet.triptodomvi.common.BindFragment
import com.jurcikova.ivet.triptodomvi.databinding.FragmentCountryListBinding
import com.jurcikova.ivet.triptodomvi.mvibase.MviIntent
import com.jurcikova.ivet.triptodomvi.mvibase.MviView
import com.jurcikova.ivet.triptodomvi.ui.countryList.CountryAdapter
import com.strv.ktools.inject
import com.strv.ktools.logD
import io.reactivex.Observable

class CountryListFragment : Fragment(), MviView<CountryListIntent, CountryListViewState> {

    private val viewModel: CountryListViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(CountryListViewModel::class.java)
    }

    //delegate the binding initialization to BindFragment delegate
    private val binding: FragmentCountryListBinding by BindFragment(R.layout.fragment_country_list)

    private val adapter by inject<CountryAdapter>()

    private val initialIntent by lazy {
        Observable.just(CountryListIntent.InitialIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.states().observe(this, Observer { state ->
            logD("state: $state")

            render(state!!)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = binding.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupListView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startStream()
    }

    override fun render(state: CountryListViewState) {
        binding.model = state

        if (state.error != null) {
            showErrorState(state.error)
        }
    }

    override fun intents() = initialIntent as Observable<CountryListIntent>

    /**
     *  Start the stream by passing [MviIntent] to [MviViewModel]
     */
    private fun startStream() {
        // Pass the UI's intents to the ViewModel
        viewModel.processIntents(intents())
    }

    private fun setupListView() {
        binding.rvCountries.layoutManager = LinearLayoutManager(activity)
        binding.rvCountries.adapter = adapter
    }

    private fun showErrorState(exception: Throwable) {
        activity?.let {
            Toast.makeText(it, "Error during fetching from api ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
