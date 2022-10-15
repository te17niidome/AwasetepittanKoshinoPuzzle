package com.example.awasetepittankoshinopuzzle;

public class FinalValues {
    static final int EASY_LIMIT_NUM = 5;
    static final int NORMAL_LIMIT_NUM = 15;
    static final int HARD_LIMIT_NUM = 25;
    static final int IMAGE_SIZE = 480;

    static final int CALL_RESULT_CODE = 100;// どのstartActivityメソッドによって呼び出されたのか識別するための定数(今回は必要ないけど、仕様変更があった場合便利そうだったからつけた。)

    //難易度に応じた手数を返す。
    public int getLimitNum(int n){
        int limit_num = 0;
        switch (n){
            case 0:
                limit_num = EASY_LIMIT_NUM;
                break;
            case 1:
                limit_num = NORMAL_LIMIT_NUM;
                break;
            case 2:
                limit_num = HARD_LIMIT_NUM;
                break;
        }
        return limit_num;
    }

    //難易度のテキスト表示
    public String getDifficultyText(int n){
        String str = "";
        switch (n){
            case 0:
                str = "やさしい";
                break;
            case 1:
                str = "ふつう";
                break;
            case 2:
                str = "むずかしい";
                break;
        }
        return str;
    }
}


