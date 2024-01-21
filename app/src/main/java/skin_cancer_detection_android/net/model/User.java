package skin_cancer_detection_android.net.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class User {

    @SerializedName("id")
    public Long id;
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;
    @SerializedName("firstName")
    public String firstName;
    @SerializedName("lastName")
    public String lastName;
    @SerializedName("gender")
    public Gender gender;
    @SerializedName("joinDateTime")
    public LocalDateTime joinDateTime;
    @SerializedName("imagePath")
    public String imagePath;
}
