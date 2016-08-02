package com.android.bear.a8hour;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    long timeLeft;
    //TextView mTextTime;
    Button mPauseButton;
    boolean paused = false;
    CountDownTimer cdLeft;
    LinearLayout taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.mipmap.ic_plus);
        fab.setRippleColor(ContextCompat.getColor(this, R.color.black));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskList.addView(new TaskView(getApplicationContext()));
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        timeLeft = 8 * 60 * 60 * 1000;

        taskList = (LinearLayout) findViewById(R.id.taskList);
        //mTextTime = (TextView) findViewById(R.id.textView);
        mPauseButton = (Button) findViewById(R.id.pauseButton);

        /*if(timeLeft>0) {
            mTextTime.setText(msGetTime(timeLeft));
        } else {
            mTextTime.setText(0);
        }*/
        //mTextTime.setText(msGetTime(timeLeft));
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paused = !paused;
                /*if(paused){
                    mPauseButton.setText("|>");
                } else {
                    mPauseButton.setText("| |");
                }*/

            }
        });


        final Handler handler = new Handler();
        Timer timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!paused) {
                            timeLeft -= 60 * 1000;
                            //mTextTime.setText(msGetTime(timeLeft));
                            if(timeLeft>0) {
                                mPauseButton.setText(msGetTime(timeLeft));
                            } else {
                                mPauseButton.setText("Done!");
                            }
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    private String msGetTime(long ms) {
        int minutes = msGetMinutes(ms);

        return "" + msGetHours(ms) + ":" + (minutes <= 9 ? "0" + minutes : minutes);
    }
    private int msGetHours (long ms) {
        return (int)(ms / (1000 * 60 * 60));
    }

    private int msGetMinutes (long ms) {
        return (int)(ms % (60*60*1000)) / (1000 * 60);
    }
}
