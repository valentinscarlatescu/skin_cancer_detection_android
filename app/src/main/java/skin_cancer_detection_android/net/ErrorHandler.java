package skin_cancer_detection_android.net;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import skin_cancer_detection_android.Constants;
import retrofit2.Response;

public class ErrorHandler {

    public static String getServerError(Response<?> response) {
        try {
            JSONObject jObjError = new JSONObject(response.errorBody().string());
            return jObjError.get("status") + ": " + jObjError.get("message");
        } catch (JSONException | IOException e) {
            return Constants.DEFAULT_ERROR;
        }
    }

}
