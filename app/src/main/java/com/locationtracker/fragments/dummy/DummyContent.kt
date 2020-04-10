package com.locationtracker.fragments.dummy

import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<DummyItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, DummyItem> = HashMap()

    private const val COUNT = 25

    init {
        // Add some sample items.
        for (i in 1..COUNT) {
            addItem(DummyItem(i.toString(),
                "Insein Road, Sanchaung Township, Yangon, Yangon Division",
                "12:00 PM - 02:00 PM"))
        }
    }

    private fun addItem(item: DummyItem) {
        ITEMS.add(item)
        ITEM_MAP[item.id] = item
    }

    /**
     * A dummy item representing a piece of content.
     */
    data class DummyItem(val id: String, val address: String, val timeInterval: String) {
//        var timeStamp : String =""
//        var time : String = ""
//        var latitude: String = ""
//        var longitude: String = ""
//        var address : String = ""
//        var city : String = ""
//        var road : String =""
//        var suburb : String =""
//        var county : String =""
//        var state : String = ""
//        var postCode : String = ""
//        var country : String = ""
//        var dateTime : String = ""
//        var retrievedBy : String = ""
        override fun toString(): String = address + " \n" + timeInterval
    }
}
