package com.android.bear.a8hour;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
    Button viewLogButton;
    Button newDayButton;
    boolean paused = true;
    CountDownTimer cdLeft;
    LinearLayout taskList;
    ArrayList<TaskView> tasks = new ArrayList<>();
    TaskView selectedTask;
    float increment = 1000;
    Calendar calendar;
    String date;
    String weekDay;

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

        calendar = Calendar.getInstance();

        timeLeft = 8 * 60 * 60 * 1000;

        taskList = (LinearLayout) findViewById(R.id.taskList);
        //mTextTime = (TextView) findViewById(R.id.textView);
        mPauseButton = (Button) findViewById(R.id.pauseButton);
        viewLogButton = (Button) findViewById(R.id.viewLogButton);
        newDayButton = (Button) findViewById(R.id.newDayButton);

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

        viewLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openLog = new Intent(getApplicationContext(), Logs.class);
                startActivity(openLog);
            }
        });
        newDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = getDate();
                weekDay = getDayofWeek();

                Toast.makeText(getApplicationContext(), "date: " + weekDay, Toast.LENGTH_SHORT).show();
                //save all the info, all of it
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

                            save();
                            createNotification();
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

    @Override
    public void remove(TaskView currentCard) {
        if(currentCard == selectedTask) {
            tasks.remove(currentCard);
            taskList.removeView(currentCard);
        }
    }

    private void save() {
        String filename = "save";
        FileOutputStream outputStream;

        try {
            //Toast.makeText(getBaseContext(), "saving", Toast.LENGTH_LONG).show();
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

    public void createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_hourglass)
                        .setContentTitle(selectedTask.getTaskName())
                        .setContentText("" + msGetTime(timeLeft));
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        int notifyID = 0;
        mNotificationManager.notify(notifyID, mBuilder.build());
    }

    @Override
    public void onBackPressed() {
        //save();
        //createNotification();
    }

    public String getDate() {
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        return month + "/" + day + "/" + year;
    }
    public String getDayofWeek() {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String dayofWeek = "null";
        switch(day) {
            case Calendar.SUNDAY:
                dayofWeek = "Sunday";
                break;
            case Calendar.MONDAY:
                dayofWeek = "Monday";
                break;
            case Calendar.TUESDAY:
                dayofWeek = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                dayofWeek = "Wednesday";
                break;
            case Calendar.THURSDAY:
                dayofWeek = "Thursday";
                break;
            case Calendar.FRIDAY:
                dayofWeek = "Friday";
                break;
            case Calendar.SATURDAY:
                dayofWeek = "Saturday";
                break;
        }
        return dayofWeek;
    }
}
