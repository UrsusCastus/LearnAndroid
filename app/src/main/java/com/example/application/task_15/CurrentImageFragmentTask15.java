package com.example.application.task_15;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.squareup.picasso.Picasso;

public class CurrentImageFragmentTask15 extends Fragment {
    public static final String TAG_CURRENT_IMAGE_FRAGMENT_TASK15 = "CurrentImageFragmentTask15";
    private Context mContext;
    private TextView mTextView;
    private ImageView mImageView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.task15_fragment_current_image, container, false);
        mImageView = rootView.findViewById(R.id.task15_current_image_view);
        mTextView = rootView.findViewById(R.id.task15_text_view);
        downloadData(mTextView, mImageView);
        return rootView;
    }

    private void downloadData(TextView textView, ImageView imageView) {
        String title = getActivity().getIntent().getStringExtra("getTitle");
        String url = getActivity().getIntent().getStringExtra("getUrl");
        textView.setText(title);

        Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_image_not_download)
                .error(R.drawable.ic_error)
                .into(imageView);
    }
}
