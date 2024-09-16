package com.goldmedal.crm.data.db.entities


import androidx.room.Entity
import androidx.room.PrimaryKey


const val CURRENT_USER_ID = 1

@Entity
data class User(

        var UserPhone: String? = null,
        var UserName: String? = null,
        var UserEmail: String? = null,
        var EmpCode: String? = null,
        var UserId: Int? = null,
        var Address: String? = null,
        var Pincode: String? = null,
        var AreaName: String? = null,
        var CityName: String? = null,
        var StateName: String? = null,
        var CountryName: String? = null,
        var ServiceCenter: String? = null,
        var ScAddress: String? = null,
        var JoiningDate: String? = null,
        var HighestQualification: String? = null,
        var PasswordSalt: String? = null,
        var PasswordFormat: String? = null,
        var Password: String? = null,
        var MasterPassword: String? = null,
        var WorkExp: String? = null,
        var ProfilePhoto: String? = null,
        var ServiceCenterID: Int? = null,
        var RoleName: String? = null

) {
    @PrimaryKey(autoGenerate = false)
    var uid: Int = CURRENT_USER_ID
}