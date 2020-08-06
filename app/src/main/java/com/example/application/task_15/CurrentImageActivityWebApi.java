package com.example.application.task_15;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.application.R;

public class CurrentImageActivityWebApi extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_api_activity_current_image);

        CurrentImageFragmentWebApi currentImageFragmentWebApi = new CurrentImageFragmentWebApi();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.web_api_container_for_fragment, currentImageFragmentWebApi)
                .commit();
    }
}
