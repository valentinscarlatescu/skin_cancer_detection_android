package skin_cancer_detection_android.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;
import com.squareup.picasso.Picasso;

import skin_cancer_detection_android.Constants;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.ErrorHandler;
import skin_cancer_detection_android.net.Session;
import skin_cancer_detection_android.net.client.PicassoClient;
import skin_cancer_detection_android.net.client.RetrofitClient;
import skin_cancer_detection_android.net.model.User;
import skin_cancer_detection_android.net.service.UserService;
import skin_cancer_detection_android.ui.auth.AuthActivity;
import skin_cancer_detection_android.ui.main.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        init();
    }

    private void init() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        if (!pref.contains(Constants.USER_TOKEN)) {
            openAuthActivity();
            return;
        }

        String token = pref.getString(Constants.USER_TOKEN, null);
        if (token == null) {
            openAuthActivity();
            return;
        }

        JWT jwt = new JWT(token);
        if (jwt.isExpired(1)) {
            openAuthActivity();
            return;
        }

        Session session = Session.getInstance();
        session.setToken(token);

        UserService userService = RetrofitClient.getRetrofitInstance().create(UserService.class);
        Call<User> userCall = userService.getProfile();
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    Session.getInstance().setUser(user);
                    openMainActivity();
                } else {
                    Toast.makeText(InitialActivity.this, ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                    openAuthActivity();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(InitialActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                openAuthActivity();
            }
        });

    }

    private void openAuthActivity() {
        Intent intent = new Intent(InitialActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    private void openMainActivity() {
        Intent intent = new Intent(InitialActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
