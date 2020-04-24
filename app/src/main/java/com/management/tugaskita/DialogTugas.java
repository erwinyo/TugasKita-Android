package com.management.tugaskita;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogTugas extends AppCompatDialogFragment {
    Global global = Global.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(global.getDataJudulTugas())
                .setMessage(global.getDataCeritaTugas())
                .setIcon(global.getDataLogoTugas())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }

    public interface DialogTugasListener {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            DialogTugasListener listener = (DialogTugasListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement dialog listener");
        }
    }
}
