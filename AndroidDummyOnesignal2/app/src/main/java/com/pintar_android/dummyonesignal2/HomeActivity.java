package com.pintar_android.dummyonesignal2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.onesignal.OneSignal;

/**
 * class ini untuk menampilkan halaman utama, yaitu halaman setelah user login.
 */
public class HomeActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home); //tampilkan layout dari activity_main.xml

		TextView textView = findViewById(R.id.textView); //dapatkan komponen textView dari layout
		Button bSignOut = findViewById(R.id.bSignOut); //dapatkan komponen button bSignOut dari layout

		CurrentUser currentUser = GlobalFunctions.getCurrentUser(this); //dapatkan data user yg sedang login saat ini

		String oneSignalUserId = currentUser.getOneSignalUserId(); //dapatkan data userid onesignal

		if(oneSignalUserId.equals("")) //jika data userid onesignal berupa string kosong
			oneSignalUserId = "[N/A]"; //tampilkan userid onesignal sebaga [N/A] atau Not Available

		//tampilkan teks selamat datang untuk user, beserta userid onesignal jika ada
		textView.setText("Selamat datang, "+currentUser.getName()
				+"\n\nOnesignal UserId Anda adalah: "+oneSignalUserId);

		//fungsi ini dijalan ketika tombol bSignOut diklik
		bSignOut.setOnClickListener(v -> {

			OneSignal.deleteTag("user_id"); //menghapus tag user_id di server onesignal untuk user yg sedang login di aplikasi
			OneSignal.deleteTag("user_name"); //menghapus tag user_name di server onesignal untuk user yg sedang login di aplikasi

			GlobalFunctions.removeCurrentUser(HomeActivity.this); //hapus data user yg sedang login
			startActivity(new Intent(getApplicationContext(),LoginActivity.class)); //tampilkan LoginActivity (halaman login)
			finish();
		});
	}
}
