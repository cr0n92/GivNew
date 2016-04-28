package com.givmed.android;

/**
 * Created by agroikos on 26/12/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "givmedData.db";

    // Table names
    private static final String TABLE_NEEDS = "need";
    private static final String TABLE_MEDS = "med";
    private static final String TABLE_DONATIONS = "donations";
    private static final String TABLE_NAMES = "names";

    // Needs Table Columns names
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PHARM_NAME = "pharmName";
    private static final String KEY_ADDR = "address";
    private static final String KEY_NEED_NAME = "needName";
    private static final String KEY_REGION = "region";

    // Meds Table Columns names
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_NAME = "name";
    private static final String KEY_EXP_DATE = "expDate";
    private static final String KEY_PRICE = "price";
    private static final String KEY_NOTES = "notes";
    private static final String KEY_STATE = "state";
    private static final String KEY_SUB = "substance";
    private static final String KEY_CATEG = "category";
    private static final String KEY_HALF_NAME = "halfName";

    // Donations Table Columns names
//    private static final String KEY_BARCODE = "barcode";
//    private static final String KEY_EXP_DATE = "expDate";
//    private static final String KEY_PRICE = "price";
//    private static final String KEY_NOTES = "notes";
//    private static final String KEY_STATE = "state";
//    private static final String KEY_SUB = "substance";
//    private static final String KEY_CATEG = "category";

    // Meds Names Table Columns names
    private static final String KEY_COUNT = "count";
    private static final String KEY_S_DATE = "sDate";

    // Table Create Statements
    private static final String CREATE_NEEDS_TABLE = "CREATE TABLE " + TABLE_NEEDS + "("
            + KEY_PHONE + " TEXT," + KEY_PHARM_NAME + " TEXT," + KEY_ADDR + " TEXT,"
            + KEY_NEED_NAME + " TEXT," + KEY_REGION + " TEXT, PRIMARY KEY (" + KEY_PHONE
            + ", " + KEY_NEED_NAME + ")" + ")";

    private static final String CREATE_MEDS_TABLE = "CREATE TABLE " + TABLE_MEDS + "("
            + KEY_BARCODE + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_EXP_DATE + " TEXT,"
            + KEY_PRICE + " TEXT," + KEY_NOTES + " TEXT," + KEY_STATE + " TEXT,"
            + KEY_SUB + " TEXT," + KEY_CATEG + " TEXT," + KEY_HALF_NAME + " TEXT" + ")";

//    private static final String CREATE_DONATIONS_TABLE = "CREATE TABLE " + TABLE_MEDS + "("
//            + KEY_BARCODE + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_EXP_DATE + " TEXT,"
//            + KEY_PRICE + " TEXT" + ")";

    private static final String CREATE_NAMES_TABLE = "CREATE TABLE " + TABLE_NAMES + "("
            + KEY_NAME + " TEXT PRIMARY KEY," + KEY_COUNT + " INTEGER," + KEY_S_DATE + " TEXT"
            + ")";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NEEDS_TABLE);
        db.execSQL(CREATE_MEDS_TABLE);
        //db.execSQL(CREATE_DONATIONS_TABLE);
        db.execSQL(CREATE_NAMES_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // An einai na kanoume update tote prepei na kanoume copy ta dedomena
        // kai meta na kanoume drop thn vash wste na mhn xa8oun
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEEDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDS);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_DONATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAMES);

        // Create tables again
        onCreate(db);
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEEDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDS);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_DONATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAMES);

        // Create tables again
        onCreate(db);
    }

    public void deleteMeds() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDS);


        db.execSQL(CREATE_MEDS_TABLE);
    }

    public void deleteNeeds() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEEDS);


        db.execSQL(CREATE_NEEDS_TABLE);
    }


    /*---------------- needs functions ----------------------------*/
    // Adding new need
    public void addNeed(Need need) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PHONE, need.getPhone());
        values.put(KEY_PHARM_NAME, need.getName());
        values.put(KEY_ADDR, need.getAddress());
        values.put(KEY_NEED_NAME, need.getNeedName());
        values.put(KEY_REGION, need.getRegion());

        // Inserting Row
        db.insert(TABLE_NEEDS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single need
//    public Need getNeed(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_NEEDS, new String[]{KEY_ID,
//                        KEY_NAME, KEY_ADDR, KEY_PH_NO}, KEY_ID + "=?",
//                new String[]{String.valueOf(id)}, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        Need need = new Need(cursor);
//        // return need
//        return need;
//    }

    // vazoume oles tis ellepseis ston adapter ta3hnomhmena eite ws pros to onoma/ousia h ws pros thn perioxh
    // epishs gyrizoume to plh8os twn elleipsewn
    public int getAllNeeds(NeedAdapter needAdapter, String orderingColumn) {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NEEDS + " ORDER BY " + orderingColumn;
        int cnt = 0;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Need need = new Need(cursor);
                needAdapter.add(need);
                cnt++;
            } while (cursor.moveToNext());
            cursor.close();
        }

        // return need list
        return cnt;
    }

    /*---------------- meds functions ----------------------------*/
    // Adding new med
    public void addMed(Medicine med, String halfName) {
        // prwta koitame an yparxei to prwto onoma sthn vash kai meta an yparxei
        // au3anoume ton metrhth alliws ftiaxnoume kainourgia eggrafh
        MedName name = this.getMedName(halfName);
        if (name == null)
            this.addMedName(new MedName(halfName, "1", med.getDate()));
        else {
            // au3anoume to count kata ena kai
            // ananewnoume thn hmeromhnia an einai mikroterh
            name.setCount("" + (Integer.parseInt(name.getCount()) + 1));
            String[] palio = name.getDate().split("/");
            String[] neo = med.getDate().split("/");

            int monthP = Integer.parseInt(palio[0]);
            int yearP = Integer.parseInt(palio[1]);
            int monthN = Integer.parseInt(neo[0]);
            int yearN = Integer.parseInt(neo[1]);

            if (yearN * 12 + monthN < yearP * 12 + monthP)
                name.setDate(monthN + "/" + yearN);

            this.updateMedName(name);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BARCODE, med.getBarcode());
        values.put(KEY_NAME, med.getName()); // Med Name
        values.put(KEY_EXP_DATE, med.getDate()); // Med Phone Number
        values.put(KEY_PRICE, med.getPrice()); // Med Address
        values.put(KEY_HALF_NAME, halfName);

        // Inserting Row
        db.insert(TABLE_MEDS, null, values);
        db.close(); // Closing database connection
    }

    // Updating single med
//    public int updateMed(Medicine med) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, med.getName());
//        values.put(KEY_PH_NO, med.getPhone());
//
//        // updating row
//        return db.update(TABLE_MEDS, values, KEY_ID + " = ?",
//                new String[]{String.valueOf(med.getID())});
//    }

    // Deleting single med
    public void deleteMed(Medicine med, String halfName) {
        MedName name = this.getMedName(halfName);

        // an to count tou antistoixou onomatos ginei mhden tote afairoume thn eggrafh
        // kai apo ton pinaka me ta onomata, alliws apla meiwnoume to count
        if (Integer.getInteger(name.getCount()) == 1)
            this.deleteMedName(name);
        else {
            name.setCount("" + (Integer.parseInt(name.getCount()) - 1));
            this.updateMedName(name);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_MEDS, KEY_BARCODE + " = ?",
                new String[]{med.getBarcode()});
        db.close();
    }

    // Getting single med
    public Medicine getMed(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MEDS, new String[]{KEY_BARCODE,
                        KEY_NAME, KEY_EXP_DATE, KEY_PRICE}, KEY_BARCODE + "=?",
                new String[]{barcode}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Medicine med = new Medicine(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return med
        return med;
    }

    // Getting all Meds and adding them to the adapter and also
    // adding the prices and returning the summary
    public void getAllMedsToAdapter(String name, MedicineAdapter medAdapter) {
        //List<Medicine> medList = new ArrayList<Medicine>();
        String selectQuery = "SELECT  * FROM " + TABLE_MEDS + " WHERE " + KEY_HALF_NAME + " == '" + name + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to adapter
        if (cursor.moveToFirst()) {
            do {
                Medicine med = new Medicine();
                med.setBarcode(cursor.getString(0));
                med.setName(cursor.getString(1));
                med.setDate(cursor.getString(2));
                med.setPrice(cursor.getString(3));

                // Adding med to adapter
                medAdapter.add(med);
            } while (cursor.moveToNext());
        }
    }

    /*---------------- donations functions ----------------------------*/
    // Adding new donation
//    public void addMed(Medicine med) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_BARCODE, med.getBarcode());
//        values.put(KEY_NAME, med.getName()); // Med Name
//        values.put(KEY_EXP_DATE, med.getDate()); // Med Phone Number
//        values.put(KEY_PRICE, med.getPrice()); // Med Address
//
//        // Inserting Row
//        db.insert(TABLE_MEDS, null, values);
//        db.close(); // Closing database connection
//    }
//
//    // Getting single med
//    public Medicine getMed(String barcode) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_MEDS, new String[]{KEY_BARCODE,
//                        KEY_NAME, KEY_EXP_DATE, KEY_PRICE}, KEY_BARCODE + "=?",
//                new String[]{barcode}, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        Medicine med = new Medicine(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
//        // return med
//        return med;
//    }
//
//    // Getting all Meds and adding them to the adapter and also
//    // adding the prices and returning the summary
//    public Double getAllMedsToAdapter(MedicineAdapter medAdapter) {
//        //List<Medicine> medList = new ArrayList<Medicine>();
//        Double sum = 0.0;
//
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + TABLE_MEDS;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to adapter
//        if (cursor.moveToFirst()) {
//            do {
//                Medicine med = new Medicine();
//                med.setBarcode(cursor.getString(0));
//                med.setName(cursor.getString(1));
//                med.setDate(cursor.getString(2));
//
//                String price = cursor.getString(3);
//                sum += Double.parseDouble(price);
//                med.setPrice(price);
//                // Adding med to adapter
//                medAdapter.add(med);
//            } while (cursor.moveToNext());
//        }
//
//        // return med sum price
//        return sum;
//    }
//
//    // Getting meds Count
//    public int getMedsCount() {
//        String countQuery = "SELECT  * FROM " + TABLE_MEDS;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();
//
//        // return count
//        return cursor.getCount();
//    }
//
//    // Updating single med
////    public int updateMed(Medicine med) {
////        SQLiteDatabase db = this.getWritableDatabase();
////
////        ContentValues values = new ContentValues();
////        values.put(KEY_NAME, med.getName());
////        values.put(KEY_PH_NO, med.getPhone());
////
////        // updating row
////        return db.update(TABLE_MEDS, values, KEY_ID + " = ?",
////                new String[]{String.valueOf(med.getID())});
////    }
//
//    // Deleting single med
//    public void deleteMed(Medicine med) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        db.delete(TABLE_MEDS, KEY_BARCODE + " = ?",
//                new String[]{String.valueOf(med.getBarcode())});
//        db.close();
//    }

    /*---------------- names functions ----------------------------*/
    // Adding new name
    public void addMedName(MedName name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name.getName()); // Med Half Name
        values.put(KEY_COUNT, name.getCount()); // Med Name count
        values.put(KEY_S_DATE, name.getDate()); // Med Name quickest expiration

        // Inserting Row
        db.insert(TABLE_NAMES, null, values);
        db.close(); // Closing database connection
    }

    // Updating single med
    public void updateMedName(MedName name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COUNT, name.getCount());
        values.put(KEY_S_DATE, name.getDate());

        // updating row
        db.update(TABLE_NAMES, values, KEY_NAME + " = ?", new String[]{name.getName()});
        db.close();
    }

    // Deleting single med
    public void deleteMedName(MedName name) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAMES, KEY_NAME + " = ?",
                new String[]{name.getName()});
        db.close();
    }

    // Getting single med name
    public MedName getMedName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        MedName medname = null;

        Cursor cursor = db.query(TABLE_NAMES, new String[]{KEY_NAME,
                        KEY_COUNT, KEY_S_DATE}, KEY_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            medname = new MedName(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            cursor.close();
        }
        db.close();

        return medname;
    }

    // Getting all Med Names and adding them to the adapter and also
    // adding the counts and returng the total count
    public int getAllNamesToAdapter(MedNameAdapter medNameAdapter) {
        //List<MedName> medList = new ArrayList<MedName>();
        int count = 0;

        String selectQuery = "SELECT  * FROM " + TABLE_NAMES + " ORDER BY " + KEY_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        // looping through all rows and adding to adapter
        if (cursor.moveToFirst()) {
            do {
                MedName name = new MedName();
                name.setName(cursor.getString(0));

                String cnt = cursor.getString(1);
                count += Integer.parseInt(cnt);
                name.setCount(cnt);
                name.setDate(cursor.getString(2));

                // Adding med name to adapter
                medNameAdapter.add(name);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return count;
    }
}