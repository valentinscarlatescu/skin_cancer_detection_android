package skin_cancer_detection_android.ui.main;

import android.os.Bundle;
import android.util.ArrayMap;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.Session;
import skin_cancer_detection_android.ui.main.home.HomeTabFragment;
import skin_cancer_detection_android.ui.main.profile.ProfileTabFragment;
import skin_cancer_detection_android.ui.main.social.SocialTabFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private boolean isTwiceClicked;
    private AppTab appTab = AppTab.HOME;
    private Map<AppTab, TabFragment> fragments = new ArrayMap<>(4);

    {
        fragments.put(AppTab.HOME, new HomeTabFragment());
        fragments.put(AppTab.HISTORY, new SocialTabFragment());
        fragments.put(AppTab.PROFILE, new ProfileTabFragment());
    }

    @BindView(R.id.mainBottomNavigationView)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.mainMenuHome:
                    if (appTab != AppTab.HOME) {
                        openTab(AppTab.HOME);
                        appTab = AppTab.HOME;
                    }
                    break;
                case R.id.mainMenuHistory:
                    if (appTab != AppTab.HISTORY) {
                        openTab(AppTab.HISTORY);
                        appTab = AppTab.HISTORY;
                    }
                    break;
                case R.id.mainMenuProfile:
                    if (appTab != AppTab.PROFILE) {
                        openTab(AppTab.PROFILE);
                        appTab = AppTab.PROFILE;
                    }
                    break;
            }
            return true;
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Stream.of(AppTab.values()).forEach(o -> {
            Fragment fragment = fragments.get(o);
            String tag = String.valueOf(o.getIndex());
            if (fragment != null) {
                ft.add(R.id.mainContent, fragment, tag);
                ft.hide(fragment);
            }
        });

        Fragment activeFragment = fragments.get(appTab);
        if (activeFragment != null) {
            ft.show(activeFragment);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
            Fragment fragment = getSupportFragmentManager().getFragments().get(i);
            if (fragment.isVisible() && fragment.getChildFragmentManager().getBackStackEntryCount() > 1) {
                ((TabFragment) fragment).onBackClicked();
                return;
            }
        }
        if (isTwiceClicked) {
            finishAffinity();
        }
        Toast.makeText(this, getString(R.string.message_back_twice), Toast.LENGTH_SHORT).show();
        Session.getInstance().getHandler().postDelayed(() -> isTwiceClicked = false, 3000);
        isTwiceClicked = true;
    }

    public void setFragment(Fragment fragment) {
        for (int i = 0; i < getSupportFragmentManager().getFragments().size(); i++) {
            Fragment tabFragment = getSupportFragmentManager().getFragments().get(i);
            if (tabFragment.isVisible()) {
                ((TabFragment) tabFragment).setFragment(fragment);
                return;
            }
        }
    }

    private void openTab(AppTab appTab) {
        TabFragment fragmentToHide = fragments.get(this.appTab);
        TabFragment fragmentToShow = fragments.get(appTab);

        Objects.requireNonNull(fragmentToHide);
        Objects.requireNonNull(fragmentToShow);

        getSupportFragmentManager()
                .beginTransaction()
                .hide(fragmentToHide)
                .show(fragmentToShow)
                .commit();

        fragmentToShow.onTabClicked();
    }
}
