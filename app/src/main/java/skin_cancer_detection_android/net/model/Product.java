package skin_cancer_detection_android.net.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Product {

    @SerializedName("id")
    public Long id;
    @SerializedName("productCategory")
    public ProductCategory productCategory;
    @SerializedName("name")
    public String name;
    @SerializedName("averagePrice")
    public Integer averagePrice;
    @SerializedName("quantityType")
    public QuantityType quantityType;
    @SerializedName("imagePath")
    public String imagePath;
    @SerializedName("cartsNumber")
    public int cartsNumber;
    @SerializedName("percentage")
    public float percentage;
    @SerializedName("commonCartsNumber")
    public int commonCartsNumber;
    @SerializedName("commonPercentage")
    public int commonPercentage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) &&
                Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

}
