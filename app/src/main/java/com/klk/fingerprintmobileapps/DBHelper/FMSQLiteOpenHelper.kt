package com.klk.fingerprintmobileapps.DBHelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FMSQLiteOpenHelper: SQLiteOpenHelper {

    companion object {

        val TAG = "FMSQLiteOpenHelper"
        val DBName = "FMDB"
        val DBVersion = 3

        val STAFF_TABLE = "staff"
        val STAFF_ID = "id"
        val STAFF_NAME = "name"
        val STAFF_PHOTO = "photo"
        val STAFF_FINGER = "finger"

        val ATTENDANCE_TABLE = "attendance"
        val ATTENDANCE_ID = "id"
        val ATTENDANCE_STAFF_ID = "staff_id"
        val ATTENDANCE_TIME = "time"
        val ATTENDANCE_LONGITUDE = "longitude"
        val ATTENDANCE_LATITUDE = "latitude"
    }

    val CREATE_STAFF = "create table ${STAFF_TABLE} " +
            "(${STAFF_ID} int primary key, " +
            "${STAFF_NAME} text, " +
            "${STAFF_PHOTO} text, " +
            "${STAFF_FINGER} text)"
    val CREATE_ATTENDANCE = "create table ${ATTENDANCE_TABLE} " +
            "(${ATTENDANCE_ID} int primary key, " +
            "${ATTENDANCE_STAFF_ID} int, " +
            "${ATTENDANCE_TIME} text, " +
            "${ATTENDANCE_LATITUDE} text, " +
            "${ATTENDANCE_LONGITUDE} text)"

    var context: Context? = null
    var db: SQLiteDatabase


    constructor(context: Context) : super(context, DBName, null, DBVersion) {
        this.context = context;
        db = this.writableDatabase;
    }

    fun getSQLiteDatabase(): SQLiteDatabase{
        return db
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_STAFF)
        db.execSQL(CREATE_ATTENDANCE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL("Drop table IF EXISTS " + STAFF_TABLE)
        db!!.execSQL("Drop table IF EXISTS " + ATTENDANCE_TABLE)
        onCreate(db)
    }
}