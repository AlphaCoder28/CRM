package com.goldmedal.crm.data.db.entities


import androidx.room.Entity
import androidx.room.PrimaryKey


const val CURRENT_USER_ID = 1

@Entity
data class User(

        val UserPhone: String? = null,
        val UserName: String? = null,
        val UserEmail: String? = null,
        val EmpCode: String? = null,
        val UserId: Int? = null,
        val Address: String? = null,
        val Pincode: String? = null,
        val AreaName: String? = null,
        val CityName: String? = null,
        val StateName: String? = null,
        val CountryName: String? = null,
        val ServiceCenter: String? = null,
        val ScAddress: String? = null,
        val JoiningDate: String? = null,
        val HighestQualification: String? = null,
        val PasswordSalt: String? = null,
        val PasswordFormat: String? = null,
        val Password: String? = null,
        val MasterPassword: String? = null,
        val WorkExp: String? = null,
        val ProfilePhoto: String? = null,
        val ServiceCenterID: Int? = null,
        val RoleName: String? = null

) {
    @PrimaryKey(autoGenerate = false)
    var uid: Int = CURRENT_USER_ID
}