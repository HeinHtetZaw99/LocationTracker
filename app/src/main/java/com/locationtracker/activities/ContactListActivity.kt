package com.locationtracker.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.appbase.activities.BaseActivity
import com.appbase.addBackNavButton
import com.appbase.configure
import com.appbase.showLogD
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.locationtracker.R
import com.locationtracker.SettingsDelegate
import com.locationtracker.adapters.ContactAdapter
import com.locationtracker.adapters.SettingsAdapter
import com.locationtracker.delegates.ContactDelegate
import com.locationtracker.sources.cache.data.SettingsVO
import com.locationtracker.viewholder.SettingsViewHolder
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_contact_list.*

import kotlinx.android.synthetic.main.layout_settings_sheet.view.*

class ContactListActivity : BaseActivity<MainViewModel>(), SettingsDelegate, ContactDelegate {

    private val settingList = ArrayList<SettingsVO>()
    private val settingsSheet: BottomSheetDialog by lazy {
        BottomSheetDialog(
            this,
            R.style.SheetDialog
        )
    }

    private val phoneNumberSheet: BottomSheetDialog by lazy {
        BottomSheetDialog(
            this,
            R.style.SheetDialog
        )
    }

    private val contactAdapter: ContactAdapter by lazy { ContactAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_list)
        initUI()
    }

    override val layoutResId: Int = R.layout.activity_contact_list

    override val rootLayout: ViewGroup? by lazy { null }

    override val viewModel: MainViewModel by contractedViewModels()


    override fun loadData() {
        viewModel.getSettingsList()
    }

    override fun onNetworkError() {

    }

    override fun retry() {

    }

    override fun initUI() {
        swipeRefreshLayout.isEnabled = false
        swipeRefreshLayout.isRefreshing = true
        contactRv.configure(this, contactAdapter)

        toolBar.addBackNavButton(this, R.drawable.ic_chevron_white)

        viewModel.settingsListLD.observe(this, Observer {
            settingList.clear()
            settingList.addAll(it)
            createSettingsList(settingList)
            if (settingList.isNotEmpty()) {
                sectorSelector.text = settingList.first().settingsName
                viewModel.getRegionContactList(settingList.first().settingsID)
            }
        })

        sectorSelector.setOnClickListener { settingsSheet.show() }

        viewModel.contactListLD.observe(this, Observer {
            swipeRefreshLayout.isRefreshing = false
            contactAdapter.appendNewData(it)
            showLogD("Contact Size : ${it.size}")
        })

        loadData()
    }

    override fun logOut() {

    }

    private fun createSettingsList(list: List<SettingsVO>) {
        val dialogView: View =
            layoutInflater.inflate(R.layout.layout_settings_sheet, null)
        val adapter = SettingsAdapter(this, this ,SettingsViewHolder.SettingType.Region(0))
        dialogView.settingsRv.configure(this, adapter)
        adapter.appendNewData(list)
        settingsSheet.setContentView(dialogView)
    }

    override fun onSettingsClicked(data: SettingsViewHolder.SettingType) {
        when (data) {
            is SettingsViewHolder.SettingType.Region -> {
                if (settingList.isNotEmpty()) {
                    sectorSelector.text = settingList[data.adapterPosition].settingsName
                    viewModel.getRegionContactList(settingList[data.adapterPosition].settingsID)
                    swipeRefreshLayout.isRefreshing = true
                }
                settingsSheet.dismiss()
            }
            is SettingsViewHolder.SettingType.Phone -> {
                callToPhone(data.phone)
                phoneNumberSheet.dismiss()
            }
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, ContactListActivity::class.java)
    }

    override fun callPhone(phoneNumberList: List<SettingsVO>) {
        showPhoneNumberList(phoneNumberList)
    }


    private fun showPhoneNumberList(list: List<SettingsVO>) {
        val dialogView: View =
            layoutInflater.inflate(R.layout.layout_settings_sheet_phone, null)
        val adapter = SettingsAdapter(this, this,SettingsViewHolder.SettingType.Phone(""))
        dialogView.settingsRv.configure(this, adapter)
        adapter.appendNewData(list)
        phoneNumberSheet.setContentView(dialogView)
        phoneNumberSheet.show()
    }

}
