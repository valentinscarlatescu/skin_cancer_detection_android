package skin_cancer_detection_android.net.client.interceptor;

import com.auth0.android.jwt.JWT;

import java.io.IOException;

import skin_cancer_detection_android.net.Session;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request mainRequest = chain.request();

        String path = mainRequest.url().url().getPath();
        if (path.endsWith("/auth/login") || path.endsWith("/auth/register")) {
            return chain.proceed(mainRequest);
        }

        String token = Session.getInstance().getToken();
        JWT jwt = new JWT(token);
        if (!jwt.isExpired(1000)) {
            Request newRequest = mainRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        }

        System.out.println("Token expired " + token);
        return chain.proceed(mainRequest);
    }

}
