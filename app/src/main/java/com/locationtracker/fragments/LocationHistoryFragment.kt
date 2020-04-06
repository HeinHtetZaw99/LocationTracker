package com.locationtracker.fragments

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appbase.fragments.BaseFragment
import com.appbase.models.vos.ReturnResult
import com.appbase.showLogE
import com.appbase.writeFileToDisk
import com.locationtracker.R
import com.locationtracker.activities.MainActivity

import com.locationtracker.fragments.dummy.DummyContent
import com.locationtracker.fragments.dummy.DummyContent.DummyItem
import com.locationtracker.repository.LocationRepositoryImpl
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.viewmodels.MainViewModel
import com.pv.viewmodels.BaseViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.reactivestreams.Subscriber

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [LocationHistoryFragment.OnListFragmentInteractionListener] interface.
 */
class LocationHistoryFragment() : BaseFragment() {

    override var fragmentLayout: Int = R.layout.fragment_location_history

    override val viewModel: MainViewModel by lazy { parentActivity.getHomeViewModel() }
    private val parentActivity: MainActivity by lazy { activity as MainActivity }

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null


    override fun onNetworkError() {

    }

    override fun loadData() {

    }

    override fun initViews(view: View) {


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onResume() {
        super.onResume()
        val allLoc = viewModel.getAllLocationData()
        Log.e("<<LOC>>", "SHOWING ALL LOC")
        val res = allLoc.subscribe { a -> Log.e("<<LOC>>", a.toString()) }
        Log.e("<<LOC>>", res.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_location_history_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyLocationHistoryRecyclerViewAdapter(DummyContent.ITEMS, listener)
            }
        }
        return view
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
        fun onListFragmentInteraction(item: DummyItem?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            LocationHistoryFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
