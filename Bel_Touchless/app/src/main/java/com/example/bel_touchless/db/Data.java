package com.example.bel_touchless.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Data {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "Jarak")
    public String jarak;

    @ColumnInfo(name = "Suhu")
    public String suhu;

    @ColumnInfo(name = "Waktu")
    public String waktu;

}
