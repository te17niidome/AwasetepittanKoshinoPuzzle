package com.example.awasetepittankoshinopuzzle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class Game extends AppCompatActivity implements View.OnLongClickListener, View.OnTouchListener , View.OnClickListener {

    //BGMの変数
    MediaPlayer gameBgm;

    SoundPool soundPool;    // 効果音を鳴らす本体（コンポ）
    int mp3s;          // 効果音データ（mp3）
    int mp3m;
    int drag;
    int drop;
    int mp3a;

    //難易度、手数、マス数
    private int difficulty, limit_num, n;
    private float startX, startY, endX, endY, nowX, nowY;
    //ファイル名
    String file_name = "";
    //部品
    TextView textView;
    ImageView[] imageView;
    Button button;

    //ブロックの現在の位置
    int[] now_place;

    //ブロック
    Bitmap[] blocks;

    //次の画面のインテント
    Intent i;

    //定数等の入ったクラス
    FinalValues finalValues;

    //画像分割のためのクラス
    DivideImage dimg;

    //アニメーションの時間
    private int shortAnimationDuration = 1000;

//    private myDragEventListener dragListen;
    //ランダム変数
    Random random = new Random();
    int randomValue = random.nextInt(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_game_easy);//デバッグ用,！一応消しておく

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
        mp3s = soundPool.load(this, R.raw.successed, 1);
        mp3m = soundPool.load(this, R.raw.missed, 1);
        drag = soundPool.load(this, R.raw.picedup, 1);
        drop = soundPool.load(this, R.raw.dropout, 1);
        mp3a = soundPool.load(this, R.raw.taptobutton, 1);

        //========呼び出されるときに引き継がれる数値を読み、パラメータをセット=========
        //難易度の読み込み
        difficulty = 0;//初期値は0にした (0:簡単, 1:普通, 2:難しい)

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("Difficulty")){
            difficulty = intent.getIntExtra("Difficulty", 1);
        }   //69~74は正常通り作動、値も引き継がれている。

        //手数をセット
        finalValues = new FinalValues();
        limit_num = finalValues.getLimitNum(difficulty);

        //マス数nをセット
        n = difficulty + 2;

        //現在の配置を初期化
        now_place = new int[n * n];
        for (int i = 0; i < n * n; i++) {
            now_place[i] = i;
        }

        //画面のxmlファイルを難易度によって選択
        switch (difficulty) {
            case 0:
                setContentView(R.layout.activity_game_easy);//activity_easy.xml
                file_name = "easy"+randomValue;                           //この中でファイルネームを判断

//                gameBgm = MediaPlayer.create(this, R.raw.pazleasy);        //BGMセット
                break;
            case 1:
                setContentView(R.layout.activity_game_normal);//activity_normal.xml
                file_name = "nomal"+randomValue;

//                gameBgm = MediaPlayer.create(this, R.raw.pazlnomal);        //BGMセット
                break;
            case 2:
                setContentView(R.layout.activity_game_hard);//activity_hard.xml
                file_name = "hard"+randomValue;

//                gameBgm = MediaPlayer.create(this, R.raw.pazlhard);   //BGMセット
                break;
        }

        //textView
        textView = findViewById(R.id.textView);
        //======================================================================

        //=========================画像の読み込み================================
        //***ファイル名をうまいこと代入するように改変すること*******************************************************************坂口箇所


//        file_name = "koshi"+randomValue;   //区別するためにcase内に記入
        // 文字列から画像のdrawableのIDを取得する
        int imageId = getResources().getIdentifier(file_name, "drawable", getPackageName());

        // imageIDよりオリジナルのビットマップを読み込む
        Bitmap bmp_orig = BitmapFactory.decodeResource(getResources(), imageId);

        //DivideImageクラス作成
        dimg = new DivideImage();

        //画像サイズ変換
        Bitmap bmp1 = dimg.Resize(bmp_orig, finalValues.IMAGE_SIZE);

        //blocksに画像を分割して代入
        blocks = new Bitmap[n * n];
        blocks = dimg.Divide(bmp1, n);

        //======================================================================

        //==========================ドラッグ＆ドロップするためのいろいろ==========================
        imageView = new ImageView[n * n];
        for (int i = 0; i < n * n; i++) {
            String name = "imageView" + String.valueOf(i);
            // 文字列からImageViewのIDを取得する
            int imageViewid = getResources().getIdentifier(name, "id", getPackageName());
            imageView[i] = findViewById(imageViewid);

        }

        LinearLayout imageLayout = findViewById(R.id.imageLayout);

        //Sets setOnTouchListener
        imageLayout.setOnTouchListener(this);
        //======================================================================

        System.out.println("ブロックシャッフル前");
        //============ブロックをシャッフルするプログラム=============================
        Random random = new Random();
        do {
            for (int i = 0; i < n * n; i++) {
                SwapBlock(random.nextInt(n * n), random.nextInt(n * n));
            }
        }while (EndJudge() == 1);
        //======================================================================


        Reload();       //ここで画像に切り替わる

        //!--------ここからBGM設定-------------!//
/*        gameBgm.setLooping(true); // ループ設定
        gameBgm.seekTo(0); // 再生位置を0ミリ秒に指定
        gameBgm.start(); // 再生開始
*/

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
    }*/

    // 画面が表示されるたびに実行
    @Override
    protected void onResume() {
        super.onResume();
        switch(difficulty){
            case 0:
                gameBgm = MediaPlayer.create(this, R.raw.pazleasy);        //BGMセット
                break;
            case 1:
                gameBgm = MediaPlayer.create(this, R.raw.pazlnomal);        //BGMセット
                break;
            case 2:
                gameBgm = MediaPlayer.create(this, R.raw.pazlhard);   //BGMセット
                break;
        }
        gameBgm.setLooping(true); // ループ設定
        gameBgm.start(); // 再生
    }

    // 画面が非表示に実行
    @Override
    protected void onPause() {
        super.onPause();
        gameBgm.pause(); // 一時停止
    }

    /* OnLongClickListenerを実装したViewが長押しされた際に呼び出されるメソッド */
    @Override
    public boolean onLongClick(View v) {
        /* ドラッグ&ドロップ処理を開始する */

        v.startDrag(null, new View.DragShadowBuilder(v), v, 0);
        //効果音drag
        soundPool.play(drop,1f , 1f, 0, 0, 1f);
        return true;
    }

    //ドロップされたときに呼び出す関数
    public void Dropped() {
        //効果音drop
        //soundPool.play(drag,1f , 1f, 0, 0, 1f);
        if (0 <= endX && endX <= finalValues.IMAGE_SIZE && 0 <= endY && endY <= finalValues.IMAGE_SIZE) {
            int a = dimg.Specific_address(startX, startY, n);
            int b = dimg.Specific_address(endX, endY, n);
            if (a != b){
                //効果音drop
                soundPool.play(drag,1f , 1f, 0, 0, 1f);
                SwapBlock(a, b);
                limit_num -= 1;
                Reload();
                if (EndJudge() == 0) {//終わってない場合(何もしない)

                } else if (EndJudge() == 1) {//ゲームクリアした場合
                    crossfade(true);
                    i = new Intent(this, Result.class);
                    i.putExtra("Clear", true);
                    i.putExtra("FileName", file_name);
                    //成功効果音
                    soundPool.play(mp3s,1f , 1f, 0, 0, 1f);

                    // ゲームクリア時にBGMを切り替える
                   // gameBgm.pause(); // プレイ中のBGMを停止する

                } else {//ゲームオーバーした場合
                    crossfade(false);
                    i = new Intent(this, Result.class);
                    i.putExtra("Clear", false);

                    //失敗効果音
                    soundPool.play(mp3m,1f , 1f, 0, 0, 1f);

                    // ゲームオーバー時にBGMを切り替える
                   // gameBgm.pause(); // プレイ中のBGMを停止する

                }
            }
        }
    }

    //ブロックの入れ替えを行う関数
    public void SwapBlock(int a, int b) {
        int temp = now_place[a];
        now_place[a] = now_place[b];
        now_place[b] = temp;
    }

    //表示をリロードする関数
    public void Reload() {
        for (int i = 0; i < n * n; i++) {
            imageView[i].setImageBitmap(blocks[now_place[i]]);
        }
        textView.setText("　残りの手数 ： " + String.valueOf(limit_num));
    }

    //終了か否か判定する関数(続く場合:0, ゲームクリア:1, ゲームオーバ:2)
    public int EndJudge() {
        boolean flg = true;
        for (int i = 0; i < n * n; i++) {
            if (now_place[i] != i) flg = false;
        }
        if (flg) return 1;
        else if(limit_num <= 0) return 2;
        else return 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                startX = event.getX();
                startY = event.getY();
                nowX = startX;
                nowY = startY;
                int i = dimg.Specific_address(startX, startY, n);
                imageView[i].setImageBitmap(dimg.BorderYellow(blocks[now_place[i]]));

//                textView.setText(String.valueOf(startX) + "  " + String.valueOf(startY));//デバッグ用
                return true;

            case (MotionEvent.ACTION_MOVE):
                float x = event.getX();
                float y = event.getY();
                if (0 <= x && x <= finalValues.IMAGE_SIZE && 0 <= y && y <= finalValues.IMAGE_SIZE) {
                    int a = dimg.Specific_address(nowX, nowY, n);
                    int b = dimg.Specific_address(x, y, n);
                    if (a != b){// もしもタッチしているアドレスに変化があった時
                        imageView[a].setImageBitmap(blocks[now_place[a]]);
                        imageView[b].setImageBitmap(dimg.WhiteOut(blocks[now_place[b]]));
                        nowX = x;
                        nowY = y;
                        //黄色いラインが消えてしまうのでもっかい描画
                        i = dimg.Specific_address(startX, startY, n);
                        imageView[i].setImageBitmap(dimg.BorderYellow(blocks[now_place[i]]));
                    }
                }

                return true;

            case (MotionEvent.ACTION_UP):
                endX = event.getX();
                endY = event.getY();

                Reload();//黄色いラインを消すため
                Dropped();
//                textView.setText(String.valueOf(endX) + "  " + String.valueOf(endY));//デバッグ用
                return true;

            default:
                return true;
        }
    }

    private int id = 0;
    @Override
    public void onClick(View v){
        if(v.getId() == id){

            startActivityForResult(i, finalValues.CALL_RESULT_CODE);

            //失敗効果音
            soundPool.play(mp3a,1f , 1f, 0, 0, 1f);
            // ゲームクリア時にBGMを切り替える
            gameBgm.stop(); // プレイ中のBGMを停止する

        }else if (v.getId() == R.id.toResultButton){

            startActivityForResult(i, finalValues.CALL_RESULT_CODE);

            //失敗効果音
            soundPool.play(mp3a,1f , 1f, 0, 0, 1f);
            // ゲームオーバー時にBGMを切り替える
            gameBgm.stop(); // プレイ中のBGMを停止する
        }
    }

    //フェードインの部分
    private void crossfade(boolean r) {
        button = findViewById(R.id.toResultButton);
        button.setOnClickListener((View.OnClickListener)this);
        // 背景画像を変更する
        if (r){
            //成功効果音
       //     soundPool.play(mp3s,1f , 1f, 0, 0, 1f);
            button.setBackground(getResources().getDrawable(R.drawable.success));
        }else {
            //失敗効果音
        //    soundPool.play(mp3m,1f , 1f, 0, 0, 1f);
            button.setBackground(getResources().getDrawable(R.drawable.failure));
        }

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        button.setAlpha(0f);
        button.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        button.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        // ゲームクリア、ここも違う

    }

    //呼び出したアクティビティが終了して帰ってきたときに実行
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent revalue){
        super.onActivityResult(requestCode, resultCode, revalue);

        //BGMここは全然違う
        if (requestCode == finalValues.CALL_RESULT_CODE){

            if (resultCode == Activity.RESULT_OK){
                //受け取ったデータをそのまま戻す
                Intent data = new Intent();
                data.putExtra("NextActivity", revalue.getStringExtra("NextActivity"));
                setResult(RESULT_OK, data);

                //ここはリザルトのところで、ボタンが押されたら止まる
                finish();
            }
        }
    }
}
