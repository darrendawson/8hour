package com.android.bear.a8hour;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.bear.a8hour.R;

import org.w3c.dom.Text;

/**
 * Created by bear on 7/31/16.
 */
public class TaskView extends LinearLayout {

    boolean selected = false;
    static final String TASK = "task";
    static final String PROJECT = "project";
    TextView taskText;
    TextView projectText;


    public TaskView(Context context) {
        super(context);

        LayoutInflater  mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.task_view, this, true);

        taskText = (TextView)findViewById(R.id.taskName);
        projectText = (TextView)findViewById(R.id.projectName);
    }

    public void updateTaskInfo(Bundle input) {

        //((TextView)findViewById(R.id.taskName)).setText("");
        taskText.setText(input.getString(TASK));
        projectText.setText(input.getString(PROJECT));
    }
}
