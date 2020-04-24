package com.management.tugaskita;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogError extends AppCompatDialogFragment {
    Global global = Global.getInstance();
    String data;
    private DialogError.DialogErrorListener listener;

    public DialogError(String data) {
        this.data = data;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Error Happen!")
                .setMessage(data)
                .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onExit();
                    }
                })
                .setIcon(R.drawable.ic_sentiment_very_dissatisfied_red_24dp);
        return builder.create();
    }

    public interface DialogErrorListener {
        void onExit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogError.DialogErrorListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement dialog listener");
        }
    }

}
