package skin_cancer_detection_android.net.service;

import java.util.List;

import skin_cancer_detection_android.net.model.Cart;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CartService {

    @GET("/api/carts")
    Call<List<Cart>> getByUserId(@Query("userId") Long userId);

    @POST("/api/carts")
    Call<Void> save(@Body Cart body);

}
