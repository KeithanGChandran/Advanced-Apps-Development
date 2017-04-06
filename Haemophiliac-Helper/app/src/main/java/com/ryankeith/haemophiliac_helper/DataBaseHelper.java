package com.ryankeith.haemophiliac_helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by RenruiLiu on 6/04/2017.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Records.db";
    public static final String InfusionTable = "infusionTable";
    public static final String BleedingTable = "bleedingTable";
    public static final String InfCOL_1 = "ID";
    public static final String InfCOL_2 = "Date";
    public static final String InfCOL_3 = "Dose";
    public static final String InfCOL_4 = "TypeOfTreatment";
    public static final String InfCOL_5 = "Description";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + InfusionTable + " (" + InfCOL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InfCOL_2 + " TEXT NOT NULL UNIQUE,"
                + InfCOL_3 + " TEXT, "
                +InfCOL_4+" TEXT, "
                +InfCOL_5+" TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + InfusionTable);
        onCreate(sqLiteDatabase);
    }

    public boolean insertInfusionData(String date, String dose, String type, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(InfCOL_2, date);
        contentValues.put(InfCOL_3, dose);
        contentValues.put(InfCOL_4, type);
        contentValues.put(InfCOL_5, description);
        long result = db.insert(InfusionTable, null, contentValues);
        return result != -1;
    }

    public void deleteData(int ID,String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + table + " where ID='" + ID + "'");
    }

    //get data and sort them with latest date comes first.
    public Cursor getAllData(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table + " ORDER BY Date desc", null);
        return res;
    }
}
