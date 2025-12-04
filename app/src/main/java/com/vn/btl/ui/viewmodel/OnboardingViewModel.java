package com.vn.btl.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vn.btl.R;
import com.vn.btl.model.something.OnboardingItem;

import java.util.Arrays;
import java.util.List;

public class OnboardingViewModel extends ViewModel {

    private final MutableLiveData<List<OnboardingItem>> onboardingItems = new MutableLiveData<>();

    public OnboardingViewModel() {
        onboardingItems.setValue(Arrays.asList(
                new OnboardingItem(R.drawable.onboard_1, "Create playlists",
                        "Organize playlists according to your preferences."),
                new OnboardingItem(R.drawable.onboard_2, "Listen anytime, anywhere",
                        "The world of music at your fingertips.")
        ));
    }

    public LiveData<List<OnboardingItem>> getOnboardingItems() {
        return onboardingItems;
    }
}