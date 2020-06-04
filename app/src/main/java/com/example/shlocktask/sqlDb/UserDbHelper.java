package com.example.shlocktask.sqlDb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.shlocktask.model.InspectionList;

public class UserDbHelper extends SQLiteOpenHelper {

    public static String column1 = "hdn_ScheduleID";
     public static String column2 = "hdn_ScheduleCode";
    public static String column3 = "LTE_INSP_Label1";
    public static String column4 = "LTE_INSP_Label2";
    public static String column5 = "LTE_INSP_Label3";
    public static String column6 = "LTE_INSP_Label5";
    private static String tableName = "inspectionItems";

    private static final String DATABASE_NAME = "TESTINFO.DB";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_QUERY =
            "CREATE TABLE "+tableName+" ( " + column1 + " VARCHAR, " + column2 + " VARCHAR ,"+column3+" VARCHAR,"+column4+" VARCHAR ,"+column5+" VARCHAR,"+column6+" VARCHAR);";


    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("Database operations", "Database created or opened...");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
    }

    public void addInformation(InspectionList.InspectionItem inspectionItem, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column1, inspectionItem.getHdn_ScheduleID());
        contentValues.put(column2, inspectionItem.getHdn_ScheduleCode());
        contentValues.put(column3, inspectionItem.getLTE_INSP_Label1());
        contentValues.put(column4, inspectionItem.getLTE_INSP_Label2());
        contentValues.put(column5, inspectionItem.getLTE_INSP_Label3());
        contentValues.put(column6, inspectionItem.getLTE_INSP_Label5());

        db.insert(tableName, null, contentValues);

        Log.e("Database operations", "One row inserted ..."+inspectionItem.getHdn_ScheduleCode());
    }

    public Cursor getInformations(SQLiteDatabase db) {
        Cursor cursor;
        String[] projections = {column1,column2,column3,column4,column5,column6};
        cursor = db.rawQuery("select * from "+tableName,null);
        return cursor;
    }
        public void eraseData(SQLiteDatabase db)
    {
        db.execSQL("DELETE FROM " + tableName);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean deleteInception(SQLiteDatabase db,String name)
    {
        System.out.println("!!!!!!="+name);
        return db.delete(tableName, column1 + "=?" , new String[]{String.valueOf(name)}) > 0;
    }

}
