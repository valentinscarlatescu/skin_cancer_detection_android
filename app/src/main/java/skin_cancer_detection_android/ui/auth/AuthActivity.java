package skin_cancer_detection_android.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import skin_cancer_detection_android.Constants;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.ErrorHandler;
import skin_cancer_detection_android.net.Session;
import skin_cancer_detection_android.net.client.RetrofitClient;
import skin_cancer_detection_android.net.model.Token;
import skin_cancer_detection_android.net.model.User;
import skin_cancer_detection_android.net.model.body.AuthLoginBody;
import skin_cancer_detection_android.net.model.body.AuthRegisterBody;
import skin_cancer_detection_android.net.service.AuthService;
import skin_cancer_detection_android.net.service.UserService;
import skin_cancer_detection_android.ui.common.ProgressDialog;
import skin_cancer_detection_android.ui.main.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity implements LoginFragment.LoginListener, RegisterFragment.RegisterListener {

    @BindView(R.id.authToolbar)
    Toolbar toolbar;
    @BindView(R.id.authTabLayout)
    TabLayout tabLayout;
    @BindView(R.id.authViewPager)
    ViewPager viewPager;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private ViewPagerAdapter adapter;
    private AuthService authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
    private UserService userService = RetrofitClient.getRetrofitInstance().create(UserService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.app_name);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), "Login");
        adapter.addFragment(new RegisterFragment(), "Register");
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onLoginRequest(String email, String password) {
        AuthLoginBody body = new AuthLoginBody();
        body.email = email;
        body.password = password;


        if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            Toast.makeText(AuthActivity.this, getString(R.string.message_invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(AuthActivity.this, getString(R.string.message_invalid_password), Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();

        Call<Token> tokenCall = authService.login(body);
        tokenCall.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {

                if (response.isSuccessful()) {
                    String token = response.body().token;
                    Session.getInstance().setToken(token);

                    Call<User> userCall = userService.getProfile();
                    userCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                User user = response.body();
                                Session.getInstance().setUser(user);
                                SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(Constants.USER_EMAIL, email);
                                editor.putString(Constants.USER_PASSWORD, password);
                                editor.putString(Constants.USER_TOKEN, token);
                                editor.apply();

                                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(AuthActivity.this, ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(AuthActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AuthActivity.this, ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AuthActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRegisterRequest(String email, String password, String confirmPassword) {
        AuthRegisterBody body = new AuthRegisterBody();
        body.email = email;
        body.password = password;
        body.confirmPassword = confirmPassword;

        if (password.length() < 8 || password.length() > 40) {
            Toast.makeText(AuthActivity.this, getString(R.string.message_password_length), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(AuthActivity.this, getString(R.string.message_password_not_identical), Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();

        Call<User> userCall = authService.register(body);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(AuthActivity.this, getString(R.string.message_success), Toast.LENGTH_LONG).show();

                    RegisterFragment registerFragment = (RegisterFragment) adapter.getItem(1);
                    registerFragment.clearFields();

                    LoginFragment loginFragment = (LoginFragment) adapter.getItem(0);
                    loginFragment.setFields(email, password);
                    viewPager.setCurrentItem(0);
                } else {
                    Toast.makeText(AuthActivity.this, ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AuthActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentsTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public @NonNull
        Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentsTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitles.get(position);
        }
    }

}