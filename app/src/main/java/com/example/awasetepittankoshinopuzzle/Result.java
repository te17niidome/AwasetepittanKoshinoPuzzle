package com.example.awasetepittankoshinopuzzle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class Result extends AppCompatActivity implements View.OnClickListener{
    //結果
    private boolean result = false;
    private String file_name = "";

    //BGMの変数
    MediaPlayer gameBgm;

    SoundPool soundPool;    // 効果音を鳴らす本体（コンポ）
    int mp3a;          // 効果音データ（mp3）

    //各部品
    TextView TextViewDetail1,TextViewDetail2;
    Button ButtonAgain, ButtonEnd, ButtonTweet, ButtonRetry;
    ImageView imageView;

    //CSV
    CsvReader csvReader;

    //定数等の入ったクラス
    FinalValues finalValues;

    //アラートダイヤログ
    AlertDialogFragment alertDialogFragment;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        //結果を受け取る
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("Clear")){
            result = intent.getBooleanExtra("Clear", true);
            if (result){//ゲームクリアしていた場合
                //画面をセット
                setContentView(R.layout.activity_result_s);

        //        gameBgm = MediaPlayer.create(this, R.raw.result);        //BGMセット

                //各部品のインスタンスを取得
                //ここでもういっこtextを受け取る
                TextViewDetail1 = (TextView) findViewById(R.id.textView_detail1);
                TextViewDetail2 = (TextView) findViewById(R.id.textView_detail2);
                ButtonAgain = (Button) findViewById(R.id.button_again);
                ButtonEnd = (Button) findViewById(R.id.button_end);
                ButtonTweet = (Button) findViewById(R.id.button_tweet);
                imageView = (ImageView) findViewById(R.id.imageView) ;

                //ボタンのリスナーセット
                ButtonAgain.setOnClickListener(this);
                ButtonEnd.setOnClickListener(this);
                ButtonTweet.setOnClickListener(this);

                //絵を張り替える
                file_name = intent.getStringExtra("FileName");
                // 文字列から画像のdrawableのIDを取得する
                int imageId = getResources().getIdentifier(file_name, "drawable", getPackageName());
                // imageIDよりオリジナルのビットマップを読み込む
                Bitmap bmp_orig = BitmapFactory.decodeResource(getResources(), imageId);
                //DivideImageクラス作成
                DivideImage dimg = new DivideImage();
                //FinalValuesクラス作成
                finalValues = new FinalValues();
                //画像サイズ変換
                Bitmap bmp1 = dimg.Resize(bmp_orig, finalValues.IMAGE_SIZE);
                imageView.setImageBitmap(bmp1);

                //CSV読み込み
                csvReader = new CsvReader();
                csvReader.reader(getApplicationContext());
                int n = csvReader.indexOf(file_name);
                String[] row = csvReader.CsvData.get(n);
                TextViewDetail1.setText(row[1]);
                //ここrow[2]ってするだけじゃない？
                TextViewDetail2.setText(row[2]);

            }else{//ゲームオーバーしていた場合
                //画面をセット
                setContentView(R.layout.activity_result_f);

       //         gameBgm = MediaPlayer.create(this, R.raw.gameover);        //BGMセット
                //各部品のインスタンスを取得
                ButtonRetry = (Button) findViewById(R.id.button_retry);
                ButtonEnd = (Button) findViewById(R.id.button_end);
                //ボタンのリスナーセット
                ButtonRetry.setOnClickListener(this);
                ButtonEnd.setOnClickListener(this);
            }
     /*       gameBgm.setLooping(true); // ループ設定
            gameBgm.seekTo(0); // 再生位置を0ミリ秒に指定
            gameBgm.start(); // 再生開始*/
        }
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
        if(result){
            gameBgm = MediaPlayer.create(this, R.raw.result);        //BGMセット
        }else{
            gameBgm = MediaPlayer.create(this, R.raw.gameover);        //BGMセット
        }
//        gameBgm = MediaPlayer.create(this, R.raw.selectmode);        //BGMセット
        gameBgm.setLooping(true); // ループ設定
        gameBgm.start(); // 再生
    }

    // 画面が非表示に実行
    @Override
    protected void onPause() {
        super.onPause();
        gameBgm.pause(); // 一時停止
    }

    @Override
    public void onClick(View v){
        Intent data = new Intent();

        switch (v.getId()){
            case R.id.button_again:
               // TextViewDetail1.setText("AgainButton was pushed!");  ここいるか？
                data.putExtra("NextActivity", "select_sub");
                setResult(RESULT_OK, data);
                soundPool.play(mp3a,1f , 1f, 0, 0, 1f);
                gameBgm.stop(); // プレイ中のBGMを停止する
                finish();
                break;

            case R.id.button_end:
                data.putExtra("NextActivity", "select_main");
                setResult(RESULT_OK, data);
                soundPool.play(mp3a,1f , 1f, 0, 0, 1f);
                gameBgm.stop(); // プレイ中のBGMを停止する
                //こっち側でほかのクラスのBGMを操作すると止まらなくなる
                finish();
                break;

            case R.id.button_tweet:

                // ツイッターの連携先URI
                String twitterUri = "twitter://user?screen_name=";

                // 起動したいプロフィール画面のユーザーID
                String userId = "@";

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String uriStr = twitterUri + userId;
                intent.setData(Uri.parse(uriStr));
                soundPool.play(mp3a,1f , 1f, 0, 0, 1f);
                //gameBgm.stop(); // プレイ中のBGMを停止する

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Twitter公式アプリがインストールされていなかったときの処理
                    //確認ダイヤログ表示
                    DialogFragment newFragment = new AlertDialogFragment();
                    newFragment.show(getSupportFragmentManager(), "alert");
                }
                break;

            case R.id.button_retry:
                data.putExtra("NextActivity", "select_sub");
                setResult(RESULT_OK, data);
                soundPool.play(mp3a,1f , 1f, 0, 0, 1f); //なのこの表記
                gameBgm.stop(); // プレイ中のBGMを停止する
                finish();
                break;

            default:
                break;
        }
    }

    // DialogFragment を継承したクラス
    public static class AlertDialogFragment extends DialogFragment {

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            // タイトル
            alert.setTitle("Twitterアプリが開けませんでした. ブラウザで開きますか?");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // OK button pressed
                    Uri uri = Uri.parse("https://twitter.com/home");
                    Intent i = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(i);
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // CANCEL button pressed
                }
            });

            return alert.create();
        }

    }


}
