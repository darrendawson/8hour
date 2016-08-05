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

/**
 * Created by bear on 7/31/16.
 */
public class TaskView extends LinearLayout {

    public interface SelectTask {
        void select(TaskView selectedCard);
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

    public TaskView(Context context) {
        super(context);

        LayoutInflater  mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.task_view, this, true);

        taskText = (TextView)findViewById(R.id.taskName);
        projectText = (TextView)findViewById(R.id.projectName);
        timeSpentText = (TextView)findViewById(R.id.timeSpent);
    }

    public TaskView(Context context, Activity activity) {
        this(context);
        owner = activity;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        SelectTask activity = (SelectTask)owner;
        activity.select(this);
        return true;
    }

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
