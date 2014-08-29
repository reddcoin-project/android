package com.reddcoin.android.handlers;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
 






import com.reddcoin.android.model.Contact;
import com.reddcoin.android.model.Withdrawal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
public class WithdrawalsDatabaseHandler extends SQLiteOpenHelper {
 
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "withdrawalsManager";
 
    // Contacts table name
    private static final String TABLE_WITHDRAWALS = "withdrawal";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_TXID = "txid";
    private static final String KEY_TIMESTAMP = "timestamp";
 
    public WithdrawalsDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_WITHDRAWALS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_AMOUNT + " INTEGER,"
                + KEY_ADDRESS + " TEXT,"  + KEY_OWNER + " TEXT," + KEY_TXID + " TEXT," + KEY_TIMESTAMP + " INTEGER," + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WITHDRAWALS);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new contact
    void addWithdrawal(Withdrawal withdrawal) {
    	Log.d("oioioioioi", "calling addWithdrawal");
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_AMOUNT, withdrawal.getAmount()); 
        values.put(KEY_ADDRESS, withdrawal.getAddress()); 
        values.put(KEY_OWNER, withdrawal.getOwner()); 
        values.put(KEY_TXID, withdrawal.getTxid());
        values.put(KEY_TIMESTAMP, withdrawal.getTimestamp());
 
        // Inserting Row
        db.insert(TABLE_WITHDRAWALS, null, values);
        db.close(); // Closing database connection
    }
 
    // Getting single contact
    Withdrawal getWithdrawal(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_WITHDRAWALS, new String[] { KEY_ID,
                KEY_AMOUNT, KEY_ADDRESS, KEY_OWNER, KEY_TXID, KEY_TIMESTAMP }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        Withdrawal withdrawal = new Withdrawal(Integer.parseInt(cursor.getString(0)), null, cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getDouble(1), cursor.getLong(5));
        // return contact
        return withdrawal;
    }
     
    // Getting All Contacts
    public List<Withdrawal> getAllWithdrawals() {
        List<Withdrawal> withdrawalList = new ArrayList<Withdrawal>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_WITHDRAWALS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Withdrawal withdrawal = new Withdrawal(Integer.parseInt(cursor.getString(0)), null, cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getDouble(1), cursor.getLong(5));
                // Adding contact to list
                withdrawalList.add(withdrawal);
            } while (cursor.moveToNext());
        }
 
        // return contact list
        return withdrawalList;
    }
 
    // Updating single contact
    public int updateWithdrawal(Withdrawal withdrawal) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_AMOUNT, withdrawal.getAmount());
        values.put(KEY_ADDRESS, withdrawal.getAddress());
        values.put(KEY_OWNER, withdrawal.getOwner());
        values.put(KEY_TXID, withdrawal.getTxid());
        values.put(KEY_TIMESTAMP, withdrawal.getTimestamp());
 
        // updating row
        return db.update(TABLE_WITHDRAWALS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(withdrawal.getId()) });
    }
 
    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WITHDRAWALS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }
 
 
    // Getting contacts Count
    public int getWithdrawalsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_WITHDRAWALS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
 
}
