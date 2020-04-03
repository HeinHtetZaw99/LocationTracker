/*
 *  Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.appbase.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.appbase.addPrimaryButtonTextEffext
import kotlinx.android.synthetic.main.empty_view_loading.view.*


class EmptyLoadingViewPod : RelativeLayout {
    /**
     * When Importing this class to use in another project, make sure to import other resources as well
     */
    private var onRefreshListener: OnRefreshListener? = null
    internal var context: Context
    internal var currentState = EmptyViewState.INITIAL
    private var customErrorView: View? = null
    private var overrideDefault: Boolean = false


    constructor(context: Context) : super(context) {
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.context = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.context = context
    }

    fun setOnRefreshListener(onRefreshListener: OnRefreshListener) {
        this.onRefreshListener = onRefreshListener
    }

    fun setOverrideDefault(override: Boolean) {
        this.overrideDefault = override
    }

    fun setEmptyText(text: String) {
        emptyTv?.text = text
    }

    fun setCustomErrorView(view: View) {
        this.customErrorView = view
        if (customErrorFrameLayout.childCount > 0)
            customErrorFrameLayout.removeAllViews()
        customErrorFrameLayout.addView(view)
    }

    fun hidePullToRefresh() {
        pullToRefreshBtn?.visibility = View.GONE
        pullToRefreshTv?.visibility = View.GONE
    }

    fun showPullToRefresh() {
        pullToRefreshBtn?.visibility = View.VISIBLE
        pullToRefreshTv?.visibility = View.VISIBLE
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        this.setCurrentState(EmptyViewState.INITIAL)
        pullToRefreshBtn?.addPrimaryButtonTextEffext()?.setOnClickListener {
            if (onRefreshListener != null)
                onRefreshListener?.onRefreshButtonClicked()
            else
                throw RuntimeException("Implement EmptyLoadingViewPod.OnRefreshListener for this particular action")
        }
    }

    private fun toLoadingView() {
        errorView?.visibility = View.GONE
        loadingRootView?.visibility = View.VISIBLE
     /*   loadingView?.setAnimation("loading_animation.json")
        loadingView?.playAnimation()*/
    }

    private fun toInitialState() {
        errorView?.visibility = View.GONE
        if (loadingView!= null && loadingView.isAnimating)
            loadingView?.pauseAnimation()
        loadingRootView?.visibility = View.GONE
    }

    private fun toSearchView() {
        errorView?.visibility = View.GONE
        loadingRootView?.visibility = View.VISIBLE
       /* loadingView?.setAnimation("search_products.json")
        loadingView?.playAnimation()*/
    }

    fun isCustomViewAlreadyAdded() = customErrorView != null

    private fun toErrorView() {
        if (customErrorView != null && overrideDefault) {
            customErrorFrameLayout.visibility = View.VISIBLE
            errorView?.visibility = View.GONE
        } else {
            customErrorFrameLayout.visibility = GONE
            errorView?.visibility = View.VISIBLE
        }
        if (loadingView!!.isAnimating)
            loadingView?.pauseAnimation()
        loadingRootView?.visibility = View.GONE
    }

    fun setCurrentState(currentState: EmptyViewState) {
        this.currentState = currentState
        when (currentState) {
            EmptyViewState.INITIAL -> {
                toInitialState()
            }
            EmptyViewState.LOADING -> {
                toLoadingView()
                pullToRefreshBtn?.visibility = View.VISIBLE
            }
            EmptyViewState.SEARCHING -> {
                toSearchView()
                pullToRefreshBtn?.visibility = View.VISIBLE
            }
            EmptyViewState.SEARCH_ERROR -> {
                toErrorView()
                //todo change the implementation here
                /*    emptyIv?.setImageDrawable(context.getDrawable(R.drawable.ic_unexpected_error_v2))
                    emptyTv?.text = context.getString(R.string.title_msg_no_search_result)
                    pullToRefreshTv?.text = context.getString(R.string.title_msg_try_again_search)*/
                pullToRefreshBtn?.visibility = View.GONE
            }
            EmptyViewState.NETWORK_ERROR -> {
                toErrorView()
                //todo change the implementation here
                /*  emptyIv?.setImageDrawable(context.getDrawable(R.drawable.empty_view))
                  emptyTv?.text = context.getString(R.string.title_msg_emptyView)
                  pullToRefreshTv?.text = context.getString(R.string.title_msg_try_again)*/
                pullToRefreshBtn?.visibility = View.VISIBLE
            }
            EmptyViewState.UNEXPECTED_ERROR -> {
                toErrorView()
                //todo change the implementation here
                /*emptyIv?.setImageDrawable(context.getDrawable(R.drawable.ic_unexpected_error_v2))
                emptyTv?.text = context.getString(R.string.title_msg_unexpected_error_occured)
                pullToRefreshTv?.text = context.getString(R.string.title_msg_try_again_for_error)*/
                pullToRefreshBtn?.visibility = View.VISIBLE

            }
            EmptyViewState.NO_DATA -> {
                toErrorView()
                //todo change the implementation here
                /*emptyIv.setImageDrawable(context.getDrawable(R.drawable.empty_view))
                emptyTv.text = context.getString(R.string.txt_no_data_to_display)
                pullToRefreshTv.text =
                    context.getString(R.string.txt_more_data_will_be_feeded_soon)*/
                pullToRefreshBtn.visibility = VISIBLE

            }
            EmptyViewState.CUSTOM_ERROR -> {
                if (customErrorView == null)
                    throw java.lang.RuntimeException("Custom Error View has not defined yet")
                errorView?.visibility = View.GONE
                if (loadingView!!.isAnimating)
                    loadingView?.pauseAnimation()
                loadingRootView?.visibility = View.GONE
                customErrorFrameLayout.visibility = View.VISIBLE
            }
        }
    }


    enum class EmptyViewState {
        NETWORK_ERROR,
        LOADING,
        UNEXPECTED_ERROR,
        SEARCHING,
        INITIAL,
        NO_DATA,
        SEARCH_ERROR,
        CUSTOM_ERROR
    }

    interface OnRefreshListener {
        fun onRefreshButtonClicked()
    }

}

fun EmptyLoadingViewPod.hide() {
    if (this.loadingView.isAnimating)
        this.loadingView.pauseAnimation()
    this.visibility = RelativeLayout.GONE
}

fun EmptyLoadingViewPod.show() {
    this.visibility = RelativeLayout.VISIBLE
    setCurrentState(currentState = this.currentState)
}

fun EmptyLoadingViewPod.show(state: EmptyLoadingViewPod.EmptyViewState) {
    this.visibility = RelativeLayout.VISIBLE
    setCurrentState(currentState = state)
}