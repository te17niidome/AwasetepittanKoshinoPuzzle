package com.example.awasetepittankoshinopuzzle;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Select extends AppCompatActivity{
    protected int difficulty;

    //BGMの変数
    MediaPlayer gameBgm;

    SoundPool soundPool;    // 効果音を鳴らす本体（コンポ）
    int mp3a;          // 効果音データ（mp3）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_select_main);
//        Intent intent = new Intent(this, GameActivity.class);

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

        mainViewCreat();
        gameBgm = MediaPlayer.create(this, R.raw.selectmode);        //BGMセット
    /*  gameBgm.setLooping(true); // ループ設定
        gameBgm.seekTo(0); // 再生位置を0ミリ秒に指定
        gameBgm.start(); // 再生開始*/
    }

    @Override
    public void onBackPressed(){
        gameBgm.stop(); // プレイ中のBGMを停止する
        //   subViewCreat();   //ここままだったら、他クラスにあるものをもってこなければならない
        finish();
    }

/*    @Override
    public void onUserLeaveHint()
    {
        // HOMEボタンが押されたときの処理
        gameBgm.stop(); // プレイ中のBGMを停止する
    }
*/
        // 画面が表示されるたびに実行
        @Override
        protected void onResume() {
            super.onResume();
            gameBgm = MediaPlayer.create(this, R.raw.selectmode);        //BGMセット
            gameBgm.setLooping(true); // ループ設定
            gameBgm.start(); // 再生
        }

         // 画面が非表示に実行
         @Override
         protected void onPause() {
              super.onPause();
              gameBgm.pause(); // 一時停止
          }

    protected void mainViewCreat(){
        setContentView(R.layout.activity_select_main);

    /*    gameBgm = MediaPlayer.create(this, R.raw.selectmode);        //BGMセット
        gameBgm.setLooping(true); // ループ設定
        gameBgm.seekTo(0); // 再生位置を0ミリ秒に指定
        gameBgm.start(); // 再生開始
*/
        //ボタン0(やさしい)クリック
        Button button0 = this.findViewById(R.id.button0);
        button0.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                difficulty = 0;
//                gameBgm.stop(); // プレイ中のBGMを停止する
                //効果音再生
                soundPool.play(mp3a,1f , 1f, 0, 0, 1f);
                subViewCreat();
            }
        });

        //ボタン1(普通)クリック
        Button button1 = this.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                difficulty = 1;
//                gameBgm.stop(); // プレイ中のBGMを停止する
                //効果音再生
                soundPool.play(mp3a,1f , 1f, 0, 0, 1f);
                subViewCreat();
            }
        });

        //ボタン2(難しい)クリック
        Button button2 = this.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                difficulty = 2;
//                gameBgm.stop(); // プレイ中のBGMを停止する
                //効果音再生
                soundPool.play(mp3a,1f , 1f, 0, 0, 1f);
                subViewCreat();
            }
        });
    }
//ここもともとprotectedだった。
    public void subViewCreat(){
        setContentView(R.layout.activity_select_sub);
        TextView textView = this.findViewById(R.id.textView);

/*        gameBgm = MediaPlayer.create(this, R.raw.selectmode);        //BGMセット
        gameBgm.setLooping(true); // ループ設定
        gameBgm.seekTo(0); // 再生位置を0ミリ秒に指定
        gameBgm.start(); // 再生開始
*/
        FinalValues finalValues = new FinalValues();
        String str = "難易度 ： " + finalValues.getDifficultyText(difficulty) + "\n"
                + String.valueOf(difficulty + 2) +" × "+String.valueOf(difficulty + 2)+"　マス\n"
                + "手数制限:" + String.valueOf(finalValues.getLimitNum(difficulty));
        textView.setText(str);

        //戻るボタンクリック
        Button buck_button = this.findViewById(R.id.buck_button);
        buck_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
             //   gameBgm.stop();  //BGM停止
                //効果音再生
                soundPool.play(mp3a,1f , 1f, 0, 0, 1f);
                mainViewCreat();
            }
        });

        //スタートボタンクリック
        Button start_button = this.findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //効果音再生
                soundPool.play(mp3a,1f , 1f, 0, 0, 1f);
                toGame();
                gameBgm.stop();  //BGM停止
            }
        }
        );
    }

    public void toGame(){
        //　インテントの作成
        Intent intent1 = new Intent(this, Game.class);
        // 引数設定
        intent1.putExtra("Difficulty", difficulty);
        //  遷移先の画面を起動(戻り値付き)
//        startActivity(intent1);

        startActivityForResult(intent1, FinalValues.CALL_RESULT_CODE);
    }

    //呼び出したアクティビティが終了して帰ってきたときに実行
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent revalue){
        super.onActivityResult(requestCode, resultCode, revalue);
        FinalValues finalValues = new FinalValues();
        if (requestCode == finalValues.CALL_RESULT_CODE){
            if (resultCode == Activity.RESULT_OK){
//                Intent data = new Intent();
//                data.putExtra("NextActivity", revalue.getStringExtra("NextActivity"));
//                setResult(RESULT_OK, data);
                if(revalue.getStringExtra("NextActivity").equals("select_main")){
                    mainViewCreat();
                }else if (revalue.getStringExtra("NextActivity").equals("select_sub")){
                    subViewCreat();
                }
//                finish();
            }
        }
    }
}
