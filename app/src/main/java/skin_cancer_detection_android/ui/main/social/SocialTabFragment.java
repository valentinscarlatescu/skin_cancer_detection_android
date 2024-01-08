package skin_cancer_detection_android.ui.main.social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import skin_cancer_detection_android.ui.main.TabFragment;

public class SocialTabFragment extends TabFragment {

    private SocialFragment socialFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socialFragment = new SocialFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setInitialFragment(socialFragment);
        return view;
    }

    @Override
    protected void onTabClicked() {

    }
}
