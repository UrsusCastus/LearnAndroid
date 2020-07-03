package com.example.application.task_15;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.application.R;

public class CurrentImageActivityTask15 extends AppCompatActivity {

    private CurrentImageFragmentTask15 mCurrentImageFragmentTask15;
    private FragmentTransaction mFragmentTransaction;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task15_activity_current_image);

        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            mCurrentImageFragmentTask15 = new CurrentImageFragmentTask15();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction
                    .replace(R.id.task15_container_for_fragment,
                            mCurrentImageFragmentTask15, CurrentImageFragmentTask15
                                    .TAG_CURRENT_IMAGE_FRAGMENT_TASK15)
                    .commit();
        } else {
            //получаем ссылку по тегу на уже созданный фрагмент
            mCurrentImageFragmentTask15 = (CurrentImageFragmentTask15) mFragmentManager
                    .findFragmentByTag(CurrentImageFragmentTask15.TAG_CURRENT_IMAGE_FRAGMENT_TASK15);
        }
    }
}
