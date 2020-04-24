package com.management.tugaskita;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogProgress extends AppCompatDialogFragment {
    Global global = Global.getInstance();
    String data;

    public DialogProgress(String data) {
        this.data= data;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog.Builder builder = new ProgressDialog.Builder(getActivity());
        builder.setTitle("In progress...")
                .setMessage(data);
        return builder.create();
    }

    public interface DialogProgressListener {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            DialogProgressListener listener = (DialogProgressListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement dialog listener");
        }
    }
}
