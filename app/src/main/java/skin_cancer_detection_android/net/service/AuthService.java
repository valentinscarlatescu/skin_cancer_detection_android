package skin_cancer_detection_android.net.service;

import skin_cancer_detection_android.net.model.Token;
import skin_cancer_detection_android.net.model.User;
import skin_cancer_detection_android.net.model.body.AuthLoginBody;
import skin_cancer_detection_android.net.model.body.AuthRegisterBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("/api/auth/register")
    Call<User> register(@Body AuthRegisterBody body);

    @POST("/api/auth/login")
    Call<Token> login(@Body AuthLoginBody body);

    @POST("/api/auth/logout")
    Call<Void> logout();

}
