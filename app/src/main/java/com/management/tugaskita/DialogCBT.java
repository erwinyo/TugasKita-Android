package com.management.tugaskita;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogCBT extends AppCompatDialogFragment {
    Global global = Global.getInstance();
    private DialogCBT.DialogCBTListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("TugasKita CBT")
                .setIcon(R.drawable.cbt_icon)
                .setPositiveButton("REFRESH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onRefresh();
                    }
                })
                .setNeutralButton("KELUAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onKeluar();
                    }
                });

        return builder.create();
    }

    public interface DialogCBTListener {
        void onRefresh();
        void onKeluar();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogCBT.DialogCBTListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement dialog listener");
        }
    }
}
