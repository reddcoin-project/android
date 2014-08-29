package com.reddcoin.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WithdrawalDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "withdrawaltable.db";
	private static final int DATABASE_VERSION = 2;

	public WithdrawalDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d("oioioi", "creating new database");
		WithdrawalTable.onCreate(database);
	}

	// Method is called during an upgrade of the database,
	// e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		WithdrawalTable.onUpgrade(database, oldVersion, newVersion);
	}

}
