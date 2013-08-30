package com.hansson.shithead.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.hansson.shithead.R;

public class YesNoDialog extends DialogFragment {

    private int mMessageString;
    private DialogActivity mCaller;

    public YesNoDialog(int messageString, DialogActivity caller) {
        mMessageString = messageString;
        mCaller = caller;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mMessageString).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCaller.yes();
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCaller.no();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
