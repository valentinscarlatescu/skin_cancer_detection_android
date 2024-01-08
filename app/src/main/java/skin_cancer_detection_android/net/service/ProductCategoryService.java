package skin_cancer_detection_android.net.service;

import java.util.List;

import skin_cancer_detection_android.net.model.ProductCategory;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductCategoryService {

    @GET("/api/productCategories")
    Call<List<ProductCategory>> getAll();

}
