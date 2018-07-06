package com.klk.fingerprintmobileapps.DBHelper

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.util.Log
import com.klk.fingerprintmobileapps.Model.Staff

class StaffHandler (val db: SQLiteDatabase){

    fun insert(values: ContentValues){
        var result: Boolean = false
        val insertAttempt = db.insert(FMSQLiteOpenHelper.STAFF_TABLE, "", values)

        if (insertAttempt > 0)
            result = true

        showLog(result)
    }

    fun getAll(): ArrayList<Staff>{
        var staffList = ArrayList<Staff>()

        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = FMSQLiteOpenHelper.STAFF_TABLE
        val rows = arrayOf(FMSQLiteOpenHelper.STAFF_ID,
                FMSQLiteOpenHelper.STAFF_NAME,
                FMSQLiteOpenHelper.STAFF_PHOTO,
                FMSQLiteOpenHelper.STAFF_FINGER)

        val queryResult = queryBuilder.query(db, rows, null, null, null, null, null)

        if (queryResult.moveToFirst()) {

            do {
                val staff_id = queryResult.getInt(queryResult.getColumnIndex(FMSQLiteOpenHelper.STAFF_ID))
                val staff_name = queryResult.getString(queryResult.getColumnIndex(FMSQLiteOpenHelper.STAFF_NAME))
                val staff_photo = queryResult.getString(queryResult.getColumnIndex(FMSQLiteOpenHelper.STAFF_PHOTO))
                val staff_finger = queryResult.getString(queryResult.getColumnIndex(FMSQLiteOpenHelper.STAFF_FINGER))

                staffList.add(Staff(staff_id, staff_name, staff_photo, staff_finger))
            } while (queryResult.moveToNext())
        }

//        var count: Int = staffList.size

        return staffList
    }

    fun getAllFinger(): Array<String>{
        var fingerList = arrayOf<String>()

        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = FMSQLiteOpenHelper.STAFF_TABLE
        val rows = arrayOf(FMSQLiteOpenHelper.STAFF_FINGER)

        val queryResult = queryBuilder.query(db, rows, null, null, null, null, null)

        if (queryResult.moveToFirst()) {

            do {
                val staff_finger = queryResult.getString(queryResult.getColumnIndex(FMSQLiteOpenHelper.STAFF_FINGER))
                var count = 0
                fingerList[count] = staff_finger
                count++
            } while (queryResult.moveToNext())
        }

        return fingerList
    }

    fun getAllStaffId(): Array<String>{
        var idList = arrayOf<String>()

        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = FMSQLiteOpenHelper.STAFF_TABLE
        val rows = arrayOf(FMSQLiteOpenHelper.STAFF_ID)

        val queryResult = queryBuilder.query(db, rows, null, null, null, null, null)

        if (queryResult.moveToFirst()) {

            do {
                val staff_id = (queryResult.getInt(queryResult.getColumnIndex(FMSQLiteOpenHelper.STAFF_ID))).toString()
                var count = 0
                idList[count] = staff_id
                count++
            } while (queryResult.moveToNext())
        }

        return idList
    }

    fun update(values: ContentValues, id: Int) {

        var selectionArs = arrayOf(id.toString())
        var valid: Boolean = false
        val updateAttempt = db!!.update(FMSQLiteOpenHelper.STAFF_TABLE, values, "id=?", selectionArs)

        if (updateAttempt > 0) {
            valid = true
        }

        showLog(valid)
    }

    fun delete(id: Int){

        var selectionArs = arrayOf(id.toString())
        var valid: Boolean = false

        val deleteAttempt = db!!.delete(FMSQLiteOpenHelper.STAFF_TABLE, "id=?", selectionArs)
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