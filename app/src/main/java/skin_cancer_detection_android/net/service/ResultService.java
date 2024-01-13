package skin_cancer_detection_android.net.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import skin_cancer_detection_android.net.model.Result;

public interface ResultService {

    @GET("/api/results")
    Call<List<Result>> getByUserId(@Query("userId") Long userId);

    @PUT("/api/result")
    Call<Result> updateScreen(@Body Result result);

}
