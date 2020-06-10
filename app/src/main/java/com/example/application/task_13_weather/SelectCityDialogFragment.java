package com.example.application.task_13_weather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SelectCityDialogFragment extends DialogFragment {

    public interface toPassData {
        void getSelectedCity(String selectCity);
    }

    //?
    private static int sCheckedItem = -1;

    private final String[] mCityArray = {"Moscow", "Saint Petersburg", "Sochi", "Ulyanovsk"};
    private String mCurrentCity;
    private toPassData callback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (toPassData) getTargetFragment();

        if (savedInstanceState != null) {
            sCheckedItem = savedInstanceState.getInt("sCheckedItem");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select city")
                .setSingleChoiceItems(mCityArray, sCheckedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        mCurrentCity = mCityArray[i];
                        sCheckedItem = i;
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    callback.getSelectedCity(mCurrentCity);
                    dialog.cancel();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                });
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("sCheckedItem", sCheckedItem);
    }
}
