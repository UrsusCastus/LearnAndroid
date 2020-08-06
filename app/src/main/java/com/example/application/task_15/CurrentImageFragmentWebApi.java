package com.example.application.task_15;

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

public class CurrentImageFragmentWebApi extends Fragment {
    public static final String TAG_CURRENT_IMAGE_FRAGMENT_WEB_API = "CurrentImageFragmentWebApi";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.web_api_fragment_current_image, container, false);
        ImageView imageView = rootView.findViewById(R.id.web_api_current_image_view);
        TextView textViewTitle = rootView.findViewById(R.id.web_api_text_view);
        downloadData(textViewTitle, imageView);
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
