package com.goldmedal.crm.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.goldmedal.crm.data.db.entities.*


@Database(
    entities = [User::class, TicketHistoryData::class],
    version = 1,
        exportSchema = false
)


abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao
    abstract fun getTicketHistoryDao(): TicketHistoryDao
    //abstract fun getBirthDateDao(): BirthdayDao
    //abstract fun getAnniversaryDateDao(): AnniversaryDao
//    abstract fun getHolidayDao(): HolidayDao
    //abstract fun getAllHolidayDao(): AllHolidayDao
    //abstract fun getAllAttendanceDao(): AttendanceDao
    //abstract fun getCurrentAttendanceDao(): CurrentAttendanceDao
    //abstract fun getEmployeeAttendanceDao(): EmployeeAttendanceDao
    //abstract fun getLeaveBalanceDao(): LeaveBalanceDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "room-database-crm.db"
            ).build()
    }
}