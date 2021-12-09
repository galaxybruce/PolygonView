package com.totoro.xkf.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;


import com.totoro.xkf.polygonview.PolygonView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //组件必须设置的值为eageCount，也就是边数，必须大于3
        //loopCount，有几层，必须大于0
        //每个方向的值
        PolygonView polygonView = findViewById(R.id.pv_polygon_view);
        //设置每个方向的值
        //如果不设置的话就不会绘制
        List<Float> pointValue = new ArrayList<>();
        //每个方向值的大小不能超过1，在是那个方向的整体比例，请计算好在设置
        //pointValue的size一定要大于边数
//        for (int i = 0; i < polygonView.getEdgeCount(); i++) {
//            pointValue.add((float) (Math.random() * 1));
//        }
        pointValue.add(0.9f);
        pointValue.add(0.9f);
        pointValue.add(0.9f);
        pointValue.add(0.9f);
        pointValue.add(0.9f);
        pointValue.add(0.7f);
        pointValue.add(1.0f);
        pointValue.add(0.9f);

        polygonView.setPointValue(pointValue);
        //每个方向的的文字，可以不设置
        List<String> pointName = new ArrayList<>();
        pointName.add("Activity");
        pointName.add("BroadcastReceiver");
        pointName.add("ContentProvider");
        pointName.add("Service");
        pointName.add("View");
        pointName.add("View");
        pointName.add("View");
        pointName.add("View");
        polygonView.setPointName(pointName);

        List<Bitmap> pointBitmap = new ArrayList<>();
        pointBitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        pointBitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        pointBitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        pointBitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        pointBitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        pointBitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        pointBitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        pointBitmap.add(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
//        polygonView.setPointBitmap(pointBitmap);
        int [] colors = {
                Color.parseColor("#9966ff"),
                Color.parseColor("#99ccff"),
                Color.parseColor("#99ffff"),
                Color.parseColor("#99ffcc"),
                Color.parseColor("#ffff99"),
                Color.parseColor("#ff9933"),
                Color.parseColor("#ff66cc"),
                Color.parseColor("#cc99ff"),
                Color.parseColor("#9966ff"),
        };
        polygonView.setAreaColors(colors);
        //如果不调用这个方法的话，组件可以正常绘制，但是没有动画效果
        polygonView.draw();
    }
}
