package id.starkey.pelanggan.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LastOrderManager {
	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "lastorder";

	// Kunci
	private static final String TAG_IDLAYANANKUNCI = "idlayanankunci";
	public static final String TAG_IDKUNCI = "idkunci";
	public static final String TAG_LATITUDE = "latitude";
	public static final String TAG_LONGITUDE = "longitude";
	public static final String TAG_ALAMAT= "alamatLengkap";
	public static final String TAG_QTY= "qty";
	public static final String TAG_KETERANGAN = "keterangan";
	public static final String TAG_BIAYAESTIMASIS = "biayaEstimasiS";
	public static final String TAG_BIAYAESTIMASI = "biayaEstimasi";
	public static final String TAG_GAMBAR = "gambarByUser";

	// Stempel
	public static final String TAG_NAMASTEMPEL = "sJenisStemp";
	public static final String TAG_UKURANSTEM = "ukuran";
	public static final String TAG_WAKTUAWAL = "waktuawal";

	// Constructor
	public LastOrderManager(Context context){
		this.context = context;
		pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void createKunciOrder(String idLayanan
			,String idKunci
			,String lati
			,String longi
			,String alamat
			,String qty
			,String keterangan
			,String total
			,String estimasi
			,String gambar){

		editor.putString(TAG_IDLAYANANKUNCI, idLayanan); // nik GA
		editor.putString(TAG_IDKUNCI, idKunci);
		editor.putString(TAG_LATITUDE, lati);
		editor.putString(TAG_LONGITUDE, longi);
		editor.putString(TAG_ALAMAT, alamat);
		editor.putString(TAG_QTY, qty);
		editor.putString(TAG_KETERANGAN, keterangan);
		editor.putString(TAG_BIAYAESTIMASIS, total);
		editor.putString(TAG_BIAYAESTIMASI, estimasi);
		editor.putString(TAG_GAMBAR, gambar);
		editor.commit();
	}

	public void createStampelOrder(String namaStempel
			,String ukuran
			,String lati
			,String longi
			,String alamat
			,String qty
			,String keterangan
			,String waktuAwal
			,String estimasi){

		editor.putString(TAG_NAMASTEMPEL, namaStempel); // nik GA
		editor.putString(TAG_UKURANSTEM, ukuran);
		editor.putString(TAG_LATITUDE, lati);
		editor.putString(TAG_LONGITUDE, longi);
		editor.putString(TAG_ALAMAT, alamat);
		editor.putString(TAG_QTY, qty);
		editor.putString(TAG_KETERANGAN, keterangan);
		editor.putString(TAG_WAKTUAWAL, waktuAwal);
		editor.putString(TAG_BIAYAESTIMASI, estimasi);
		editor.commit();
	}

	public String getValue(String tag){
		return pref.getString(tag, "");
	}

}
