package com.example.application.task_8;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.R;

public class ButtonOfFilterFragment extends Fragment {

    public static Button blackAndWhiteFilter;
    public static Button blurFilter;
    public static Button brightUpFilter;
    public static Button brightDownFilter;
    public static Button reset;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(CurrentImageFragment.TAG_CALLBACK, "onCreateView - Fragment of button");

        View rootView = inflater.inflate(R.layout.fragment_button_of_filter, null);

        blackAndWhiteFilter = rootView.findViewById(R.id.black_and_white_button);
        blurFilter = rootView.findViewById(R.id.blur_filter);
        brightUpFilter = rootView.findViewById(R.id.brightUp_filter);
        brightDownFilter = rootView.findViewById(R.id.brightDown_filter);
        reset = rootView.findViewById(R.id.reset);

        blackAndWhiteFilter.getBackground().setColorFilter(0xFF3E8EC7, PorterDuff.Mode.MULTIPLY);
        blurFilter.getBackground().setColorFilter(0xFF3E8EC7, PorterDuff.Mode.MULTIPLY);
        brightUpFilter.getBackground().setColorFilter(0xFF3E8EC7, PorterDuff.Mode.MULTIPLY);
        brightDownFilter.getBackground().setColorFilter(0xFF3E8EC7, PorterDuff.Mode.MULTIPLY);
        reset.getBackground().setColorFilter(0xFF0DA057, PorterDuff.Mode.MULTIPLY);

        return rootView;
    }
}
