package skin_cancer_detection_android.net.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class ProductCategory {

    @SerializedName("id")
    public Long id;
    @SerializedName("name")
    public String name;
    @SerializedName("imagePath")
    public String imagePath;
    @SerializedName("productsNumber")
    public int productsNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCategory category = (ProductCategory) o;
        return Objects.equals(id, category.id) &&
                Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
