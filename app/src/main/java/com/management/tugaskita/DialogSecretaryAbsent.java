package com.management.tugaskita;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogSecretaryAbsent extends AppCompatDialogFragment {
    Global global = Global.getInstance();
    private DialogSecretaryAbsent.DialogSecretaryAbsentListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] jenisAbsent = {"MASUK","IJIN","SAKIT","ALPHA"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pilih keterangan:")
                .setItems(jenisAbsent, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        if (which == 0)
                            listener.onMasuk();
                        else if (which == 1)
                            listener.onIjin();
                        else if(which == 2)
                            listener.onSakit();
                        else if(which == 3)
                            listener.onAlpha();
                    }
                })
                .setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    public interface DialogSecretaryAbsentListener {
        void onMasuk();
        void onIjin();
        void onSakit();
        void onAlpha();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DialogSecretaryAbsent.DialogSecretaryAbsentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement dialog listener");
        }
    }
}
