package com.example.a20_18071591_phamanhtuan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ImageView imgLoa;
    ImageButton btnPlay;
    private ServiceConnection serviceConnection;

    private boolean isConnected;
    private MyService myService;
    private int count = 0;
    private int totalTime = 0;
    TextView txtTotalTime;
    TextView txtProgress;
    Boolean flag = true;

    private ProgressBar progressB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTotalTime = findViewById(R.id.txtTotalTime);
        txtProgress = findViewById(R.id.txtProgress);

        totalTime = MediaPlayer.create(getBaseContext(), R.raw.overhit).getDuration();
        Date date = new Date(totalTime);
        txtTotalTime.setText("" + milliSecondsToTimer((long) totalTime));

        imgLoa = findViewById(R.id.imgLoa);
        Animation animation =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.aniblink);

        imgLoa.startAnimation(animation);
        initView();
        connectService();
        // progress
        progressB = findViewById(R.id.progressB);
    }

    private void sendNotificationMedia() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bg_nhac);
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this,"tag");
        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_smaill_music)
                .setSubText("AnhTuanIT")
                .setContentTitle("Over Hit")
                .setContentText("Game trailer audio")
                .setLargeIcon(bitmap)
                .addAction(R.drawable.previous, "Previous", null)
                .addAction(R.drawable.pause, "Pause", null)
                .addAction(R.drawable.next, "Next", null)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .build();

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(1,notification);

    }

    private void connectService() {

        Intent intent = new Intent(this, MyService.class);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MyService.MyBinder myBinder = (MyService.MyBinder) service;

                myService = myBinder.getService();
                isConnected = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isConnected = false;
                myService = null;
            }
        };
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    void initView()
    {
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected){
                    return;
                }

                // play and stop
                if(count++ % 2 == 0) {
                    myService.playMusic();
                    Toast.makeText(myService, "Play music", Toast.LENGTH_SHORT).show();
                    if(myService.getMp().isPlaying()) {
                        totalTime = totalTime = myService.getMp().getDuration();
                        txtProgress.post(mUpdateTime);
                        progressB.post(mUpdateProgress);
                    }
                    if(flag) {
                        flag = false;
                        sendNotificationMedia();
                    }
                } else {
                    myService.stopMusic();
                    Toast.makeText(myService, "Stop music", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
    private Runnable mUpdateProgress = new Runnable() {
        public void run() {
            int currentDuration;
            if (myService.getMp().isPlaying()) {
                currentDuration = myService.getMp().getCurrentPosition();
                double time = currentDuration * 1.0 / totalTime * 100;
                progressB.setProgress((int) time);
                progressB.postDelayed(this, 1000);
            }else {
                progressB.removeCallbacks(this);
            }
        }
    };

    private Runnable mUpdateTime = new Runnable() {
        public void run() {
            int currentDuration;
            if (myService.getMp().isPlaying()) {
                currentDuration = myService.getMp().getCurrentPosition();
                updatePlayer(currentDuration);
                txtProgress.postDelayed(this, 1000);
            }else {
                txtProgress.removeCallbacks(this);
            }
        }
    };

    private void updatePlayer(int currentDuration){
        txtProgress.setText("" + milliSecondsToTimer((long) currentDuration));
    }

    public  String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) finalTimerString += (hours > 9 ? hours : "0" + hours) + ":";
        finalTimerString += (minutes > 9 ? minutes : "0" + minutes) + ":";
        finalTimerString += (seconds > 9 ? seconds : "0" + seconds);

        // return timer string
        return finalTimerString;
    }
}