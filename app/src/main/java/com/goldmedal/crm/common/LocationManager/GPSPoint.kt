package com.goldmedal.crm.common.LocationManager

import java.text.DateFormat
import java.util.*

/**
 * * Created by alejandro.tkachuk
 */
class GPSPoint {
    var latitude: Double
        private set
    var longitude: Double
        private set
    var date: Date? = null
        private set
    var lastUpdate: String? = null
        private set

    constructor(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
        date = Date()
        lastUpdate = DateFormat.getTimeInstance().format(date)
    }

    constructor(latitude: Double?, longitude: Double?) {
        this.latitude = latitude!!
        this.longitude = longitude!!
    }

    override fun toString(): String {
        return "(" + latitude + ", " + longitude + ")"
    }
}