package com.klk.fingerprintmobileapps.DBHelper

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.util.Log
import com.klk.fingerprintmobileapps.Model.Attendance
import com.klk.fingerprintmobileapps.Model.Staff

class AttendanceHandler (val db: SQLiteDatabase){

    fun insert(values: ContentValues){
        var result: Boolean = false
        val insertAttempt = db.insert(FMSQLiteOpenHelper.ATTENDANCE_TABLE, "", values)

        if (insertAttempt > 0)
            result = true

        showLog(result)
    }

    fun getAll(): ArrayList<Attendance>{
        var attendanceList = ArrayList<Attendance>()

        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = FMSQLiteOpenHelper.ATTENDANCE_TABLE
        val rows = arrayOf(FMSQLiteOpenHelper.ATTENDANCE_ID,
                FMSQLiteOpenHelper.ATTENDANCE_STAFF_ID,
                FMSQLiteOpenHelper.ATTENDANCE_TIME,
                FMSQLiteOpenHelper.ATTENDANCE_LONGITUDE,
                FMSQLiteOpenHelper.ATTENDANCE_LATITUDE)

        val queryResult = queryBuilder.query(db, rows, null, null, null, null, null)

        if (queryResult.moveToFirst()) {

            do {
                val attendance_id = queryResult.getInt(queryResult.getColumnIndex(FMSQLiteOpenHelper.ATTENDANCE_ID))
                val attendance_staff_id = queryResult.getInt(queryResult.getColumnIndex(FMSQLiteOpenHelper.ATTENDANCE_STAFF_ID))
                val attendance_time = queryResult.getString(queryResult.getColumnIndex(FMSQLiteOpenHelper.ATTENDANCE_TIME))
                val attendance_longitude = queryResult.getString(queryResult.getColumnIndex(FMSQLiteOpenHelper.ATTENDANCE_LONGITUDE))
                val attendance_latitude = queryResult.getString(queryResult.getColumnIndex(FMSQLiteOpenHelper.ATTENDANCE_LATITUDE))

                attendanceList.add(Attendance(attendance_id, attendance_staff_id, attendance_time, attendance_longitude, attendance_latitude))
            } while (queryResult.moveToNext())
        }

//        var count: Int = staffList.size

        return attendanceList
    }

    fun update(values: ContentValues, id: Int) {

        var selectionArs = arrayOf(id.toString())
        var valid: Boolean = false
        val updateAttempt = db!!.update(FMSQLiteOpenHelper.ATTENDANCE_TABLE, values, "id=?", selectionArs)

        if (updateAttempt > 0) {
            valid = true
        }

        showLog(valid)
    }

    fun delete(id: Int){

        var selectionArs = arrayOf(id.toString())
        var valid: Boolean = false

        val deleteAttempt = db!!.delete(FMSQLiteOpenHelper.ATTENDANCE_TABLE, "id=?", selectionArs)
        if (deleteAttempt > 0) {
            valid = true
        }

        showLog(valid)
    }

    fun showLog(flag: Boolean){
        if (flag){
            Log.w(FMSQLiteOpenHelper.TAG, "Success")
        } else {
            Log.e(FMSQLiteOpenHelper.TAG, "Fail")
        }
    }

}