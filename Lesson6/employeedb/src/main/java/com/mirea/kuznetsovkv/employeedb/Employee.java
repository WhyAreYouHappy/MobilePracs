package com.mirea.kuznetsovkv.employeedb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Employee {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    public int salary;
}
