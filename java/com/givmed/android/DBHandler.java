package com.givmed.android;

/**
 * Created by agroikos on 26/12/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;


public class DBHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "givmedData.db";

    // Table names
    private static final String TABLE_NEEDS = "need";
    private static final String TABLE_PHARMACIES = "pharmacies";
    private static final String TABLE_MEDS = "med";
    private static final String TABLE_EOFCODES = "eofcodes";
    private static final String TABLE_DONATIONS = "donations";
    private static final String TABLE_DONE_DONATIONS = "doneDnations";
    private static final String TABLE_NAMES = "names";

    // Needs Table Columns names
    private static final String KEY_PHAR_PHONE = "pharPhone";
    private static final String KEY_NEED_NAME = "needName";

    // Pharmacies Table Columns names
    // private static final String KEY_PHAR_PHONE = "pharPhone";
    private static final String KEY_PHAR_ADDRESS = "pharAddress";
    private static final String KEY_PHAR_HOURS = "pharHours";
    private static final String KEY_PHAR_NAME = "pharName";
    private static final String KEY_PHAR_NAME_GEN = "pharNameGen";
    private static final String KEY_PHAR_REG = "pharReg";

    // Meds Table Columns names
    private static final String KEY_BARCODE = "barcode";
    private static final String KEY_EOFCODE = "eofcode";
    private static final String KEY_EXP_DATE = "expDate";
    private static final String KEY_NOTES = "notes";
    private static final String KEY_STATE = "state";
    private static final String KEY_STATUS = "status";
    private static final String KEY_HALF_NAME = "halfName";

    // Eofcodes Table Columns names
    // private static final String KEY_EOFCODE = "eofcode";
    private static final String KEY_NAME = "name";
    private static final String KEY_PRICE = "price";
    private static final String KEY_SUB = "substance";
    private static final String KEY_CATEG = "category";
    private static final String KEY_COUNT = "count";

    // Donations Table Columns names
    // private static final String KEY_BARCODE = "barcode";
    // private static final String KEY_PHAR_PHONE = "pharPhone";
    private static final String KEY_DATE1 = "date1";
    private static final String KEY_DATE2 = "date2";
    private static final String KEY_DATE3 = "date3";
    private static final String KEY_VOLUNTEER = "volunteer";
    private static final String KEY_PICK_ADDR = "pickAddr";

    // Done Donations Table Columns names
    private static final String KEY_ID = "id";
    // private static final String KEY_PRICE = "price";
    // private static final String KEY_NAME = "name";
    // private static final String KEY_PHAR_NAME = "pharName";
    private static final String KEY_DATE = "date";

    // Meds Names Table Columns names
    // private static final String KEY_NAME = "name";
    // private static final String KEY_COUNT = "count";
    private static final String KEY_S_DATE = "sDate";

    // Table Create Statements
    private static final String CREATE_NEEDS_TABLE = "CREATE TABLE " + TABLE_NEEDS + "("
            + KEY_PHAR_PHONE + " TEXT," + KEY_NEED_NAME + " TEXT, PRIMARY KEY (" + KEY_PHAR_PHONE + ", "
            + KEY_NEED_NAME + ")" + ")";

    private static final String CREATE_MEDS_TABLE = "CREATE TABLE " + TABLE_MEDS + "("
            + KEY_BARCODE + " TEXT PRIMARY KEY," + KEY_EOFCODE + " TEXT," + KEY_EXP_DATE + " TEXT,"
            + KEY_NOTES + " TEXT," + KEY_STATE + " TEXT," + KEY_STATUS + " TEXT,"
            + KEY_HALF_NAME + " TEXT" + ")";

    private static final String CREATE_PHARMACIES_TABLE = "CREATE TABLE " + TABLE_PHARMACIES + "("
            + KEY_PHAR_PHONE + " TEXT PRIMARY KEY," + KEY_PHAR_ADDRESS + " TEXT," + KEY_PHAR_HOURS + " TEXT,"
            + KEY_PHAR_NAME + " TEXT," + KEY_PHAR_NAME_GEN + " TEXT," + KEY_PHAR_REG + " TEXT" + ")";

    private static final String CREATE_EOFCODES_TABLE = "CREATE TABLE " + TABLE_EOFCODES + "("
            + KEY_EOFCODE + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_PRICE + " TEXT,"
            + KEY_SUB + " TEXT," + KEY_CATEG + " TEXT," + KEY_COUNT + " TEXT" + ")";

    private static final String CREATE_DONATIONS_TABLE = "CREATE TABLE " + TABLE_DONATIONS + "("
            + KEY_BARCODE + " TEXT PRIMARY KEY," + KEY_PHAR_PHONE + " TEXT," + KEY_DATE1 + " TEXT,"
            + KEY_DATE2 + " TEXT," + KEY_DATE3 + " TEXT," + KEY_VOLUNTEER + " TEXT,"
            + KEY_PICK_ADDR + " TEXT" + ")";

    private static final String CREATE_DONE_DONATIONS_TABLE = "CREATE TABLE " + TABLE_DONE_DONATIONS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + KEY_PRICE + " TEXT,"
            + KEY_NAME + " TEXT," + KEY_PHAR_NAME + " TEXT," + KEY_DATE + " TEXT" + ")";

    private static final String CREATE_NAMES_TABLE = "CREATE TABLE " + TABLE_NAMES + "("
            + KEY_NAME + " TEXT PRIMARY KEY," + KEY_COUNT + " TEXT," + KEY_S_DATE + " TEXT" + ")";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NEEDS_TABLE);
        db.execSQL(CREATE_PHARMACIES_TABLE);
        db.execSQL(CREATE_MEDS_TABLE);
        db.execSQL(CREATE_EOFCODES_TABLE);
        db.execSQL(CREATE_DONATIONS_TABLE);
        db.execSQL(CREATE_DONE_DONATIONS_TABLE);
        db.execSQL(CREATE_NAMES_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // An einai na kanoume update tote prepei na kanoume copy ta dedomena
        // kai meta na kanoume drop thn vash wste na mhn xa8oun
        // Drop older table if existed

        // Create tables again
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEEDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHARMACIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EOFCODES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DONATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DONE_DONATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAMES);

        // Create tables again
        onCreate(db);
    }

    public void deleteMeds() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDS);
        db.execSQL(CREATE_MEDS_TABLE);
    }

    public void deletePharmacies() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHARMACIES);
        db.execSQL(CREATE_PHARMACIES_TABLE);
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
        values.put(KEY_PHAR_PHONE, need.getPhone());
        values.put(KEY_NEED_NAME, need.getNeedName());

        db.insert(TABLE_NEEDS, null, values);
        db.close();
    }

    // vazoume oles tis ellepseis ston adapter ta3hnomhmena eite ws pros to onoma/ousia h ws pros thn perioxh
    // epishs gyrizoume to plh8os twn elleipsewn
    public int getAllNeeds(NeedAdapter needAdapter, String orderingColumn) {
        SQLiteDatabase db = this.getWritableDatabase();
        int cnt = 0;

        String selectQuery = "SELECT " + KEY_PHAR_PHONE + "," + KEY_NEED_NAME + "," + KEY_PHAR_REG
                + " FROM " + TABLE_NEEDS + " NATURAL JOIN " + TABLE_PHARMACIES + " ORDER BY " + orderingColumn;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Need need = new Need(cursor);
                needAdapter.add(need);
                cnt++;
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return cnt;
    }

    /*---------------- pharmacies functions ----------------------------*/
    // Adding new pharmacy
    public void addPharmacy(String phone, String address, String hours, String name, String nameGen, String region) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PHAR_PHONE, phone);
        values.put(KEY_PHAR_ADDRESS, address);
        values.put(KEY_PHAR_HOURS, hours);
        values.put(KEY_PHAR_NAME, name);
        values.put(KEY_PHAR_NAME_GEN, nameGen);
        values.put(KEY_PHAR_REG, region);

        db.insert(TABLE_PHARMACIES, null, values);
        db.close();
    }

    //vazoume pharmName h pharmNameGen kai gurnaei ola ta stoixeia tou farmakeiou
    public void getPharmacy(String name, String[] arr) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_PHARMACIES + " WHERE " + KEY_PHAR_NAME_GEN + " = '" + name + "' OR "
                + KEY_PHAR_NAME + " = '" + name + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            arr[0] = cursor.getString(0);
            arr[1] = cursor.getString(1);
            arr[2] = cursor.getString(2);
            arr[3] = cursor.getString(3);
            arr[4] = cursor.getString(4);
            arr[5] = cursor.getString(5);
            cursor.close();
        }
        db.close();
    }

    //get all pharmacies that have a specific need
    public void getPharmaciesForNeed(RadioGroup group, Context conte, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        //List<String> list = new ArrayList<String>();
        int i = 0;

        String selectQuery = "SELECT " + KEY_PHAR_NAME + ", " + KEY_PHAR_REG + " FROM " + TABLE_NEEDS +
                " NATURAL JOIN " + TABLE_PHARMACIES + " WHERE " + KEY_NEED_NAME + " = '" + name + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                //list.add(cursor.getString(0) + ", " + cursor.getString(1));
                RadioButton rdbtn = new RadioButton(conte);
                rdbtn.setId(i++);
                rdbtn.setText(cursor.getString(0) + ", " + cursor.getString(1));
                rdbtn.setTextColor(R.color.black);
                group.addView(rdbtn);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        //return list;
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

        int eofcodes = this.getEofcodeCnt(med.getEofcode());
        if (eofcodes < 0)
            this.addEofcode(med);
        else {
            this.updateEofcode(med.getEofcode(), eofcodes + 1);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BARCODE, med.getBarcode());
        values.put(KEY_EOFCODE, med.getEofcode());
        values.put(KEY_EXP_DATE, med.getDate());
        values.put(KEY_NOTES, med.getNotes());
        values.put(KEY_STATE, med.getState());
        values.put(KEY_HALF_NAME, halfName);
        values.put(KEY_STATUS, med.getStatus());

        db.insert(TABLE_MEDS, null, values);
        db.close();
    }

    // Updating single med
    public int updateMed(Medicine med) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NOTES, med.getNotes());
        values.put(KEY_STATE, med.getState());
        values.put(KEY_STATUS, med.getStatus());

        // updating row
        return db.update(TABLE_MEDS, values, KEY_BARCODE + " = ?",
                new String[]{med.getBarcode()});
    }

    // Updating single med status
    public int updateMedForDonation(String barcode, String forDonation) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, forDonation);

        return db.update(TABLE_MEDS, values, KEY_BARCODE + " = ?", new String[]{barcode});
    }

    // check an den uparxei elleipsh gia ayto to farmako ki an einai to teleytaio farmako me idio onoma kai 'Y' h 'SY'
    // an nai tote kanoume unsubscribe

    public boolean checkMedSubscribe(String Halfname,boolean subscribe) {

        Object [] ret;

        ret = this.matchExists(Halfname);
        if (((int) ret[0]) != -1) return false;

        SQLiteDatabase db = this.getWritableDatabase();





        String selectQuery = "SELECT " + KEY_BARCODE + " FROM " + TABLE_MEDS + " WHERE " + KEY_HALF_NAME
                + " == '" + Halfname + "' AND ( " + KEY_STATUS + " == 'Y' OR " + KEY_STATUS + " == 'SY')";
        Cursor cursor = db.rawQuery(selectQuery, null);

        //uparxei hdh allo farmako pros amesh dwrea ara exei ginei hdh subscribe
        if (subscribe)
            if (cursor.getCount() > 0) return false;
        //mporei na svhsei ena farmako alla na xei ki alla ara dn kanoume unsubscribe
        else
            if (cursor.getCount() > 1) return false;


        return true;

    }



    public ArrayList<String> updateMedStatus(String three_months_later) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> topics = new ArrayList<String>();
        ContentValues values = new ContentValues();


        //query pou pairnoume ta kleista siropia me SY kai ta alla me Y pou lhgoun se 3 mhnes
        String selectQuery = "SELECT " + KEY_BARCODE + "," + KEY_HALF_NAME + "," + KEY_STATUS + " FROM " + TABLE_MEDS
                + " WHERE (" + KEY_STATUS + " = 'B' OR ("+ KEY_STATUS + " = 'SB' AND " + KEY_STATE + " = 'C')) AND "
                + KEY_EXP_DATE + "= '" + three_months_later + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) do {

            String selectQuery1 = "SELECT " + KEY_PHAR_PHONE + " FROM " + TABLE_NEEDS
                    + " WHERE " + KEY_NEED_NAME + " = '" + cursor.getString(1) + "'";
            Cursor cursor1 = db.rawQuery(selectQuery1, null);
            if (cursor1.getCount() > 0) {
                if (cursor1.getCount() == 1) {
                    cursor.moveToFirst();
                    ContentValues values1 = new ContentValues();
                    values1.put(KEY_BARCODE,cursor.getString(0));
                    values1.put(KEY_PHAR_PHONE, cursor1.getString(0));
                    values1.put(KEY_DATE1, ";");
                    values1.put(KEY_DATE2, ";");
                    values1.put(KEY_DATE3, ";");
                    values1.put(KEY_VOLUNTEER, "A");
                    values1.put(KEY_PICK_ADDR, ";");

                    db.insert(TABLE_DONATIONS, null, values1);
                } else {
                    ContentValues values1 = new ContentValues();
                    values1.put(KEY_BARCODE, cursor.getString(0));
                    values1.put(KEY_PHAR_PHONE, ";");
                    values1.put(KEY_DATE1, ";");
                    values1.put(KEY_DATE2, ";");
                    values1.put(KEY_DATE3, ";");
                    values1.put(KEY_VOLUNTEER, "Α");
                    values1.put(KEY_PICK_ADDR, ";");

                    db.insert(TABLE_DONATIONS, null, values1);
                }
            }

            else {
               topics.add(cursor.getString(1));
            }
            if (cursor.getString(2).equals("SB"))
                values.put(KEY_STATUS, "SY");
            else
                values.put(KEY_STATUS, "Y");
            db.update(TABLE_MEDS, values, KEY_BARCODE + " = ? ", new String[]{cursor.getString(0)});
            cursor1.close();

        } while (cursor.moveToNext());


        cursor.close();
        db.close();
        return topics;
    }

    // Deleting single med
    public void deleteMed(Medicine med, String halfName) {
        MedName name = this.getMedName(halfName);
        // an to count tou antistoixou onomatos ginei mhden tote afairoume thn eggrafh
        // kai apo ton pinaka me ta onomata, alliws apla meiwnoume to count
        if (name.getCount().equals("1"))
            this.deleteMedName(name);
        else {
            name.setCount("" + (Integer.parseInt(name.getCount()) - 1));
            this.updateMedName(name);
        }

        int eofcodes = this.getEofcodeCnt(med.getEofcode());
        if (eofcodes == 1)
            this.deleteEofcode(med.getEofcode());
        else {
            this.updateEofcode(med.getEofcode(), eofcodes - 1);
        }

        this.deleteProgDonation(med.getBarcode());

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_MEDS, KEY_BARCODE + " = ?", new String[]{med.getBarcode()});
        db.close();
    }

    // Getting single med
    public Medicine getMed(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Medicine med = null;

        Cursor cursor = db.query(TABLE_MEDS, new String[]{KEY_BARCODE,
                        KEY_EOFCODE, KEY_EXP_DATE, KEY_NOTES, KEY_STATE, KEY_STATUS}, KEY_BARCODE + "=?",
                new String[]{barcode}, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            Cursor cursor2 = db.query(TABLE_EOFCODES, new String[]{KEY_EOFCODE,
                            KEY_NAME, KEY_PRICE, KEY_SUB, KEY_CATEG}, KEY_EOFCODE + "=?",
                    new String[]{cursor.getString(1)}, null, null, null, null);

            if (cursor2 != null && cursor2.getCount() > 0) {
                cursor2.moveToFirst();
                med = new Medicine(cursor.getString(0), cursor2.getString(0), cursor2.getString(1), cursor.getString(2),
                        cursor2.getString(2), cursor.getString(3), cursor.getString(4), cursor2.getString(3),
                        cursor2.getString(4), cursor.getString(5));

                cursor2.close();
            }
            cursor.close();
        }
        db.close();

        return med;
    }

    // get single med barcode from name
    public String getMedBarcodeByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String barcode = null;

        Cursor cursor = db.query(TABLE_MEDS, new String[]{KEY_BARCODE}, KEY_HALF_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            barcode = cursor.getString(0);
            cursor.close();
        }
        db.close();

        return barcode;
    }

    // Getting all Meds and adding them to the adapter
    public void getAllMedsToAdapter(String name, MedicineAdapter medAdapter) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT " + KEY_BARCODE + " FROM " + TABLE_MEDS + " WHERE " + KEY_HALF_NAME + " == '" + name + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                medAdapter.add(cursor.getString(0));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
    }

	// Getting all Unknown Meds and adding them to the adapter
    public boolean getUnknownMedsToAdapter(BlueRedAdapter brAdapter) {
        boolean hasUnknown = false;
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT " + KEY_BARCODE + "," + KEY_HALF_NAME + "," + KEY_STATUS + " FROM " + TABLE_MEDS
                + " WHERE " + KEY_STATUS + " == 'U' OR " + KEY_STATUS + " == 'SU' ";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            hasUnknown = true;
            do {
                brAdapter.add(new BlueRedItem(cursor.getString(0), cursor.getString(1), cursor.getString(2).equals("SU"), R.drawable.ic_tick_in_circle_gray));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return hasUnknown;
    }

	public void printAllMeds() {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_NAMES;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Log.e("Barcode", "Name " + cursor.getString(0) + " Count " + cursor.getString(1) + " Date " + cursor.getString(2));

                //medAdapter.add(med);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
    }

    // Updating and matching
    public int updateMedAndMatch(BlueRedItem med) {
        SQLiteDatabase db = this.getWritableDatabase();
        int ret = 0;

        ContentValues values = new ContentValues();
        switch (med.getStatus()) {
            case BlueRedItem.gray:
                values.put(KEY_STATUS, "N");
                break;
            case BlueRedItem.blue:
                String name = med.getName();
                //query : select * from Needs where need_name=name gia na paroume ola ta farmakeia pou exoun elleipsh
                //an gurisei keno prepei na kanoume subscribe sto antistoixo topic(enallaktika koitame an uparxei idio onoma
                //sto Meds->Half_Name kai an oxi tote kanoume subscribe)

                String selectQuery = "SELECT " + KEY_PHAR_PHONE + " FROM " + TABLE_NEEDS
                        + " WHERE " + KEY_NEED_NAME + " = '" + name + "'";
                Cursor cursor = db.rawQuery(selectQuery, null);

                if (cursor.getCount() > 0) {
                    ret = 1;
                    if (cursor.getCount() == 1) {
                        cursor.moveToFirst();
//                        String selectQuery1 = "SELECT " + KEY_PHAR_NAME + " FROM " + TABLE_PHARMACIES + " WHERE " + KEY_PHAR_PHONE + " = " + cursor.getString(0);
//                        Cursor cursor1 = db.rawQuery(selectQuery1, null);
                        ContentValues values1 = new ContentValues();
                        values1.put(KEY_BARCODE, med.getBarcode());
                        values1.put(KEY_PHAR_PHONE, cursor.getString(0));
                        values1.put(KEY_DATE1, ";");
                        values1.put(KEY_DATE2, ";");
                        values1.put(KEY_DATE3, ";");
                        values1.put(KEY_VOLUNTEER, "A");
                        values1.put(KEY_PICK_ADDR, ";");

                        db.insert(TABLE_DONATIONS, null, values1);
                    } else {
                        ContentValues values1 = new ContentValues();
                        values1.put(KEY_BARCODE, med.getBarcode());
                        values1.put(KEY_PHAR_PHONE, ";");
                        values1.put(KEY_DATE1, ";");
                        values1.put(KEY_DATE2, ";");
                        values1.put(KEY_DATE3, ";");
                        values1.put(KEY_VOLUNTEER, "Α");
                        values1.put(KEY_PICK_ADDR, ";");

                        db.insert(TABLE_DONATIONS, null, values1);
                    }
                }
                else {
                    String selectQuery1 = "SELECT " + KEY_BARCODE + " FROM " + TABLE_MEDS + " WHERE " + KEY_HALF_NAME
                            + " == '" + name + "' OR " + KEY_STATUS + " == 'Y' OR " + KEY_STATUS + " == 'SY'";
                    Cursor cursor1 = db.rawQuery(selectQuery1, null);

                    if (cursor.getCount() == 0)
                        ret = -1;
                    else
                        ret = 0;
                    cursor1.close();
                }

                cursor.close();


                //an to ret einai 0 tote adiaforo.an einai 1 tote egine ena mats,an einai -1 tote dn vrhkame elleipsh
                //kai prepei na kanoume subscribe

                //an kaname match ftiaxnoume to antistoixo row sto Table Donations sumplhrwnontas barcode kai isws phar_phone
                //(monadiko farmakeio)

                //stis programmatismenes theloume mia sunarthsh pou na dinei ta aparaithta stoixeia apo ton pinaka donations


                values.put(KEY_STATUS, "Y");
                break;
            case BlueRedItem.red:
                values.put(KEY_STATUS, "B");
                break;
            default:
                values.put(KEY_STATUS, "U");
        }

        db.update(TABLE_MEDS, values, KEY_BARCODE + " = ?", new String[]{med.getBarcode()});


        db.close();
        return ret;
    }

    // Updating and matching
    //Epistrefei :
        //(1,phone) an uparxei mia elleipsh
        //(2,;) an uparxoun perissoteres apo mia elleipseis
        //(-1,;) an den uparxoun elleipseis
    public Object[] matchExists(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int ret = 0;
        String phone = ";";

        String selectQuery = "SELECT " + KEY_PHAR_PHONE + " FROM " + TABLE_NEEDS
                + " WHERE " + KEY_NEED_NAME + " = '" + name + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0)
            if (cursor.getCount() == 1) {
                ret = 1;
                cursor.moveToFirst();
                phone = cursor.getString(0);
            }
            else {
                ret = 2;
                phone = ";";
            }
        else ret = -1;

        cursor.close();
        db.close();

        Object[] info = {ret, phone};
        return info;
    }

    /*---------------- eofcodes functions ----------------------------*/
    // Adding new eofcode
    public void addEofcode(Medicine med) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EOFCODE, med.getEofcode());
        values.put(KEY_NAME, med.getName());
        values.put(KEY_PRICE, med.getPrice());
        values.put(KEY_SUB, med.getSubstance());
        values.put(KEY_CATEG, med.getCategory());
        values.put(KEY_COUNT, "1");

        db.insert(TABLE_EOFCODES, null, values);
        db.close();
    }

    // Updating single eofcode
    public void updateEofcode(String eofcode, int cnt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COUNT, String.valueOf(cnt));

        db.update(TABLE_EOFCODES, values, KEY_EOFCODE + " = ?", new String[]{eofcode});
        db.close();
    }

    // Updating single eofcode
    public void updateEofStuff(String eofcode, String price, String category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRICE, price);
        values.put(KEY_CATEG, category);

        db.update(TABLE_EOFCODES, values, KEY_EOFCODE + " = ?", new String[]{eofcode});
        db.close();
    }

    // Deleting single eofcode
    public void deleteEofcode(String eofcode) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_EOFCODES, KEY_EOFCODE + " = ?", new String[]{eofcode});
        db.close();
    }

    // Getting single eofcode
    public int getEofcodeCnt(String eofcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        int cnt = -1;

        Cursor cursor = db.query(TABLE_EOFCODES, new String[]{KEY_COUNT}, KEY_EOFCODE + "=?",
                new String[]{eofcode}, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            cnt = cursor.getInt(0);
            cursor.close();
        }
        db.close();

        return cnt;
    }

    /*---------------- donations functions ----------------------------*/
    public void addDonation(String barcode, String phar_phone, String date1, String date2,
                            String date3, String volunteer,String pick_addr) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BARCODE, barcode);
        values.put(KEY_PHAR_PHONE, phar_phone);
        values.put(KEY_DATE1, date1);
        values.put(KEY_DATE2, date2);
        values.put(KEY_DATE3, date3);
        values.put(KEY_VOLUNTEER, volunteer);
        values.put(KEY_PICK_ADDR, pick_addr);

        db.insert(TABLE_DONATIONS, null, values);
        db.close();
    }

    //pairnoume ta barcodes pou exoun halfName ayto pou hr8e sto push kai einai Y/SY
    //me ayta ta barcodes dhmiourgoume tis antistoixes dwrees
    public void addDonationFromPush(String halfName, String phar_phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        String selectQuery = "SELECT " + KEY_BARCODE + " FROM " + TABLE_MEDS
                + " WHERE " + KEY_HALF_NAME + " = '" + halfName + "' AND ("
                + KEY_STATUS + " == 'Y' OR " + KEY_STATUS + " == 'SY')";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                values.put(KEY_BARCODE, cursor.getString(0));
                values.put(KEY_PHAR_PHONE, phar_phone);
                values.put(KEY_DATE1, ";");
                values.put(KEY_DATE2, ";");
                values.put(KEY_DATE3, ";");
                values.put(KEY_VOLUNTEER, "A");
                values.put(KEY_PICK_ADDR, ";");
                db.insert(TABLE_DONATIONS, null, values);
            } while (cursor.moveToNext());
            cursor.close();
            db.close();
        }

    }

    public int getAllDonations(DonationAdapter donationAdapter) {
        SQLiteDatabase db = this.getWritableDatabase();
        int cnt = 0;

        String selectQuery = "SELECT " + KEY_HALF_NAME + "," + KEY_DATE1 +"," + KEY_DATE2 + ","
                + KEY_DATE3 + "," + KEY_VOLUNTEER + "," + TABLE_MEDS+"."+KEY_BARCODE + ","
                + KEY_PHAR_REG + "," + KEY_PHAR_NAME + " FROM " + TABLE_DONATIONS
                + " LEFT OUTER JOIN " + TABLE_PHARMACIES + " ON donations.pharPhone = pharmacies.pharPhone NATURAL JOIN "
                + TABLE_MEDS;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Donation don = new Donation(cursor.getString(0), cursor.getString(1), cursor.getString(4),
                        cursor.getString(5), (cursor.getString(6) == null) ? ";" : cursor.getString(6),
                        (cursor.getString(7) == null) ? ";" : cursor.getString(7));
                donationAdapter.add(don);
                cnt++;
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return cnt;
    }

    // Deleting single programmed donation
    public void deleteProgDonation(String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_DONATIONS, KEY_BARCODE + " = ?", new String[]{barcode});
        db.close();
    }

    // Deleting single programmed donation and updating med status
    public void deleteProgAndUpdateMed(String barcode) {
        Medicine medo = this.getMed(barcode);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (medo.getStatus().charAt(0) == 'S') values.put(KEY_STATUS, "SN");
        else values.put(KEY_STATUS, "N");

        db.delete(TABLE_DONATIONS, KEY_BARCODE + " = ?", new String[]{barcode});
        db.update(TABLE_MEDS, values, KEY_BARCODE + " = ?", new String[]{barcode});

        db.close();
    }

    // Getting a programmed donation
    public String[] getProgDonation(String barcode) {
        String[] arr = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DONATIONS, new String[]{KEY_BARCODE, KEY_PHAR_PHONE, KEY_DATE1,
                KEY_DATE2, KEY_DATE3, KEY_VOLUNTEER, KEY_PICK_ADDR}, KEY_BARCODE + "=?",
                new String[]{barcode}, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            arr = new String[7];
            arr[0] = cursor.getString(0);
            arr[1] = cursor.getString(1);
            arr[2] = cursor.getString(2);
            arr[3] = cursor.getString(3);
            arr[4] = cursor.getString(4);
            arr[5] = cursor.getString(5);
            arr[6] = cursor.getString(6);
            cursor.close();
        }
        db.close();

        return arr;
    }

    // Updating a programmed donation
    public void updateProgDonation(String barcode, String pharPhone, String date1, String date2,
                                   String date3, String volunteer, String pick_addr) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BARCODE, barcode);
        values.put(KEY_PHAR_PHONE, pharPhone);
        values.put(KEY_DATE1, date1);
        values.put(KEY_DATE2, date2);
        values.put(KEY_DATE3, date3);
        values.put(KEY_VOLUNTEER, volunteer);
        values.put(KEY_PICK_ADDR, pick_addr);

        db.update(TABLE_DONATIONS, values, KEY_BARCODE + " = ?", new String[]{barcode});
        db.close();
    }

    /*---------------- done donations functions ----------------------------*/
    // Adding new done donation
    public void addDoneDonation(String price, String name, String pharName, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRICE, price);
        values.put(KEY_NAME, name);
        values.put(KEY_PHAR_NAME, pharName);
        values.put(KEY_DATE, date);

        db.insert(TABLE_DONE_DONATIONS, null, values);
        db.close();
    }

    //diagrafei prog dwrea ,th vazei stis oloklhrwmenes kai diagrafei to farmako
    public void progToDoneDonation(String barcode, String pharName, String date , String halfName) {
        Medicine med = this.getMed(barcode);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRICE, med.getPrice());
        values.put(KEY_NAME,halfName);
        values.put(KEY_PHAR_NAME, pharName);
        values.put(KEY_DATE, date);

        db.insert(TABLE_DONE_DONATIONS, null, values);
        db.close();

        this.deleteMed(med, halfName);
    }

    public int[] getAllDoneDonations(DonationAdapter donationAdapter) {
        SQLiteDatabase db = this.getWritableDatabase();
        int[] info = new int[2];
        info[0] = info[1] = 0;

        String selectQuery = "SELECT * FROM " + TABLE_DONE_DONATIONS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Donation don = new Donation(cursor.getString(2), cursor.getString(4), "", "", cursor.getString(3), "");
                donationAdapter.add(don);

                info[0]++;
                info[1] += (int) Math.round(Double.parseDouble(cursor.getString(1)));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return info;
    }

    /*---------------- names functions ----------------------------*/
    // Adding new med name
    public void addMedName(MedName name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name.getName());
        values.put(KEY_COUNT, name.getCount());
        values.put(KEY_S_DATE, name.getDate());

        db.insert(TABLE_NAMES, null, values);
        db.close();
    }

    // Updating single med name
    public void updateMedName(MedName name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_COUNT, name.getCount());
        values.put(KEY_S_DATE, name.getDate());

        db.update(TABLE_NAMES, values, KEY_NAME + " = ?", new String[]{name.getName()});
        db.close();
    }

    // Deleting single med name
    public void deleteMedName(MedName name) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAMES, KEY_NAME + " = ?", new String[]{name.getName()});
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
            medname = new MedName(cursor);
            cursor.close();
        }
        db.close();

        return medname;
    }

    // Getting all Med Names and adding them to the adapter and also
    // adding the counts and returng the total count
    public int getAllNamesToAdapter(MedNameAdapter medNameAdapter) {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = 0;

        String selectQuery = "SELECT  * FROM " + TABLE_NAMES + " ORDER BY " + KEY_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MedName name = new MedName();
                name.setName(cursor.getString(0));

                String cnt = cursor.getString(1);
                count += Integer.parseInt(cnt);
                name.setCount(cnt);
                name.setDate(cursor.getString(2));

                medNameAdapter.add(name);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return count;
    }
}