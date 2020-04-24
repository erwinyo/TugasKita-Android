package com.management.tugaskita;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogTerimaUndangan extends AppCompatDialogFragment {
    Global global = Global.getInstance();
    private DialogTerimaUndangan.DialogTerimaUndanganListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Undangan Masuk dari '"+global.getDataGrupNamaUndanganMasuk()+"'")
                .setMessage("Anda diundang sebagai "+global.getDataGrupAsStatusUndanganMasuk()+"\n\nApakah Anda akan menerima atau menolaknya?")
                .setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Terima", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onAccept();
                    }
                })
                .setNegativeButton("Tolak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDecline();
                    }
                });
        return builder.create();
    }

    public interface DialogTerimaUndanganListener {
        void onAccept();
        void onDecline();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DialogTerimaUndangan.DialogTerimaUndanganListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement dialog listener");
        }
    }
}
