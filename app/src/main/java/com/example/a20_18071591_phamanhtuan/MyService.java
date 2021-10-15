package com.example.a20_18071591_phamanhtuan;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MyService extends Service {
    private MediaPlayer mp;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }


    public int add(int a, int b)
    {
        return a + b;
    }
    public void playMusic() {
        mp = MediaPlayer.create(this, R.raw.overhit);
        mp.setLooping(false);
        mp.start();
    }

    public void stopMusic() {
        mp.stop();
    }

    public MediaPlayer getMp() {
        return mp;
    }

    public class MyBinder extends Binder {
        public MyService getService(){
            return MyService.this;
        };
    }
}
