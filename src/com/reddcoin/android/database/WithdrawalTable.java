package com.reddcoin.android.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WithdrawalTable {

	// Database table
	public static final String TABLE_WITHDRAWAL = "withdrawal";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_AMOUNT = "amount";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_OWNER = "owner";
	public static final String COLUMN_TXID = "txid";
	public static final String COLUMN_TIMESTAMP = "timestamp";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_WITHDRAWAL + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_AMOUNT
			+ " integer not null," + COLUMN_ADDRESS + " text not null,"
			+ COLUMN_OWNER + " text not null," + COLUMN_TXID
			+ " text not null," + COLUMN_TIMESTAMP + " integer not null" + ");";

	public static void onCreate(SQLiteDatabase database) {
		Log.d("oioi", "onCreate called!");
		database.execSQL(DATABASE_CREATE);
		Log.d("oioi", "SQL executed!");
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(WithdrawalTable.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_WITHDRAWAL);
		onCreate(database);
	}
}
