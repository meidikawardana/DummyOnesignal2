package com.pintar_android.dummyonesignal2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity ini untuk menampilkan halaman login
 */
public class LoginActivity extends AppCompatActivity {

	private EditText mUsernameView; //komponen untuk meminta inputan username
	private EditText mPasswordView; //komponen untuk meminta inputan password
	private View mProgressView; //komponen untuk menampilkan indikator loading
	private View mLoginFormView; //komponen untuk menampilkan form login

	boolean isLoggingIn = false; //penanda apakah aplikasi sedang memproses login ke server

	String oneSignalUserId = ""; //variabel untuk menyimpan userid onesignal

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);  //tampilkan layout dari activity_login.xml
		// Set up the login form.
		mUsernameView = findViewById(R.id.username); //dapatkan komponen inputan username dari layout

		mPasswordView = findViewById(R.id.password); //dapatkan komponen inputan password dari layout
		mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
			if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) { //jika password sudah diisi
				attemptLogin(); //mulai proses login
				return true;
			}
			return false;
		});

		Button signInButton = findViewById(R.id.sign_in_button); //dapatkan komponen tombol signIn dari layout
		signInButton.setOnClickListener(view -> attemptLogin()); //jika tombol signIn di-klik, mulai proses login

		mLoginFormView = findViewById(R.id.login_form); //dapatkan komponen form login dari layout
		mProgressView = findViewById(R.id.login_progress); //dapatkan komponen tampilan loading dari layout

		// update userid onesignal jika saat ini userid onesignal di aplikasi bernilai null atau string kosong
		// (empty string)
		updateOnesignalUserIdIfNullOrEmpty();

		if(BuildConfig.DEBUG) //jika aplikasi dijalankan dari android studio (mode debug)
			Log.e("--oneSignalUserId", oneSignalUserId); //tampilkan oneSignalUserId di logcat

		if(!GlobalFunctions.getUsername(this).equals("")){ //jika sudah ada user yg login di aplikasi
			Intent HomeActivityIntent = new Intent(getApplicationContext(),HomeActivity.class); //tampilkan HomeActivity (layar utama)
			startActivity(HomeActivityIntent); //aktifkan HomeActivity
			finish(); //sembunyikan activity saat ini (layar login)
		}
	}

	// fungsi ini untuk mengupdate userid onesignal jika saat ini userid onesignal di aplikasi
	// bernilai null atau string kosong (empty string)
	// PERHATIAN: FUNGSI INI BISA SAJA TIDAK JALAN & OUTPUTNYA TETAP USERID ONESIGNAL BERNILAI NULL / EMPTY STRING
	private void updateOnesignalUserIdIfNullOrEmpty() {
		//jika userid onesignal di hape adalah null atau empty string (string kosong)
		if(oneSignalUserId == null || oneSignalUserId.equals("")){
			// mendapatkan status berlangganan user terhadap onesignal
			// data ini didapat dari server onesignal
			OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();

			//mendapatkan userid dari data langganan (subscription) user
			oneSignalUserId = status.getSubscriptionStatus().getUserId();
		}

		//jika data dari server onesignal bernilai null, kita harus membuatnya menjadi empty string
		//supaya aplikasi tidak error ketika menggunakan userid onesignal yang bernilai null
		if(oneSignalUserId == null)
			oneSignalUserId = "";
	}

	/**
	 * Mencoba login dengan akun sesuai username & password yang diinputkan
	 * jika ada error (username / password tidak diisi atau invalid)
	 * pesan error akan ditampilkan dan proses login dibatalkan
	 */
	private void attemptLogin() {
		if (isLoggingIn) { //jika aplikasi sedang login ke server

			//tampilkan pesan
			Toast.makeText(this, "Maaf, sedang memproses masuk ...", Toast.LENGTH_SHORT).show();

			//stop fungsi ini
			return;
		}

		//sembunyikan indikator error
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		//dapatkan username & password dari inputan
		String username = mUsernameView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false; //ini variabel untuk menandakan apakah login perlu dibatalkan
		View focusView = null; //variabel untuk menyimpan inputan yang perlu difokuskan

		//cek apakah password tidak kosong dan apakah password valid
		if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {

			//jika password kosong atau password tidak valid, tampilkan error di indikator error
			mPasswordView.setError("Kata sandi kurang panjang");
			focusView = mPasswordView; //set inputan password jadi inputan yang perlu difokuskan
			cancel = true; //tandai bahwa login perlu dibatalkan
		}


		//cek apakah username tidak kosong
		if (TextUtils.isEmpty(username)) {

			//jika username kosong, tampilkan pesan error di indikator error
			mUsernameView.setError("Inputan harus diisi");
			focusView = mUsernameView; //set inputan username jadi inputan yang perlu difokuskan
			cancel = true; //tandai bahwa login perlu dibatalkan
		}

		if (cancel) { //jika login perlu dibatalkan
			focusView.requestFocus(); //cukup fokuskan tampilan ke inputan yg perlu difokuskan
		} else { //jika login tidak perlu dibatalkan
			login(username, password); //proses login ke server
		}
	}

	//cek apakah password valid
	private boolean isPasswordValid(String password) {
		return password.length() > 2; //jika password lebih dari 2 karakter, maka password valid
	}

	/**
	 * mengatur apakah form login dan layout loading perlu ditampilkan
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2) //jalankan fungsi ini jika OS Android di hape >= 3.2 (honeycomb).
	private void showProgress(final boolean show) {
		//set durasi animasi loading dari konfigurasi bawaan android
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

		mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE); //tampilkan / sembunyikan form login sesuai parameter "show"
		mLoginFormView.animate().setDuration(shortAnimTime) //set durasi sesuai settingan tadi
				.alpha(show ? 0 : 1) //set transparansi sesuai parameter "show"
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) { //ketika animasi selesai
						mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE); //tampilkan / sembunyikan form login sesuai parameter "show"
					}
				});

		mProgressView.setVisibility(show ? View.VISIBLE : View.GONE); //tampilkan / sembunyikan layout loading sesuai parameter show
		mProgressView.animate().setDuration(shortAnimTime)  //set durasi sesuai settingan tadi
				.alpha(show ? 1 : 0) //set transparansi sesuai parameter "show"
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) { //ketika animasi selesai
						mProgressView.setVisibility(show ? View.VISIBLE : View.GONE); //tampilkan / sembunyikan layout loading sesuai parameter "show"
					}
				});
	}

	//fungsi ini untuk memproses login ke server sesuai username & password yg telah diinputkan
	void login(final String username, final String password){

		isLoggingIn = true; //tandai kalau aplikasi sedang login ke server

		//tampilkan layout loading
		showProgress(true);

		//buat permintaan (request) ke server, untuk login
		StringRequest stringRequest = new StringRequest(
				com.android.volley.Request.Method.POST //pakai metode POST
				, Constants.BASE_URL + "Android_api/login" //set url login
				, response -> { //ketika respon dari server diterima

			if(BuildConfig.DEBUG) //jika aplikasi sedang mode DEBUG (dijalankan dari Android Studio)
				Log.e("--login-response", response); //tampilkan respons dari server di log

			try {

				JSONObject responseJO = new JSONObject(response); //ubah respons dari server dari format String
				// ke format JSONObject

				showProgress(false); //sembunyikan tampilan loading
				isLoggingIn = false; //tandai bahwa aplikasi sedang tidak login ke server

				if(responseJO.getInt("status") == 1){ //jika data dari server (status) = 1 (artinya login sukses)

					JSONObject aUserJO = responseJO.getJSONObject("a_user"); //dapatkan data dari server tentang user yg sedang login

					//buat obyek untuk user yg sedang login
					CurrentUser currentUser = new CurrentUser(aUserJO.getInt("id")
							, aUserJO.getString("username")
							, aUserJO.getString("name")
							, oneSignalUserId
					);

					//simpan obyek user yg sedang login ke shared preferences (semacam session di hape)
					GlobalFunctions.setCurrentUser(getApplicationContext(), currentUser);

					JSONObject tags = new JSONObject();
					tags.put("user_id", aUserJO.getInt("id")); //set tag user_id di server onesignal. nilainya dari id user di mysql
					tags.put("user_name", aUserJO.getString("username")); //set tag user_name di server onesignal. nilainya dari username di mysql
					OneSignal.sendTags(tags); //kirim tag ke server onesignal

					//tampilkan halaman utama
					Intent HomeActivityIntent = new Intent(getApplicationContext(),HomeActivity.class);
					startActivity(HomeActivityIntent);

					//sembunyikan halaman saat ini (login)
					finish();
				}else{
					Toast.makeText(this, responseJO.getString("msg"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) { //jika ada error, tangani di sini
				Log.e("--login-err", e.toString()); //catat error ke log

				//tampilkan pesan ke user
				Toast.makeText(LoginActivity.this, "Maaf, gagal masuk. terjadi kesalahan"
						, Toast.LENGTH_SHORT).show();

				showProgress(false); //sembunyikan tampilan loading
				isLoggingIn = false; //tandai bahwa aplikasi sedang tidak login ke server
			}
		}, error -> {

			if(error != null) { //jika ada error
				if(error.getMessage() != null) { //jika ada pesan error dari server
					//tampilkan pesan error ke user
					Toast.makeText(getApplicationContext(), "Error: "
							+ error.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}

			showProgress(false); //sembunyikan tampilan loading
			isLoggingIn = false; //tandai bahwa aplikasi sedang tidak login ke server
		}){
			@Override
			public String getBodyContentType() {
				return "application/x-www-form-urlencoded; charset=UTF-8"; //set contenttype
			}

			@Override
			protected Map<String, String> getParams() {

				if(oneSignalUserId == null)
					oneSignalUserId = "";

				Map<String, String> params = new HashMap<>();
				params.put("username", username); //mengirim username ke server
				params.put("pass",Enkripsi_MD5(password)); //mengirim password ke server
				params.put("oneSignalUserId", oneSignalUserId); //mengirim userid onesignal ke server
				return params;
			}

		};

		stringRequest.setShouldCache(false); //set supaya permintaan ke server ini tidak menyisakan cache

		RequestQueue queue = Volley.newRequestQueue(this); //buat queue (antrian) baru pakai library Volley
		queue.add(stringRequest); //tambahkan permintaan ke server ke queue supaya dieksekusi oleh library Volley
	}

	//ini adalah fungsi untuk mengenkripsi password menggunakan algoritma MD5
	private static String Enkripsi_MD5(String pass) {
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			byte[] md5hash;
			md.update(pass.getBytes("iso-8859-1"), 0, pass.length());
			md5hash = md.digest();

			StringBuilder buf = new StringBuilder();
			for (byte aData : md5hash) {
				int halfbyte = (aData >>> 4) & 0x0F;
				int two_halfs = 0;
				do {
					if ((0 <= halfbyte) && (halfbyte <= 9))
						buf.append((char) ('0' + halfbyte));
					else
						buf.append((char) ('a' + (halfbyte - 10)));
					halfbyte = aData & 0x0F;
				} while (two_halfs++ < 1);
			}
			return buf.toString();
		} catch (Exception e) {
			return pass;
		}
	}
}
