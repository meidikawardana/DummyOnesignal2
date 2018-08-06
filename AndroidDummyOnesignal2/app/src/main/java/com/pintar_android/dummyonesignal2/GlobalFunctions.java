package com.pintar_android.dummyonesignal2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

/**
 * class ini menyediakan fungsi2 yang bisa dijalankan untuk berbagai keperluan di aplikasi
 */
public class GlobalFunctions {

	//fungsi ini untuk menyimpan data user yg sedang login saat ini ke shared preferences
	//(semacam session di hape)
	public static void setCurrentUser(Context context, CurrentUser currentUser){
		//mendapatkan obyek untuk menyimpan data di hape
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context.getApplicationContext());

		//mengedit obyek penyimpanan data
		SharedPreferences.Editor editor = prefs.edit();

		//menyimpan data username
		editor.putString("username", currentUser.getUsername());

		Gson gson = new Gson(); //membuat obyek baru untuk memformat obyek java ke string berformat json
		String currentUserStr = gson.toJson(currentUser); //ubah data user ke string json

		editor.putString("currentUser", currentUserStr); //simpan string json
		editor.apply(); //konfirmasi penyimpanan data
	}

	//fungsi ini untuk mendapatkan data username dari shared preferences (semacam session di hape)
	public static String getUsername(Context context) {
		//mendapatkan obyek yang menyimpan data di hape
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context.getApplicationContext());
		//mendapatkan data username
		return prefs.getString("username", "");
	}

	//fungsi ini untuk mendapatkan data user dari shared preferences (semacam session di hape)
	public static CurrentUser getCurrentUser(Context context) {
		//mendapatkan obyek yang menyimpan data di hape
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context.getApplicationContext());

		//dapatkan data user
		String currentUserStr = prefs.getString("currentUser", "");

		Gson gson = new Gson(); //membuat obyek untuk mengubah json menjadi obyek java, dalam hal ini obyek user.
		return gson.fromJson(currentUserStr, CurrentUser.class); //menghasilkan obyek user
	}

	//fungsi ini untuk menghapus data user yang tersimpan di hape
	public static void removeCurrentUser(Context context){
		//tumpuki data user dengan data kosong
		setCurrentUser(context, new CurrentUser(0,"","",""));
	}
}
