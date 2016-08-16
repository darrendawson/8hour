package com.android.bear.a8hour;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements EditNameDialog.EditNameDialogListener, TaskView.SelectTask{
    long timeLeft;
    //TextView mTextTime;
    Button mPauseButton;
    boolean paused = true;
    CountDownTimer cdLeft;
    LinearLayout taskList;
    ArrayList<TaskView> tasks = new ArrayList<>();
    TaskView selectedTask;
    float increment = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_plus);
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
        fab.setRippleColor(ContextCompat.getColor(this, R.color.black));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //run "New Task" fragment
                FragmentManager fm = getSupportFragmentManager();
                EditNameDialog editNameDialog = new EditNameDialog();
                editNameDialog.show(fm, "fragment_edit_name");
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
                if (tasks.size() > 0) {
                    paused = !paused;
                    if (paused) {
                        mPauseButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                        //mPauseButton.setText("|>");
                    } else {
                        mPauseButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gold));
                        //mPauseButton.setText("| |");
                    }
                }
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
                            timeLeft -= increment*60; //60 * 1000
                            //mTextTime.setText(msGetTime(timeLeft));
                            if(timeLeft>0) {
                                mPauseButton.setText(msGetTime(timeLeft));

                                //increment time on selected task
                                if(tasks.size()>0) {
                                    if (selectedTask == null) {
                                        selectedTask = tasks.get(0);
                                        selectedTask.selectCard();
                                    }
                                    selectedTask.increment(increment * 60);
                                }

                            } else {
                                mPauseButton.setText("Done!");
                            }
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);


        load();
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


    @Override
    public void onFinishEditDialog(Bundle input) {
        Toast.makeText(this, "Hi, " + input.getString("task") + input.getString("project"), Toast.LENGTH_SHORT).show();

        //create card
        TaskView newCard = new TaskView(getApplicationContext(), this);
        newCard.updateTaskInfo(input);
        taskList.addView(newCard);
        tasks.add(newCard);

        if(selectedTask!=null) {
            selectedTask.deselectCard();
        }
        selectedTask = newCard;
        selectedTask.selectCard();
        mPauseButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gold));
        paused=false;
    }

    @Override
    public void select(TaskView selectedCard) {
        //TaskView previousCard = selectedTask;
        if(selectedTask != null) {
            selectedTask.deselectCard();
        }
        selectedTask = selectedCard;
        selectedTask.selectCard();
    }

    private void save() {
        String filename = "save";
        FileOutputStream outputStream;

        try {
            Toast.makeText(getBaseContext(), "saving", Toast.LENGTH_LONG).show();
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);

            //save time here
            outputStream.write(("" + timeLeft + "\n").getBytes());
            //
            for(TaskView task : tasks) {
                String output = task.toString() + "\n";
                outputStream.write(output.getBytes());
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void load() {
        String filename = "save";

        try {
            FileInputStream inputStream = openFileInput(filename);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            line = r.readLine();
            timeLeft = Long.parseLong(line);
            mPauseButton.setText(msGetTime(timeLeft));
            while ((line = r.readLine()) != null) {
                //Toast.makeText(getBaseContext(), line, Toast.LENGTH_LONG).show();
                TaskView loadedTask = new TaskView(getApplicationContext(), this, line);
                taskList.addView(loadedTask);
                tasks.add(loadedTask);
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        save();
    }
}
