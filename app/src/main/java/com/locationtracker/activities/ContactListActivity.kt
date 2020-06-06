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
import com.locationtracker.sources.ListType
import com.locationtracker.sources.cache.data.SettingsVO
import com.locationtracker.viewholder.SettingsViewHolder
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_contact_list.*

import kotlinx.android.synthetic.main.layout_settings_sheet.view.*

class ContactListActivity : BaseActivity<MainViewModel>(), SettingsDelegate, ContactDelegate {

    private var currentListType: ListType = ListType.ContactList
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
        viewModel.getSettingsList(currentListType)
    }

    override fun onNetworkError() {

    }

    override fun retry() {

    }

    override fun initUI() {
        swipeRefreshLayout.isEnabled = false
        swipeRefreshLayout.isRefreshing = true
        contactRv.configure(this, contactAdapter)


        if (intent.getStringExtra(listType) == CONTACT_LIST) {
            toolBarTitleTv.text = "ဆက်သွယ်ရန်"
            currentListType = ListType.ContactList
        } else {
            toolBarTitleTv.text = "အခမဲ့ဆေးပေးခန်းများ"
            currentListType = ListType.FOCClinics
        }
        toolBar.addBackNavButton(this, R.drawable.ic_chevron_white)

        viewModel.settingsListLD.observe(this, Observer {
            settingList.clear()
            settingList.addAll(it)
            createSettingsList(settingList)
            if (settingList.isNotEmpty()) {
                sectorSelector.text = settingList.first().settingsName
                if (currentListType is ListType.ContactList)
                    viewModel.getRegionContactList(settingList.first().settingsID)
                else
                    viewModel.getRegionClinicsList(settingList.first().settingsID)
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
        val adapter = SettingsAdapter(this, this, SettingsViewHolder.SettingType.Region(0))
        dialogView.settingsRv.configure(this, adapter)
        adapter.appendNewData(list)
        settingsSheet.setContentView(dialogView)
    }

    override fun onSettingsClicked(data: SettingsViewHolder.SettingType) {
        when (data) {
            is SettingsViewHolder.SettingType.Region -> {
                if (settingList.isNotEmpty()) {
                    sectorSelector.text = settingList[data.adapterPosition].settingsName
                    if (currentListType is ListType.ContactList)
                        viewModel.getRegionContactList(settingList[data.adapterPosition].settingsID)
                    else
                        viewModel.getRegionClinicsList(settingList[data.adapterPosition].settingsID)
                    swipeRefreshLayout.isRefreshing = true
                }
                settingsSheet.dismiss()
            }
            is SettingsViewHolder.SettingType.Phone -> {
                callToPhone(convertToEngPhoneNumber(data.phone))
                phoneNumberSheet.dismiss()
            }
        }
    }

    companion object {
        const val CONTACT_LIST = "CONTACTS"
        const val CLINICS_LIST = "CLINICS"
        private const val listType = "LIST_TYPE"
        fun newIntent(context: Context, type: String) =
            Intent(context, ContactListActivity::class.java).apply {
                putExtra(listType, type)
            }
    }

    override fun callPhone(phoneNumberList: List<SettingsVO>) {
        showPhoneNumberList(phoneNumberList)
    }

    /*the phone numbers in the original data set comes up with myanmar, so this is used to convert it to the english phone number*/
    //todo refactor this method with regex
    private fun convertToEngPhoneNumber(mmPhoneNumber: String): String {
        showLogD("NumberConversion","$mmPhoneNumber is before ")
        var numberToBeConverted = mmPhoneNumber
        numberToBeConverted = numberToBeConverted.replace("-","")
        numberToBeConverted = numberToBeConverted.replace("၁","1")
        numberToBeConverted = numberToBeConverted.replace("၂","2")
        numberToBeConverted = numberToBeConverted.replace("၃","3")
        numberToBeConverted = numberToBeConverted.replace("၄","4")
        numberToBeConverted = numberToBeConverted.replace("၅","5")
        numberToBeConverted = numberToBeConverted.replace("၆","6")
        numberToBeConverted = numberToBeConverted.replace("၇","7")
        numberToBeConverted = numberToBeConverted.replace("၈","8")
        numberToBeConverted = numberToBeConverted.replace("၉","9")
        numberToBeConverted = numberToBeConverted.replace("၀","0")
        showLogD("NumberConversion","$numberToBeConverted is after ")
        return numberToBeConverted
    }

    private fun showPhoneNumberList(list: List<SettingsVO>) {
        val dialogView: View =
            layoutInflater.inflate(R.layout.layout_settings_sheet_phone, null)
        val adapter = SettingsAdapter(this, this, SettingsViewHolder.SettingType.Phone(""))
        dialogView.settingsRv.configure(this, adapter)
        adapter.appendNewData(list)
        phoneNumberSheet.setContentView(dialogView)
        phoneNumberSheet.show()
    }

}
