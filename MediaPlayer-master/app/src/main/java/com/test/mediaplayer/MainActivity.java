package com.test.mediaplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private final String DATA_SD = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            + "/Wishbone Ash - Deep Blues.mp3";

    private ImageButton buttonPause;
    private ImageButton buttonPlay;
    private TextView tx1, tx2, songTitle;
    private SeekBar seekbar;
    private ImageView iv;

    private MediaPlayer mediaPlayer;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();

    private int forwardTime = 5000;
    private int backwardTime = 5000;

    public static int oneTimeOnly = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializationViewComponents();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {

                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }

        songTitle.setText("Название песни.mp3");
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(DATA_SD);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            tx1.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekbar.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    public void play() {
        Toast.makeText(getApplicationContext(), "Старт песни", Toast.LENGTH_SHORT).show();
        mediaPlayer.start();
        buttonPlay.setVisibility(View.INVISIBLE);
        buttonPause.setVisibility(View.VISIBLE);

        // получаем длинну трека и присваеваем ее finalTime
        finalTime = mediaPlayer.getDuration();

        // получаем текущую позицию воспроизведения и присваиваем ее startTime
        startTime = mediaPlayer.getCurrentPosition();

        // если первый раз проигрывается
        if (oneTimeOnly == 0) {
            // настраиваем seekbar на основе длины трека
            seekbar.setMax((int) finalTime);
            // и меняем значение, что уже проигрывался
            oneTimeOnly = 1;
        }
        // тут присваеваем сколько идет трек
        tx2.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime)))
        );
        // тут присваеваем его длительность
        tx1.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime)))
        );
        // присваиваем каждые 100 миллисекунд прогресс текущего воспроизведения
        seekbar.setProgress((int) startTime);
        // задаем шаг
        myHandler.postDelayed(UpdateSongTime, 100);
        // кнопку пауза делаем активной
        buttonPause.setEnabled(true);
        // кнопку плей делаем неактивной
        buttonPlay.setEnabled(false);
    }

    public void pause() {
        Toast.makeText(getApplicationContext(), "Песня на паузе", Toast.LENGTH_SHORT).show();
        mediaPlayer.pause();
        buttonPause.setVisibility(View.INVISIBLE);
        buttonPlay.setVisibility(View.VISIBLE);
        buttonPause.setEnabled(false);
        buttonPlay.setEnabled(true);
    }

    public void forward() {
        int temp = (int) startTime;

        if ((temp + forwardTime) <= finalTime) {
            startTime = startTime + forwardTime;
            mediaPlayer.seekTo((int) startTime);
            Toast.makeText(getApplicationContext(), "You have Jumped forward 5 seconds", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
        }
    }

    public void rewind() {
        int temp = (int) startTime;

        if ((temp - backwardTime) > 0) {
            startTime = startTime - backwardTime;
            mediaPlayer.seekTo((int) startTime);
            Toast.makeText(getApplicationContext(), "You have Jumped backward 5 seconds", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_play:
                play();
                break;
            case R.id.button_pause:
                pause();
                break;
            case R.id.button_forward:
                forward();
                break;
            case R.id.button_rewind:
                rewind();
                break;
        }
    }

    public void initializationViewComponents() {
        ImageButton buttonForward = (ImageButton) findViewById(R.id.button_forward);
        buttonPause = (ImageButton) findViewById(R.id.button_pause);
        buttonPlay = (ImageButton) findViewById(R.id.button_play);
        ImageButton buttonRewind = (ImageButton) findViewById(R.id.button_rewind);
        iv = (ImageView) findViewById(R.id.imageView);
        tx1 = (TextView) findViewById(R.id.textView2);
        tx2 = (TextView) findViewById(R.id.textView3);
        songTitle = (TextView) findViewById(R.id.textView4);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setClickable(false);
        buttonPause.setEnabled(false);
        buttonForward.setOnClickListener(this);
        buttonPause.setOnClickListener(this);
        buttonPlay.setOnClickListener(this);
        buttonRewind.setOnClickListener(this);
    }

    public void searchMedia() {
        ContentResolver contentResolver = getContentResolver();

        File file = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);

        Cursor cursor = contentResolver.query(Uri.fromFile(file), null, null, null, null);

        if (cursor == null) {
        } else if (!cursor.moveToFirst()) {

        } else {
            int titleColumn = cursor
                    .getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor
                    .getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int autor = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do {

                long thisId = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);

            } while (cursor.moveToNext());
        }
    }


}

