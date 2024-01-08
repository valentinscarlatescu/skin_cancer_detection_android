package skin_cancer_detection_android.net.service;

import java.util.List;

import skin_cancer_detection_android.net.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserService {

    @GET("/api/profile")
    Call<User> getProfile();

    @PUT("/api/profile")
    Call<User> updateProfile(@Body User user);

    @GET("/api/users")
    Call<List<User>> getUsers();

}
