package skin_cancer_detection_android.net.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Image {
    @SerializedName("id")
    public Long id;
    @SerializedName("user")
    public User user;
    @SerializedName("dateTime")
    public LocalDateTime dateTime;
    @SerializedName("imagePath")
    public String imagePath;
}
