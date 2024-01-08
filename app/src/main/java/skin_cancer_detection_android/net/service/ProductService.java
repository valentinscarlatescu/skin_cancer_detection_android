package skin_cancer_detection_android.net.service;

import java.util.List;

import skin_cancer_detection_android.net.model.Product;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductService {

    @GET("/api/products/byCategory")
    Call<List<Product>> getByCategoryId(@Query("categoryId") Long categoryId);

    @GET("/api/products/mostPopular")
    Call<List<Product>> getMostPopularProducts(@Query("minPercentage") Integer minPercentage);

    @GET("/api/products/recommendations")
    Call<List<Product>> getRecommendations(@Query("productsIds") List<Long> productsIds);

}
