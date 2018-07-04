package com.klk.fingerprintmobileapps

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG: String? = "MainActivity"

    private val mStaffIdList = arrayOf("001", "002", "003")
    private val mStaffFingerDataList = arrayOf("fd001", "fd002", "fd003")

    private var mStaffId: String? = null
    private var mStaffFingerData: String? = null

    private val customSpinnerAdapter = CustomSpinnerAdapter()

    private var mLocationLong: String? = null
    private var mLocationLat: String? = null
    private var mLocationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager?;

        customSpinnerAdapter.setAdapter(this, spinnerStaffId, mStaffIdList)
        customSpinnerAdapter.setAdapter(this, spinnerStaffFingerData, mStaffFingerDataList)

        init()
    }

    private fun init(){
        spinnerStaffId.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    mStaffId = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        spinnerStaffFingerData.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    mStaffFingerData = parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        btnScan.setOnClickListener {
            var valid: Boolean = checkValid()

            if (valid) {
                getResult()
            } else {
                Toast.makeText(this,
                        "Gagal",
                        Toast.LENGTH_LONG).show()
            }
        }
    }

    fun checkValid(): Boolean{
        var valid: Boolean = false
        if ((mStaffId.equals("001") && mStaffFingerData.equals("fd001"))
                || (mStaffId.equals("002") && mStaffFingerData.equals("fd002"))
                || (mStaffId.equals("003") && mStaffFingerData.equals("fd003"))) {
            valid = true
        }

        return valid
    }

    fun getResult(){
        val date = Date();
        val formatter = SimpleDateFormat("dd MMM yyyy HH:mm")
        val answer: String = formatter.format(date)

        mLocationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, mLocationListener);

        Toast.makeText(this,
                "Staff ID : ${mStaffId} | " +
                        "Date : ${answer} | " +
                        "Longitude : ${mLocationLong} | " +
                        "Latitude : ${mLocationLat}",
                Toast.LENGTH_LONG).show()
    }

    private val mLocationListener: LocationListener = object: LocationListener{
        override fun onLocationChanged(location: Location?) {
            Log.e(TAG, "iblis")
            mLocationLong = location?.longitude.toString()
            mLocationLat = location?.latitude.toString()
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String?) {}

        override fun onProviderDisabled(provider: String?) {}
    }
}
