package skin_cancer_detection_android.ui.main.homescd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import skin_cancer_detection_android.ui.main.TabFragment;
import skin_cancer_detection_android.ui.main.homescd.HomeFragment;

public class HomeTabFragment extends TabFragment {

    private skin_cancer_detection_android.ui.main.homescd.HomeFragment homeFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeFragment = new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setInitialFragment(homeFragment);
        return view;
    }

    @Override
    protected void onTabClicked() {
        homeFragment.init();
    }
}
