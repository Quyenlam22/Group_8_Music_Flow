package com.vn.btl.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.vn.btl.R;
import com.vn.btl.model.OnboardingItem;

import java.util.Arrays;
import java.util.List;

public class OnboardingViewModel extends ViewModel {

    private final MutableLiveData<List<OnboardingItem>> onboardingItems = new MutableLiveData<>();

    public OnboardingViewModel() {
        onboardingItems.setValue(Arrays.asList(
                new OnboardingItem(R.drawable.onboard_1, "Tạo danh sách phát",
                        "Tự tay sắp xếp playlist theo sở thích của bạn."),
                new OnboardingItem(R.drawable.onboard_2, "Nghe mọi lúc mọi nơi",
                        "Thưởng thức âm nhạc ngay cả khi ngoại tuyến.")
        ));
    }

    public LiveData<List<OnboardingItem>> getOnboardingItems() {
        return onboardingItems;
    }
}