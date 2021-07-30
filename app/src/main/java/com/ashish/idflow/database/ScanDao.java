package com.ashish.idflow.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ScanDao {

    @Insert
    void insert(ScanTable scanTable);

    //Update EmployeeTable
    @Update
    void update(ScanTable scanTable);

    //Delete EmployeeTable
    @Delete
    void delete(ScanTable scanTable);

    //Delete all data from EmployeeTable;
    @Query("DELETE FROM scan_table")
    void deleteAll();

    //get all row
    @Query("SELECT * FROM scan_table")
    List<ScanTable> getAll();

}
