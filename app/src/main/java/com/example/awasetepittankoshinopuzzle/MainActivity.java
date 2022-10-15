package com.example.awasetepittankoshinopuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.VideoView;

//起動画面
public class MainActivity extends AppCompatActivity{

    private Context mContext;

    SoundPool soundPool;    // 効果音を鳴らす本体（コンポ）
    int mp3a;          // 効果音データ（mp3）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //こっから効果音設定
        // ② 初期化（電源を入れる・コピペOK）
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        } else {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    .setMaxStreams(5)
                    .build();
        }

        // ③ 読込処理(CDを入れる)
        mp3a = soundPool.load(this, R.raw.taptobutton, 1);

        //こっから動画再生
        mContext = getApplicationContext();

        //VideoView 動画再生

        final VideoView video = (VideoView) findViewById(R.id.main_videoView);
        //Mediaコントローラ（再生、停止などのボタン）
//        video.setMediaController(new MediaController(this));
        video.setVideoURI(Uri.parse("android.resource://" + this.getPackageName() + "/" +R.raw.prestartmovie3));
        video.start();

        // 再生完了通知リスナー
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp) {
                //この中身が動画終了時のもの

                //ここから次に進むボタン
                ImageButton nextButton = findViewById(R.id.imageButton1);
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.imageButton1){
                            //効果音再生
                            soundPool.play(mp3a,1f , 1f, 0, 0, 1f);
                            Intent intent = new Intent(getApplication(), Select.class);
                            startActivity(intent);//これ使って戻った時BGMなるようにするか？
                            MainActivity.this.finish();
                        }
                    }
                });

            }
        });

    }

}