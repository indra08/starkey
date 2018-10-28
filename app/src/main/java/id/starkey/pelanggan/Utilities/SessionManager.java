package id.starkey.pelanggan.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;
	
	// Editor for Shared preferences
	Editor editor;
	
	// Context
	Context context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = "login";
	
	// All Shared Preferences Keys
	private static final String TAG_ID = "idUser";
	public static final String TAG_NAMA = "namaUser";
	public static final String TAG_NAMA_BLK = "namablkgUser";
	public static final String TAG_PHONE = "phoneUser";
	public static final String TAG_EMAIL= "emailUser";
	public static final String TAG_TOKEN= "tokenIdUser";

	// Constructor
	public SessionManager(Context context){
		this.context = context;
		pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	/**
	 * Create login session
	 * */
	/*public void createLoginSession(String uid, String nik, String nama, String username, String password, String saved, String jabatan, String exp, String level, String area, String flag){

		editor.putBoolean(IS_LOGIN, true);

		editor.putString(TAG_UID, uid); // nik GA

		editor.putString(TAG_NIK, nik);

		editor.putString(TAG_NAMA, nama);

		editor.putString(TAG_USERNAME, username);

		editor.putString(TAG_PASSWORD, password);

		editor.putString(TAG_SAVED, saved); // value is 0 or 1

		editor.putString(TAG_JAB, jabatan);

		editor.putString(TAG_EXP, exp);

		editor.putString(TAG_LEVEL, level); // DS atau SF

		editor.putString(TAG_AREA, area);

		editor.putString(TAG_FLAG, flag);
		// commit changes
		editor.commit();
	}*/
	
	/**
	 * Get stored session data
	 * */
	/*public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();
		// user uid
		user.put(TAG_UID, pref.getString(TAG_UID, ""));
		
		// user nik
		user.put(TAG_NIK, pref.getString(TAG_NIK, ""));

		user.put(TAG_NAMA, pref.getString(TAG_NAMA, ""));

		user.put(TAG_USERNAME, pref.getString(TAG_USERNAME, ""));

		user.put(TAG_PASSWORD, pref.getString(TAG_PASSWORD, ""));

		user.put(TAG_SAVED, pref.getString(TAG_SAVED, ""));

		user.put(TAG_JAB, pref.getString(TAG_JAB, ""));

		user.put(TAG_EXP, pref.getString(TAG_EXP, ""));

		user.put(TAG_LEVEL, pref.getString(TAG_LEVEL, ""));

		user.put(TAG_AREA, pref.getString(TAG_AREA, ""));

		user.put(TAG_FLAG, pref.getString(TAG_FLAG, ""));
		// return user
		return user;
	}*/

	public String getID(){
		return pref.getString(TAG_ID, "");
	}

	public String getNama(){
		return pref.getString(TAG_NAMA, "");
	}

	public String getNamaBlk(){
		return pref.getString(TAG_NAMA_BLK, "");
	}

	public String getPhone(){
		return pref.getString(TAG_PHONE, "");
	}

	public String getEmail(){
		return pref.getString(TAG_EMAIL, "");
	}

	public String getToken(){
		return pref.getString(TAG_TOKEN, "");
	}

	/**
	 * Clear session details
	 * */
	/*public void logoutUser(Intent logoutIntent){

		// Clearing all data from Shared Preferences
		try {
			editor.clear();
			editor.commit();
		}catch (Exception e){
			e.printStackTrace();
		}

		logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(logoutIntent);
		((Activity)context).finish();
		((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}*/
}
