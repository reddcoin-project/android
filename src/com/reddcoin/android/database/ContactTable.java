package com.reddcoin.android.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ContactTable {

	// Database table
	public static final String TABLE_CONTACT = "contact";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_OWNER = "owner";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
			+ TABLE_CONTACT
			+ "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_NAME + " text not null," 
			+ COLUMN_ADDRESS
			+ " text not null," 
			+ COLUMN_OWNER
			+ " text not null" + ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(ContactTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
		onCreate(database);
	}
}
