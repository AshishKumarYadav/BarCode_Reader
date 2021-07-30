package com.ashish.idflow.database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scan_table")
public class ScanTable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "data")
    String data;

    @ColumnInfo(name = "timestamp")
    String timeStamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
