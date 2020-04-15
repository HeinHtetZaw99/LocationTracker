package com.locationtracker.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.appbase.configure
import com.appbase.fragments.BaseFragment
import com.locationtracker.R
import com.locationtracker.activities.MainActivity

import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_location_history_list.view.*

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [LocationHistoryFragment.OnListFragmentInteractionListener] interface.
 */
class LocationHistoryFragment() : BaseFragment() {

    override var fragmentLayout: Int = R.layout.fragment_location_history_list

    private lateinit var adapter: MyLocationHistoryRecyclerViewAdapter
    override val viewModel: MainViewModel by lazy { parentActivity.getHomeViewModel() }
    private val parentActivity: MainActivity by lazy { activity as MainActivity }

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    override fun onError() {

    }

    override fun loadData() {

    }

    override fun initViews(view: View) {
        adapter = MyLocationHistoryRecyclerViewAdapter(listener)

        view.historyRv.configure(context!!, adapter)
        viewModel.locationListLD.observe(viewLifecycleOwner, Observer {
            adapter.appendNewData(it)
        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getLocationHistory()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(adapterPosition: Int)
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            LocationHistoryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
