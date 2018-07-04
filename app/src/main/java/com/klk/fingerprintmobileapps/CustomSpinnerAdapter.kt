package com.klk.fingerprintmobileapps

import android.R
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_main.*

class CustomSpinnerAdapter {
    fun setAdapter(context: Context, spinner: Spinner, list: Array<String>){
        val mAdapter = ArrayAdapter(context, R.layout.simple_spinner_item, list)
        mAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinner.adapter = mAdapter
    }
}