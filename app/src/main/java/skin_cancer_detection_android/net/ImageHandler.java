package skin_cancer_detection_android.net;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import skin_cancer_detection_android.Constants;

public class ImageHandler {

    public static void loadImage(ImageView image, String path) {
        loadImage(image, path, null, null);
    }

    public static void loadImage(ImageView image, String path, Drawable placeholder) {
        loadImage(image, path, placeholder, null);
    }

    public static void loadImage(ImageView image, String path, Callback callback) {
        loadImage(image, path, null, callback);
    }

    public static void loadImage(ImageView image, String path, Drawable placeholder, Callback callback) {
        if (path == null || path.isEmpty()) {
            image.setImageDrawable(placeholder);
            return;
        }
        String uri = Constants.BASE_URL + "api/image?path=" + path;
        Picasso.get()
                .load(uri)
                .placeholder(placeholder)
                .into(image, callback);
        image.setTag(path);
    }

}
