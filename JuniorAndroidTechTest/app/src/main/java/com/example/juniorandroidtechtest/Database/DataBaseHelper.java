package com.example.juniorandroidtechtest.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "team_table";
    private static final String COL1 = "Team_ID";
    private static final String COL2 = "Name";
    private static final String COL3 = "Photo";


    public DataBaseHelper(Context context) {
        super(context,TABLE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = ("CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1+ " TEXT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT)");
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addData(String id, String name, String photo ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, id);
        contentValues.put(COL2, name);
        contentValues.put(COL3, photo);

        Log.d(TAG, "addData: Adding " + id + " to " + TABLE_NAME);
        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<String> getDataArray() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> teamSaved = new ArrayList<String>();

        try {

            Cursor c = db.rawQuery("SELECT * FROM team_table", null);

                if(c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndex(COL1));
                        String name = c.getString(c.getColumnIndex(COL2));
                        String image = c.getString(c.getColumnIndex(COL3));

                        teamSaved.add(id);
                        teamSaved.add(name);
                        teamSaved.add(image);

                }
                else{
                    // doesnt exist
                    }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return teamSaved;
    }


    public void deleteTableColumn(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM team_table WHERE id IN (SELECT id FROM team_table ORDER BY id DESC LIMIT 1);";
        db.execSQL(query);
    }

    public int tableLength(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM team_table", null);
        return c.getColumnCount();
    }

}
