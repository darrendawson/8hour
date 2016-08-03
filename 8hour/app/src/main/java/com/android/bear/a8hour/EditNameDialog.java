package com.android.bear.a8hour;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
// ...

public class EditNameDialog extends DialogFragment {

    private EditText mEditText;
    Button addButton;

    public EditNameDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task_fragment, container);
        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        getDialog().setTitle("Hello");
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return view;
    }



    @Override
    public void onStart() {
        super.onStart();

        Toast toast = Toast.makeText(getContext(), "onStart", 10);
        toast.show();

        final Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(1300, 750);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        addButton = (Button) getView().findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test to see if this gets called
                //addButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

                //close fragment
                dialog.dismiss();
            }
        });
    }


}