package com.example.pc.pingpong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/7/16.
 */
public class DrawView extends View {
    static float ballsize=0,width=1980,height=1080;
    static float x,y,p1x,p1y,p2x,p2y;
    boolean drawtext=false;
    Bitmap ball;
    /**
     *
     * @param context
     */
    public DrawView(Context context) {
        super(context);
        ball= BitmapFactory.decodeResource(getResources(), R.mipmap.ball);
        // TODO Auto-generated constructor stub
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        //创建画笔 ;

        Paint p = new Paint() ;
        p.setColor(Color.rgb(255,245,0));
        canvas.drawCircle(x,y,ballsize,p);
        p.setColor(Color.rgb(217,189,141));
        canvas.drawRect(p1x-6*ballsize,p1y-ballsize/2,p1x+6*ballsize,p1y+ballsize/2,p);
        canvas.drawRect(p2x-6*ballsize,p2y-ballsize/2,p2x+6*ballsize,p2y+ballsize/2,p);
        p.setColor(Color.rgb(40,40,40));
        p.setTextSize(2*ballsize);
        if(drawtext)canvas.drawText("打得好+10",width/2-6*ballsize,height/2-ballsize,p);
    }


}

