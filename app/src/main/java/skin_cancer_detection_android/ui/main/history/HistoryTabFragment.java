package skin_cancer_detection_android.ui.main.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import skin_cancer_detection_android.ui.main.TabFragment;

public class HistoryTabFragment extends TabFragment {

    private HistoryFragment historyFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyFragment = new HistoryFragment();
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
        historyFragment.init();
    }
}