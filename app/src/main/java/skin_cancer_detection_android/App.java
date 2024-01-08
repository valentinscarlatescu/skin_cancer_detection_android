package skin_cancer_detection_android;

import android.app.Application;

import com.squareup.picasso.Picasso;

import skin_cancer_detection_android.net.client.PicassoClient;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Picasso.setSingletonInstance(PicassoClient.getPicasso(this));
    }

}
