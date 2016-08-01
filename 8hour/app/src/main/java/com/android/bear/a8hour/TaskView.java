package com.android.bear.a8hour;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.android.bear.a8hour.R;

/**
 * Created by bear on 7/31/16.
 */
public class TaskView extends LinearLayout {

    public TaskView(Context context) {
        super(context);

        LayoutInflater  mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.task_view, this, true);
    }
}
