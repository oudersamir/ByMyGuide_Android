package com.example.myapplication;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

public class LocationsDB extends SQLiteOpenHelper{

    /** Database name */
    private static String DBNAME = "locationmarkersqlite";

    /** Version number of the database */
    private static int VERSION = 7;

    /** Field 1 of the table locations, which is the primary key */
    public static final String FIELD_ROW_ID = "_id";

    /** Field 2 of the table locations, stores the latitude */
        public static final String FIELD_LAT = "lat";

    /** Field 3 of the table locations, stores the longitude*/
    public static final String FIELD_LNG = "lng";

    /** Field 4 of the table locations, stores the zoom level of map*/
    public static final String FIELD_ZOOM = "zom";
    public static final String NAME_PLACE = "place";

    /** A constant, stores the the table name */
    private static final String DATABASE_TABLE = "locations";
    public static final String COLUMN_STATUS = "status";
    public static final String NOTES = "notes";


    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;

    /** Constructor */
    public LocationsDB(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }

    /** This is a callback method, invoked when the method getReadableDatabase() / getWritableDatabase() is called
     * provided the database does not exists
     * */
    public boolean addPlace(place place, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        // Setting latitude in ContentValues
        contentValues.put(LocationsDB.FIELD_LAT,place.getLat());

        // Setting longitude in ContentValues
        contentValues.put(LocationsDB.FIELD_LNG,place.getLng());

        // Setting zoom in ContentValues
        contentValues.put(LocationsDB.NAME_PLACE, place.getPlace());


        // Setting zoom in ContentValues
        contentValues.put(LocationsDB.FIELD_ZOOM,place.getZoom());
        contentValues.put(COLUMN_STATUS, status);

        contentValues.put(LocationsDB.NOTES, place.getNotes());

        db.insert(DATABASE_TABLE, null, contentValues);
        db.close();
        return true;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =     "create table " + DATABASE_TABLE + " ( " +
                FIELD_ROW_ID + " integer primary key autoincrement , " +
                FIELD_LNG + " double , " +
                FIELD_LAT + " double , " +
                NAME_PLACE + "  text , " +
                FIELD_ZOOM + " text ," +
                COLUMN_STATUS + " integer , " +
                NOTES + " double " +

                " ) ";

        db.execSQL(sql);
    }
    public boolean updatePlaceStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(DATABASE_TABLE, contentValues, FIELD_ROW_ID + "=" + id, null);
        db.close();
        return true;
    }


    /** Inserts a new location to the table locations */
    public long insert(ContentValues contentValues){
        long rowID = mDB.insert(DATABASE_TABLE, null, contentValues);
        return rowID;
    }

    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + DATABASE_TABLE + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /** Deletes all locations from the table */
    public int del(){
        int cnt = mDB.delete(DATABASE_TABLE, null , null);
        return cnt;
    }

    /** Returns all the locations from the table */
    public Cursor getAllLocations2(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c=db.query(DATABASE_TABLE, new String[] { FIELD_ROW_ID,  FIELD_LAT , FIELD_LNG,NAME_PLACE, FIELD_ZOOM ,COLUMN_STATUS,NOTES} , null, null, null, null, null);

        return  c;
    }
    public Cursor getAllLocations(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c=db.query(DATABASE_TABLE, new String[] { FIELD_ROW_ID,  FIELD_LAT , FIELD_LNG,NAME_PLACE, FIELD_ZOOM ,COLUMN_STATUS,"avg("+NOTES+") as notes"} , null, null, NAME_PLACE, null, null);

        return  c;
    }
    public static String addSlashes(String s) {
        s = s.replaceAll("\\\\", "\\\\\\\\");
        s = s.replaceAll("\\n", "\\\\n");
        s = s.replaceAll("\\r", "\\\\r");
        s = s.replaceAll("\\00", "\\\\0");
        s = s.replaceAll("'", "\\\\'");
        s = s.replaceAll(",", "\\\\'");

        return s;
    }
    public double getNotes(String place){
       String query = "SELECT  AVG("+NOTES+") as notes FROM " + LocationsDB.DATABASE_TABLE +"  where "+NAME_PLACE+"="+place+"  GROUP BY  "+NAME_PLACE+" ";
        SQLiteDatabase db = this.getWritableDatabase();
        place place1 = new place();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                place1.setNotes(cursor.getDouble(cursor.getColumnIndex(NOTES)));
            } while (cursor.moveToNext());
        }
        return  place1.getNotes();
    }
    public List<place> PlacesList(String filter) {
        String query;
        if(filter.equals("")){
            //regular query
            query = "SELECT  "+FIELD_ROW_ID +" , "+  FIELD_LAT+"  , "+  FIELD_LNG +" , "+NAME_PLACE+" , "+ FIELD_ZOOM +" ,"+COLUMN_STATUS+ " , AVG("+NOTES+") as notes , count("+NOTES+") as number FROM " + LocationsDB.DATABASE_TABLE +"  GROUP BY  "+NAME_PLACE+" ORDER BY _id";
        }else{
            //filter results by filter option provided
            query = "SELECT  * FROM " + LocationsDB.DATABASE_TABLE+ " ORDER BY "+ filter;
        }

        List<place> personLinkedList = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        place place;

        if (cursor.moveToFirst()) {
            do {
                place = new place();

                place.setId(cursor.getLong(cursor.getColumnIndex(FIELD_ROW_ID)));
                place.setPlace(cursor.getString(cursor.getColumnIndex(NAME_PLACE)));
                place.setLat(cursor.getDouble(cursor.getColumnIndex(FIELD_LAT)));
                place.setLng(cursor.getDouble(cursor.getColumnIndex(FIELD_LNG)));
                place.setZoom(cursor.getInt(cursor.getColumnIndex(FIELD_ZOOM)));
                place.setStatut(cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)));
                place.setNotes(cursor.getDouble(cursor.getColumnIndex(NOTES)));
                place.setNumber(cursor.getInt(cursor.getColumnIndex("number")));



                personLinkedList.add(place);
            } while (cursor.moveToNext());
        }


        return personLinkedList;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
        onCreate(db);    }
}