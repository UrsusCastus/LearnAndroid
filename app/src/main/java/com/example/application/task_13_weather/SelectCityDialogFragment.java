package com.example.application.task_13_weather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.application.R;

public class SelectCityDialogFragment extends DialogFragment {

    public interface SelectCityDialogListener {
        void onCitySelected(String selectedCity, int indexItem);
    }

    private static final String[] CITY_ARRAY = {"Moscow", "Saint Petersburg", "Sochi", "Ulyanovsk"};

    private int mCheckedItem;
    private SelectCityDialogListener mSelectCityListener;   //callback

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //получаем ссылку на вызывающий фрагмент
            mSelectCityListener = (SelectCityDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement SelectCityDialogListener interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (savedInstanceState != null) {
            mCheckedItem = savedInstanceState.getInt("mCheckedItem");
        } else if (bundle != null) {
            mCheckedItem = bundle.getInt("mIndexCheckedItem");
        } else {
            mCheckedItem = -1;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        int saveUnToChecked = mCheckedItem;
        builder.setTitle(R.string.SelectCity)
                .setSingleChoiceItems(CITY_ARRAY, mCheckedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        mCheckedItem = i;
                    }
                })
                .setPositiveButton(R.string.OK, (dialog, which) -> {
                    mSelectCityListener.onCitySelected(CITY_ARRAY[mCheckedItem], mCheckedItem);
                    // закрытие диалога
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.Cancel, (dialog, which) -> {
                    mSelectCityListener = null;
                    mCheckedItem = saveUnToChecked;
                    dialog.cancel();
                });
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mCheckedItem", mCheckedItem);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSelectCityListener = null;
    }
}
