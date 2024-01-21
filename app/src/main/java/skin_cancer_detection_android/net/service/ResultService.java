package skin_cancer_detection_android.net.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import skin_cancer_detection_android.net.model.Result;

public interface ResultService {

    @GET("/api/results")
    Call<List<Result>> getByUserId(@Query("userId") Long userId);

//    @PUT("/api/results")
//    Call<Result> updateScreen(@Body Result result);

    @POST("/api/results") // Aici trebuie să fie calea corectă spre metoda din backend pentru creare
    Call<Result> saveResult(@Body Result result);

    @GET("/api/results")
    Call<List<Result>> getResults(@Query("userId") Long userId);

    @GET("/api/results/latest")
    Call<Result> findLatestResult(@Query("userId") Long userId);


}
