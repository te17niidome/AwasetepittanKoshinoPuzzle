package com.example.awasetepittankoshinopuzzle;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CsvReader {
    //データ保存用のArrayListを作成
    protected ArrayList<String[]> CsvData = new ArrayList<String[]>();
    // CsvDataの構造
//    ArrayList[
//        String["name","detail"],
//        String["name","detail"],
//           :
//        String["name","detail"]
//    ]
    //改行の文字コード追加
    static final String BR = System.getProperty("line.separator");

    public void reader(Context context) {
        AssetManager assetManager = context.getResources().getAssets();
        try {
            // CSVファイルの読み込み
            InputStream inputStream = assetManager.open("pictures_data.csv");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            String line;

            while ((line = bufferReader.readLine()) != null) {
//
                //カンマ区切りで１つづつ配列に入れる
                String[] RowData = line.split(",");
                ArrayList<String> row = new ArrayList<String>();
                CsvData.add(RowData);



            }
            bufferReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int indexOf(String s){
        for(int i=0; i<CsvData.size(); i++){
            String a = CsvData.get(i)[0];
            if(a.equals(s)){
                return i;
            }
        }
        return 0;
    }
}
