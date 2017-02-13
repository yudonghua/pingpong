package com.example.pc.pingpong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Runnable{
    static float ballsize=0,starsize,width=1980,height=1080;
    DrawView draw;
    int best,score,time=0;
    RelativeLayout re;
    boolean live=true,bounce=true,canadd=true;
    static float xv,yv,p2xv,p2yv,maxv,datx=0,daty=0,lastx=0,lasty=0,downx,downy,analyx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout main = (LinearLayout)findViewById(R.id.root) ;
        draw = new DrawView(this) ;
        SharedPreferences best0 = getSharedPreferences("best",
                Activity.MODE_PRIVATE);
        int history = best0.getInt("best", 0);
        TextView besttext = (TextView)findViewById(R.id.best);
        besttext.setText("最高分："+history);
        best=history;
        re = (RelativeLayout) findViewById(R.id.re) ;
        main.post(new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
        draw.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                if(event.getY()>draw.height/2){
                    downx=event.getX();
                    downy=event.getY();
                }

                return true;
            }
        }) ;
        main.addView(draw) ;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(live){
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(draw.drawtext&&time<500){
                        time++;
                    }
                    if(time>=500){
                        draw.drawtext=false;
                    }
                    datx=downx-draw.p1x;
                    daty=downy-draw.p1y;
                    float p=(float) (maxv/Math.sqrt(datx*datx+daty*daty));
                    if(datx*datx+daty*daty>maxv*maxv){
                        datx=datx*p;
                        daty=daty*p;
                    }
                    draw.p1x+=datx;
                    draw.p1y+=daty;
                    if(yv>=0)bounce=false;
                    else bounce=true;
                    if(canadd&&draw.y<draw.height/2){
                        score++;
                        canadd=false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView currentscore = (TextView)findViewById(R.id.score);
                                currentscore.setText("得分："+score);
                            }
                        });
                    }
                    if(draw.y>draw.height/2) canadd=true;
                    //复位
                    if(!bounce){
                        p2xv=draw.width/2-draw.p2x;
                        p2yv=draw.ballsize-draw.p2y;
                        p=(float) (maxv/Math.sqrt(p2xv*p2xv+p2yv*p2yv));
                        if(p2xv*p2xv+p2yv*p2yv>maxv*maxv){
                            p2xv=p2xv*p;
                            p2yv=p2yv*p;
                        }
                    }
                    //打球
                    if(bounce){
                        p2xv=analyx-draw.p2x;
                        p2yv=draw.height/4-draw.p2y;
                        p=(float) (maxv/Math.sqrt(p2xv*p2xv+p2yv*p2yv));
                        if(p2xv*p2xv+p2yv*p2yv>maxv*maxv){
                            p2xv=p2xv*p;
                            p2yv=p2yv*p;
                        }
                    }
                    draw.p2x+=p2xv;
                    draw.p2y+=p2yv;

                    if(draw.x>draw.p1x-6*draw.ballsize&&draw.x<draw.p1x+6*draw.ballsize
                            &&draw.y>draw.p1y-draw.ballsize/2&&draw.y<draw.p1y+draw.ballsize/2){
                        xv=datx;
                        yv=-(Math.abs(daty)+Math.abs(yv));
                        analyx=draw.p1x-xv/yv*(draw.p1y-draw.height/4);
                        if(analyx<0)analyx=-analyx;
                        if(analyx>draw.width)analyx=analyx-2*(analyx-draw.width);
                    }
                    if(draw.x>draw.p2x-6*draw.ballsize&&draw.x<draw.p2x+6*draw.ballsize
                            &&draw.y>draw.p2y-draw.ballsize/2&&draw.y<draw.p2y+draw.ballsize/2){
                        xv=p2xv+(float) 0.8*xv;
                        yv=Math.abs(p2yv)+Math.abs(yv);
                    }
                    p=(float) (maxv/Math.sqrt(xv*xv+yv*yv));
                    if(xv*xv+yv*yv>maxv*maxv){
                        xv=xv*p;
                        yv=yv*p;
                    }
                    if(bounce){
                        if((draw.x<draw.ballsize||draw.x>draw.width-draw.ballsize)&&(draw.y>draw.height/2)){
                            live=false;
                            update();
                            main.post(MainActivity.this);
                        }
                    }
                    if(!bounce){
                        if((draw.x<draw.ballsize||draw.x>draw.width-draw.ballsize)&&(draw.y<draw.height/2)){
                            score+=10;
                            draw.drawtext=true;
                            time=0;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView currentscore = (TextView)findViewById(R.id.score);
                                    currentscore.setText("得分："+score);
                                }
                            });
                        }
                    }
                    if(draw.y>draw.height-draw.ballsize){
                        live=false;
                        update();
                        main.post(MainActivity.this);
                    }
                    if(draw.x<draw.ballsize||draw.x>draw.width-draw.ballsize){
                        xv=-xv;
                    }
                    if(draw.y<draw.ballsize){
                        yv=-yv;
                    }

                    draw.x+=xv;
                    draw.y+=yv;
                    draw.postInvalidate();
                }
            }
        }).start();
    }
    @Override
    public void run() {
        if(best<score)best=score;
        SharedPreferences best0 = getSharedPreferences("best", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = best0.edit();
        editor.putInt("best", best);
        editor.commit();
        TextView bestscore = (TextView)findViewById(R.id.best2);
        bestscore.setVisibility(View.VISIBLE);
        bestscore.setText("最高分："+best);
        TextView currentscore = (TextView)findViewById(R.id.score2);
        currentscore.setVisibility(View.VISIBLE);
        currentscore.setText("得分："+score);
        Button button=(Button)findViewById(R.id.button);
        button.setVisibility(View.VISIBLE);
        TextView rule = (TextView)findViewById(R.id.rule);
        rule.setVisibility(View.VISIBLE);
    }
    public void replay(View view){
        score=0;
        live=true;
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

    }
    public void update(){
        draw.width=(float) re.getWidth();
        draw.height=(float)re.getHeight();
        draw.ballsize=draw.height/80;
        draw.x=draw.width/2;
        draw.y=draw.height/2+draw.width/3;
        draw.p1x=draw.width/2;
        draw.p1y=draw.height-draw.ballsize;
        draw.p2x=draw.width/2;
        draw.p2y=draw.ballsize;
        downx=draw.p1x;
        downy=draw.p1y;
        maxv=draw.height/1200;
        xv=0;
        yv=0;
    }
}
