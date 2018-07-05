package com.klk.fingerprintmobileapps.Model

class Attendance{
    var id: Int? = null
    var staff_id: Int? = null
    var time: String? = null
    var longitude: String? = null
    var latitude: String? = null

    constructor(id : Int, staff_id : Int, time : String, longitude : String, latitude : String){
        this.id = id
        this.staff_id = staff_id
        this.time = time
        this.longitude = longitude
        this.latitude = latitude
    }

}