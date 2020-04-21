package com.locationtracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.locationtracker.R
import com.locationtracker.sources.cache.data.BannerVO
import kotlinx.android.synthetic.main.layout_image_view.view.*

class ImageViewPagerAdapter(context: Context) : PagerAdapter() {
    var adImageView: ImageView? = null
    private var data: List<BannerVO> = ArrayList<BannerVO>()
    private val mLayoutInflator: LayoutInflater = LayoutInflater.from(context)
    private var viewPager: ViewPager? = null
    fun setData(data: List<BannerVO>) {
        this.data = data
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View =
            mLayoutInflator.inflate(R.layout.layout_image_view, container, false)

        if (data.isNotEmpty()) {
            view.adFactTv.text = data[position].fact
            viewPager = container as ViewPager
            viewPager!!.addView(view)
        }
        return view
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {
        return view === `object`
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        container.removeView(`object` as View)
    }

}