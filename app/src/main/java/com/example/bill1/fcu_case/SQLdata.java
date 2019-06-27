package com.example.bill1.fcu_case;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.os.Build;


import java.sql.Blob;

public class SQLdata extends SQLiteOpenHelper {
    private final  static  String DBname = "DB2019.db";//database
    private static final  String TBname = "TB2019";//資料表
    private  final  static  int DBversion = 1;//版本

    private static final String useTBsql =
            "CREATE TABLE " +TBname+"("+
            "useNum VARCHAR(100) NOT NULL, "+
            "useName VARCHAR(20) NOT NULL , "+

            "useSoc int(10),"+
            "useFeel VARCHAR(20),"+
            "useAdd VARCHAR(20),PRIMARY KEY(useNo));";


    public SQLdata(Context context, String DBname, SQLiteDatabase.CursorFactory cursorFactory, int DBversion)
    {
        super(context, "2019.db", null,1);
    }

    public SQLdata(Context context) {
        //super(context, name, factory, version);
        super(context, "DB2019.db" , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(useTBsql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop TABLE IFEXISTS" + TBname);
        onCreate(db);
    }


    public long insertRec(String UseNum, String UseName, String UseSoc, String UseFeel)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("useNum", UseNum);
        rec.put("useName", UseName);

        rec.put("useSoc", UseSoc);
        rec.put("useFeel",UseFeel);

        long rowID = db.insert(TBname,null,rec);
        db.close();
        return rowID;


    }

    public int RecCount()
    {
        SQLiteDatabase db = getWritableDatabase();
        String  sql = "SELECT * FROM "+TBname;
        Cursor recSet = db.rawQuery(sql,null);
        return recSet.getCount();

    }
}





