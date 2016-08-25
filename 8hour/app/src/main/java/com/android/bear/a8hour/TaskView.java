package com.android.bear.a8hour;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.bear.a8hour.R;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Created by bear on 7/31/16.
 */
public class TaskView extends LinearLayout implements /*View.OnLongClickListener, View.OnClickListener,*/ View.OnTouchListener{

    public interface SelectTask {
        void select(TaskView selectedCard);
        void remove(TaskView selectedCard);
    }

    Activity owner;
    boolean selected = false;
    static final String TASK = "task";
    static final String PROJECT = "project";
    TextView taskText;
    TextView projectText;
    TextView timeSpentText;
    long milliseconds;
    String time;
    private float x1,x2;
    static final int minDistance = 500;

    public TaskView(Context context) {
        super(context);

        LayoutInflater  mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.task_view, this, true);

        taskText = (TextView)findViewById(R.id.taskName);
        projectText = (TextView)findViewById(R.id.projectName);
        timeSpentText = (TextView)findViewById(R.id.timeSpent);

        //this.setOnLongClickListener(this);
        //this.setOnClickListener(this);
        this.setOnTouchListener(this);
    }

    public TaskView(Context context, Activity activity) {
        this(context);
        owner = activity;
    }

    public TaskView(Context context, Activity activity, String data) {
        this(context, activity);

        List<String> elements = Arrays.asList(data.split(","));

        if(elements.size() == 3)
        {
            taskText.setText(elements.get(0));
            projectText.setText(elements.get(1));
            milliseconds = Long.parseLong(elements.get(2));
            timeSpentText.setText(msGetTime(milliseconds));
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        //on click
        //on long click
        //on swipe
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float distance = Math.abs(x2-x1);
                //if normal click -> distance < minDistance
                if(distance<minDistance) {
                    SelectTask activity = (SelectTask) owner;
                    activity.select(this);
                } else {
                    Toast.makeText(getContext(), "swipe" + distance, Toast.LENGTH_SHORT).show();
                    //delete
                    SelectTask remover = (SelectTask) owner;
                    remover.remove(this);

                }
                break;
        }
        //if swipe
        return true;
    }
    /*
    @Override
    public void onClick(View view) {

        SelectTask activity = (SelectTask)owner;
        activity.select(this);
    }

    @Override
    public boolean onLongClick(View view) {
        Toast.makeText(getContext(), "puta", Toast.LENGTH_LONG).show();
        return true;
    }*/

    public void updateTaskInfo(Bundle input) {

        //((TextView)findViewById(R.id.taskName)).setText("");
        taskText.setText(input.getString(TASK));
        projectText.setText(input.getString(PROJECT));
    }

    public void selectCard() {
        taskText.setTextColor(ContextCompat.getColor(getContext(), R.color.gold));
    }
    public void deselectCard() {
        taskText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }
    public void increment(float increment)
    {
        milliseconds+=increment;
        time = msGetTime(milliseconds);
        timeSpentText.setText(time);

    }

    public String toString() {
        return taskText.getText() + "," + projectText.getText() + "," + milliseconds;
    }

    public String toLog() {
        return taskText.getText() + "," + projectText.getText() + "," + msGetTime(milliseconds) + "<";
    }

    public String getTaskName() {
        return taskText.getText() + "";
    }

    public String getProjectName() {
        return projectText.getText() + "";
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

    public void setTimetoZero() {
        milliseconds = 0;
    }
}
