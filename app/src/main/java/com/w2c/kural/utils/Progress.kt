package com.w2c.kural.utils

import android.app.ProgressDialog
import android.content.*
import com.w2c.kural.R

/**
 * This Singleton class is the progress bar class
 * We are using ProgressDialog class for progress
 */
class Progress private constructor(context: Context) {
    private val mDialog: ProgressDialog

    /**
     * This is used to display progress
     */
    fun showProgress() {
        if (!mDialog.isShowing) {
            mDialog.show()
        }
    }

    /**
     * This is used to hide progress
     */
    fun hideProgress() {
        if (mDialog.isShowing) {
            mDialog.hide()
        }
    }

    companion object {
        private var mProgress: Progress? = null

        /**
         * Setting up singleton class for Progress bar
         *
         * @param context Context
         * @return Progress object
         */
        fun getInstance(context: Context): Progress? {
            if (mProgress == null) {
                mProgress = Progress(context)
            }
            return mProgress
        }
    }

    /**
     * Initialization of ProgressDialog
     *
     * @param context Context
     */
    init {
        mDialog = ProgressDialog(context)
        mDialog.setCancelable(false)
        mDialog.setMessage(context.getString(R.string.wait))
    }
}