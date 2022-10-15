package com.example.awasetepittankoshinopuzzle;

import android.graphics.Bitmap;
import android.graphics.Color;

public class DivideImage {

    // ビットマップイメージの縦横を指定したサイズ(int size)に変更する関数
    public Bitmap Resize(Bitmap bmp_orig, int size){
        int Width = bmp_orig.getWidth();
        int Height = bmp_orig.getHeight();

        Bitmap bmp1;
        if(Width > Height){
            bmp1 = Bitmap.createBitmap(bmp_orig, (Width-Height)/2, 0, Height, Height);
        }else{
            bmp1 = Bitmap.createBitmap(bmp_orig, 0, (Height-Width)/2, Width, Width);
        }

        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp1, size, size, true);

        return bmp2;

    }
    //n*nに分割する関数
    public Bitmap[] Divide(Bitmap bmp_orig, int n){
        int Width = bmp_orig.getWidth();
        Bitmap[] blocks = new Bitmap[n*n];

        int delta = Width / n;
        for (int x=0; x<n; x++){
            for (int y=0; y<n; y++){
                blocks[x + n*y] = Bitmap.createBitmap(bmp_orig, x*delta, y*delta, delta, delta);
            }
        }
        return blocks;
    }

    //x座標, y座標, マス数nからその位置がどの番地に該当するか特定する
    public int Specific_address(double x, double y, int n){
        FinalValues finalValues = new FinalValues();
        int a = (int)(x / (finalValues.IMAGE_SIZE / n));
        int b = (int)(y / (finalValues.IMAGE_SIZE / n));
        return b*n + a;
    }

    //縁に黄色のラインをつける関数
    public Bitmap BorderYellow(Bitmap img){
        FinalValues finalValues = new FinalValues();
        Bitmap bmp = Bitmap.createBitmap(img);
        int height = img.getHeight();
        int width = img.getWidth();
        int line_width = 5;//線幅
        //色
        int color_r = 255;
        int color_g = 255;
        int color_b = 0;

        int[] pixels = new int[height*width];
        img.getPixels(pixels, 0, width, 0, 0, width, height);

        int x, y;
        for(x=0; x<height; x++){
            for (y=0; y<line_width; y++){
                pixels[x + width*y] = Color.rgb(color_r, color_g, color_b);
                pixels[x + width*(height - 1 - y)] = Color.rgb(color_r, color_g, color_b);
            }
        }
        for(y=0; y<height; y++){
            for (x=0; x<line_width; x++){
                pixels[x + width*y] = Color.rgb(color_r, color_g, color_b);
                pixels[(width - 1 - x) + width*y] = Color.rgb(color_r, color_g, color_b);
            }
        }
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmp;
    }

    //ビットマップ画像を全体的に白っぽくする関数
    public Bitmap WhiteOut(Bitmap img){
        Bitmap bmp = Bitmap.createBitmap(img);
        int height = img.getHeight();
        int width = img.getWidth();
        int addPoint = 0x001f1f1f;//どれくらい白くするのか

        int[] pixels = new int[height*width];
        img.getPixels(pixels, 0, width, 0, 0, width, height);

        int x, y;
        for(y=0; y<height; y++){
            for (x=0; x<width; x++){
                if (pixels[x + width*y] >= (0xffffffff - addPoint)){
                    pixels[x + width*y] = 0xffffffff;
                }else{
                    pixels[x + width*y] = pixels[x + width*y] + addPoint;
                }
            }
        }

        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmp;
    }
}

