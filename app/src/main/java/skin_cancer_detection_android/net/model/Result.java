package skin_cancer_detection_android.net.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Result {
    @SerializedName("id")
    public Long id;
    @SerializedName("user")
    public User user;
    @SerializedName("malign")
    public String malign;
    @SerializedName("benign")
    public String benign;
    @SerializedName("dateTime")
    public LocalDateTime dateTime;
    @SerializedName("imagePath")
    public String imagePath;
}
