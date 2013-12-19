package com.abbey.zephyr.provider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class VitalsProvider extends ContentProvider {

	static final String PROVIDER_NAME = "com.abbey.zephyr.provider";
	static final String URL = "content://" + PROVIDER_NAME + "/vitals";
	public static final Uri CONTENT_URI = Uri.parse(URL);

	public static final String _ID = "_id";
	public static final String HR = "heartRate";
	public static final String RR = "respRate";
	public static final String ST = "skinTemp";
	public static final String PO = "posture";
	public static final String PA = "peakAcce";
	public static final String TS = "timeStamp";
	public static final String SY = "notSync";

	private static HashMap<String, String> VITALS_PROJECTION_MAP;

	static final int VITALS = 1;
	static final int VITAL_ID = 2;
	
	static final UriMatcher uriMatcher;
	   static{
	      uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	      uriMatcher.addURI(PROVIDER_NAME, "vitals", VITALS);
	      uriMatcher.addURI(PROVIDER_NAME, "vitals/#", VITAL_ID);
	   }
	   
	   private SQLiteDatabase db;
	   static final String DATABASE_NAME = "zephyr";
	   static final String VITALS_TABLE_NAME = "vitals";
	   static final int DATABASE_VERSION = 9;
	   static final String CREATE_DB_TABLE = 
	      " CREATE TABLE " + VITALS_TABLE_NAME +
	      " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
	      " heartRate TEXT, " +
	      " respRate TEXT, " +
	      " skinTemp TEXT, " +
	      " posture TEXT, " +
	      " peakAcce TEXT, " +
	      " timeStamp TEXT, " +
	      " notSync TEXT);";
	   
	   private static class DatabaseHelper extends SQLiteOpenHelper {
	       DatabaseHelper(Context context){
	          super(context, DATABASE_NAME, null, DATABASE_VERSION);
	       }

	       @Override
	       public void onCreate(SQLiteDatabase db)
	       {
	          db.execSQL(CREATE_DB_TABLE);
	       }
	       
	       @Override
	       public void onUpgrade(SQLiteDatabase db, int oldVersion, 
	                             int newVersion) {
	          db.execSQL("DROP TABLE IF EXISTS " +  VITALS_TABLE_NAME);
	          onCreate(db);
	       }
	   }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int count = 0;

	      switch (uriMatcher.match(uri)){
	      case VITALS:
	         count = db.delete(VITALS_TABLE_NAME, selection, selectionArgs);
	         break;
	      case VITAL_ID:
	         String id = uri.getPathSegments().get(1);
	         count = db.delete( VITALS_TABLE_NAME, _ID +  " = " + id + 
	                (!TextUtils.isEmpty(selection) ? " AND (" + 
	                selection + ')' : ""), selectionArgs);
	         break;
	      default: 
	         throw new IllegalArgumentException("Unknown URI " + uri);
	      }
	      
	      getContext().getContentResolver().notifyChange(uri, null);
	      return count;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (uriMatcher.match(uri)){
	      case VITALS:
	         return "vnd.android.cursor.dir/vnd.abbey.vitals";
	      case VITAL_ID:
	         return "vnd.android.cursor.item/vnd.abbey.vitals";
	      default:
	         throw new IllegalArgumentException("Unsupported URI: " + uri);
	      }
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		long rowID = db.insert(	VITALS_TABLE_NAME, "", values);
	      if (rowID > 0)
	      {
	         Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
	         getContext().getContentResolver().notifyChange(_uri, null);
	         return _uri;
	      }
	      throw new SQLException("Failed to add a record into " + uri);
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		Context context = getContext();
	      DatabaseHelper dbHelper = new DatabaseHelper(context);
	      db = dbHelper.getWritableDatabase();
	      return (db == null)? false:true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	      qb.setTables(VITALS_TABLE_NAME);
	      switch (uriMatcher.match(uri)) {
	      case VITALS:
	         qb.setProjectionMap(VITALS_PROJECTION_MAP);
	         break;
	      case VITAL_ID:
	         qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
	         break;
	      default:
	         throw new IllegalArgumentException("Unknown URI " + uri);
	      }
	      if (sortOrder == null || sortOrder == ""){
	         sortOrder = _ID;
	      }
	      Cursor c = qb.query(db,	projection,	selection, selectionArgs, 
	                          TS, null, sortOrder);
	      c.setNotificationUri(getContext().getContentResolver(), uri);

	      return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		int count = 0;
	      
	      switch (uriMatcher.match(uri)){
	      case VITALS:
	         count = db.update(VITALS_TABLE_NAME, values, 
	                 selection, selectionArgs);
	         break;
	      case VITAL_ID:
	         count = db.update(VITALS_TABLE_NAME, values, _ID + 
	                 " = " + uri.getPathSegments().get(1) + 
	                 (!TextUtils.isEmpty(selection) ? " AND (" +
	                 selection + ')' : ""), selectionArgs);
	         break;
	      default: 
	         throw new IllegalArgumentException("Unknown URI " + uri );
	      }
	      getContext().getContentResolver().notifyChange(uri, null);
	      return count;
	}

}
