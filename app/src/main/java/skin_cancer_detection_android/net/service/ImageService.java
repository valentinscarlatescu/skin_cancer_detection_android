package skin_cancer_detection_android.net.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import skin_cancer_detection_android.net.model.Image;

public interface ImageService {

    @GET("/api/image")
    Call<Image> getProfile();

    @PUT("/api/image")
    static Call<Image> updateScreen(@Body Image image);

}
