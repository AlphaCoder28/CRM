package com.goldmedal.crm.data.repositories



import com.goldmedal.crm.data.db.AppDatabase
import com.goldmedal.crm.data.network.SafeApiRequest

class ProfileRepository(

        private val db: AppDatabase
) : SafeApiRequest() {








    // suspend fun saveUser(user: User?) = db.getUserDao().upsert(user)

     fun getUser() = db.getUserDao().getUser()

}