package skin_cancer_detection_android.ui.main.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import skin_cancer_detection_android.ui.main.TabFragment;

public class ProfileTabFragment extends TabFragment {

    private ProfileFragment profileFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileFragment = new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setInitialFragment(profileFragment);
        return view;
    }

    @Override
    protected void onTabClicked() {
        profileFragment.init();
    }

}
