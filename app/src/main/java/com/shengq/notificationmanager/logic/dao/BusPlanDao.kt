package com.shengq.notificationmanager.logic.dao

import androidx.room.*

@Dao
interface BusPlanDao {
    @Insert
    fun insertBusPlan(dao:BusPlan):Long

    @Update
    fun updateBusPlan(dao:BusPlan)

    @Query("select * from busPlan")
    fun loadAllBusPlan():List<BusPlan>

    @Delete
    fun deleteBusPlan(dao:BusPlan)



    @Query("delete from BusPlan where id = :id")
    fun deleteUserByLastId(id: Long):Int

}