package com.example.tugaskita30;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogError extends AppCompatDialogFragment {
    Global global = Global.getInstance();
    String data;

    public DialogError(String data) {
        this.data = data;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Error Happen!")
                .setMessage(data)
                .setIcon(R.drawable.ic_sentiment_very_dissatisfied_red_24dp);
        return builder.create();
    }

}
