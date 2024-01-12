package skin_cancer_detection_android.ui.main.homescd;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import skin_cancer_detection_android.Constants;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.ErrorHandler;
import skin_cancer_detection_android.net.ImageHandler;
import skin_cancer_detection_android.net.Session;
import skin_cancer_detection_android.net.client.RetrofitClient;
import skin_cancer_detection_android.net.model.Result;
import skin_cancer_detection_android.net.model.User;
import skin_cancer_detection_android.net.service.AuthService;
import skin_cancer_detection_android.net.service.FileService;
import skin_cancer_detection_android.net.service.ResultService;
import skin_cancer_detection_android.net.service.UserService;
import skin_cancer_detection_android.ui.common.ProgressDialog;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    @BindView(R.id.imagePhotoImageView)
    ImageView photoImageView;

    private OnImageUploadListener onImageUploadListener;
    private Unbinder unbinder;
    private User user = new User();
    private Bitmap bitmap;
    private Uri cameraImageUri;
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;
    private AuthService authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
    private UserService userService = RetrofitClient.getRetrofitInstance().create(UserService.class);
    private FileService fileService = RetrofitClient.getRetrofitInstance().create(FileService.class);

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);

        init();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    // Continuă cu procesarea imaginii sau trimiterea către backend
                    // Fără a afișa imaginea în ImageView
                    try {
                        InputStream imageStream = requireContext().getContentResolver().openInputStream(cameraImageUri);
                        // Continuă cu procesarea sau trimiterea imaginii
                    } catch (IOException e) {
                        requireActivity().runOnUiThread(this::showError);
                    }
                }
                break;
            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    // Continuă cu procesarea imaginii sau trimiterea către backend
                    // Fără a afișa imaginea în ImageView
                    try {
                        Uri imageUri = data.getData();
                        if (imageUri == null) {
                            return;
                        }
                        InputStream imageStream = requireContext().getContentResolver().openInputStream(imageUri);
                        // Continuă cu procesarea sau trimiterea imaginii
                    } catch (IOException e) {
                        requireActivity().runOnUiThread(this::showError);
                    }
                    break;
                }
        }
    }



    @OnClick(R.id.imagePhotoImageView)
    void onButtonClicked() {
        String[] items = {getString(R.string.source_camera), getString(R.string.source_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_source));
        builder.setItems(items, (dialog, item) -> {
            switch (item) {
                case 0:
                    if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST);
                    } else {
                        takePicture();
                    }
                    break;
                case 1:
                    pickPicture();
                    break;
            }
        });
        builder.show();
    }

    private void updateScreen(ProgressDialog progressDialog) {
        Call<User> userCall = userService.updateProfile(user);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Session.getInstance().setUser(response.body());
                    Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void init() {
        User sessionUser = Session.getInstance().getUser();
        user.id = sessionUser.id;
        user.imagePath = sessionUser.imagePath;

        String password = requireActivity().getApplicationContext()
                .getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
                .getString(Constants.USER_PASSWORD, "");

        ImageHandler.loadImage(photoImageView, user.imagePath, requireContext().getDrawable(R.drawable.item_placeholder_padding));

        bitmap = null;
        cameraImageUri = null;
    }

    private void uploadImageToBackend(ProgressDialog progressDialog) {
        // Asigură-te că calea imaginii este validă
        if (user.imagePath == null || user.imagePath.isEmpty()) {
            Toast.makeText(requireContext(), "Invalid image path", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        // Construiește obiectul Image cu calea imaginii și data curentă
        Result result = new Result();
        result.user = user;
        result.imagePath = user.imagePath;
        result.dateTime = LocalDateTime.now();

        // Realizează apelul către backend pentru încărcarea imaginii
        Call<Result> uploadCall = ResultService.updateScreen(result);
        uploadCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    // Gestionează răspunsul de la backend după încărcarea imaginii
                    Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                    // Comunică rezultatul către activitatea gazdă (MainActivity)
                    if (onImageUploadListener != null) {
                        onImageUploadListener.onImageUploaded(response.body());
                    }
                } else {
                    // Gestionează eroarea primită de la backend
                    Toast.makeText(requireContext(), ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                // Gestionează cazul în care apelul către backend eșuează
                progressDialog.dismiss();
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void pickPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private void takePicture() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "New picture with Camera");
        cameraImageUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void showError() {
        Toast.makeText(requireContext(), getString(R.string.message_default_error), Toast.LENGTH_SHORT).show();
    }

    public interface OnImageUploadListener {
        void onImageUploaded(Result result);
    }

    public void setOnImageUploadListener(OnImageUploadListener listener) {
        this.onImageUploadListener = listener;
    }



}