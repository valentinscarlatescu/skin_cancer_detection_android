package skin_cancer_detection_android.ui.main.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import skin_cancer_detection_android.ui.main.TabFragment;
import skin_cancer_detection_android.ui.main.homescd.HomeFragment;

public class HistoryTabFragment extends TabFragment {

    private HomeFragment historyFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyFragment = new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setInitialFragment(historyFragment);
        return view;
    }

    @Override
    protected void onTabClicked() {

    }
}