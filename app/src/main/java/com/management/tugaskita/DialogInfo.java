package com.management.tugaskita;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogInfo extends AppCompatDialogFragment {
    Global global = Global.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Info")
                .setMessage(global.getDataInfo())
                .setIcon(R.drawable.ic_error_blue_24dp);
        return builder.create();
    }

    public interface DialogInfoListener {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            DialogInfoListener listener = (DialogInfoListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement dialog listener");
        }
    }
}
