package com.goldmedal.crm.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goldmedal.crm.data.db.entities.TicketHistoryData


@Dao
interface TicketHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicketHistory(historyData: List<TicketHistoryData?>)

    @Query("SELECT * FROM TicketHistoryData")
    fun getTicketHistory() : LiveData<List<TicketHistoryData>>

    @Query("DELETE FROM TicketHistoryData")
    suspend fun removeTicketHistory()
}