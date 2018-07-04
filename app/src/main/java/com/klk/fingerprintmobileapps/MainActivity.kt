package com.klk.fingerprintmobileapps

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
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

    private fun checkAndRequestPermissions(): Boolean {
        val accessFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val accessCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)

        val listPermissionsNeeded = ArrayList<String>()

        // Add list of permissions needed
        if(accessFineLocationPermission != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (accessCoarseLocationPermission != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (internetPermission != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.INTERNET)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        Log.d(TAG, "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.ACCESS_FINE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.INTERNET] = PackageManager.PERMISSION_GRANTED

                // Fill with actual results from user
                if (grantResults.size > 0) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (!(perms[Manifest.permission.ACCESS_COARSE_LOCATION] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.ACCESS_FINE_LOCATION] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.INTERNET] == PackageManager.PERMISSION_GRANTED)) {
                        Log.d(TAG, "Some permissions are not granted ask again ")
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                            showDialogOK("Service Permissions are required for this app",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                            DialogInterface.BUTTON_NEGATIVE ->
                                                // proceed with logic by disabling the related features or quit the app.
                                                finish()
                                        }
                                    })
                        } else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }//permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                    } else {
                        Log.d(TAG, "internet & location services permission granted")
                    }
                }
            }
        }

    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    private fun explain(msg: String) {
        val dialog = android.support.v7.app.AlertDialog.Builder(this)
        dialog.setMessage(msg)
                .setPositiveButton("Yes") { paramDialogInterface, paramInt ->
                    //  permissionsclass.requestPermission(type,code);
                    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.klk.fingerprintmobileapps")))
                }
                .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> finish() }
        dialog.show()
    }

    companion object {
        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        private val SPLASH_TIME_OUT = 2000
    }

    @SuppressLint("MissingPermission")
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
            var valid: Boolean = false
            if ((mStaffId.equals("001") && mStaffFingerData.equals("fd001"))
                    || (mStaffId.equals("002") && mStaffFingerData.equals("fd002"))
                    || (mStaffId.equals("003") && mStaffFingerData.equals("fd003"))) {
                valid = true
            } else {
                valid = false
            }

            if (valid) {
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
            } else {
                Toast.makeText(this, "Gagal", Toast.LENGTH_LONG).show()
            }
        }
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
