package com.pintar_android.dummyonesignal2;

import android.app.Application;

import com.onesignal.OneSignal;

/**
 * Created by Meidika on 04/08/2018.
 */
public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //kita menginisialisasi onesignal dengan 4 baris berikut ini.
        OneSignal.startInit(this) //mulai proses inisialisasi
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification) //menampilkan notifikasi ketika aplikasi sedang dipakai user
                .unsubscribeWhenNotificationsAreDisabled(true) //jika notifikasi dinonaktifkan, maka unsubscribe (hapus langganan) user dari onesignal
                .init();
    }
}
