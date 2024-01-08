package skin_cancer_detection_android.net.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Cart {

    @SerializedName("id")
    public Long id;
    @SerializedName("user")
    public User user;
    @SerializedName("cartProducts")
    public List<Product> cartProducts;
    @SerializedName("dateTime")
    public LocalDateTime dateTime;
    @SerializedName("productsNumber")
    public int productsNumber;

}
