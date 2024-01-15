package skin_cancer_detection_android.ui.main.homescd;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import skin_cancer_detection_android.Constants;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.ErrorHandler;
import skin_cancer_detection_android.net.Session;
import skin_cancer_detection_android.net.client.RetrofitClient;
import skin_cancer_detection_android.net.model.Result;
import skin_cancer_detection_android.net.model.User;
import skin_cancer_detection_android.net.service.FileService;
import skin_cancer_detection_android.net.service.ResultService;
import skin_cancer_detection_android.ui.common.ProgressDialog;
import skin_cancer_detection_android.ui.main.common.result.ResultsFragment;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    @BindView(R.id.imagePhotoImageView)
    Button photoButton;

    private OnImageUploadListener onImageUploadListener;
    private Unbinder unbinder;
    private User user = new User();
    private Bitmap bitmap;
    private Uri cameraImageUri;
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
    private FileService fileService = RetrofitClient.getRetrofitInstance().create(FileService.class);
    private ResultService resultService = RetrofitClient.getRetrofitInstance().create(ResultService.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homescd, container, false);
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
                    try {
                        InputStream imageStream = requireContext().getContentResolver().openInputStream(cameraImageUri);
                        bitmap = BitmapFactory.decodeStream(imageStream);
                        // Poți afișa imaginea în ImageView dacă dorești
                        // photoImageView.setImageBitmap(bitmap);
                        processImage();
                    } catch (IOException e) {
                        requireActivity().runOnUiThread(this::showError);
                    }
                }
                break;
            case GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        Uri imageUri = data.getData();
                        if (imageUri == null) {
                            return;
                        }
                        InputStream imageStream = requireContext().getContentResolver().openInputStream(imageUri);
                        bitmap = BitmapFactory.decodeStream(imageStream);
                        // Poți afișa imaginea în ImageView dacă dorești
                        // photoImageView.setImageBitmap(bitmap);
                        processImage();
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

    public void init() {
        User sessionUser = Session.getInstance().getUser();
        user.id = sessionUser.id;
        user.imagePath = sessionUser.imagePath;

        String password = requireActivity().getApplicationContext()
                .getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
                .getString(Constants.USER_PASSWORD, "");

        // Afișează textul dorit pe buton (preluat din @string/home_image)
        photoButton.setText(getString(R.string.home_image));

        bitmap = null;
        cameraImageUri = null;
    }

    private void processImage() {
        // Realizează procesarea imaginii și trimiterea către backend
        // După procesare, afișează rezultatele în fragmentul ResultsFragment
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();

        // Construiește obiectul Result cu calea imaginii și data curentă
        Result result = new Result();
        result.user = user;
        result.dateTime = LocalDateTime.now();

        // Realizează apelul către backend pentru încărcarea imaginii procesate
        MultipartBody.Part part = FileService.getPartFromBitmap(bitmap, "imagescd_");
        Call<String> pathCall = fileService.saveImage(part);
        pathCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    // Actualizează calea imaginii în obiectul Result
                    result.imagePath = response.body();

                    // Salvare rezultat în baza de date
                    saveResultInDatabase(result);
                } else {
                    showErrorDialog("The image was not processed. There was an error.");
                    init();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

//    private void processImage() {
//        // Realizează procesarea imaginii și trimiterea către backend
//        // După procesare, afișează rezultatele în fragmentul ResultsFragment
//        ProgressDialog progressDialog = new ProgressDialog(requireContext());
//        progressDialog.show();
//
//        // Construiește obiectul Result cu calea imaginii și data curentă
//        Result result = new Result();
//        result.user = user;
//        result.imagePath = "";
//        result.dateTime = LocalDateTime.now();
//
//        // Realizează apelul către backend pentru încărcarea imaginii procesate
//        MultipartBody.Part part = FileService.getPartFromBitmap(bitmap, "imagescd_");
//        Call<String> path = fileService.saveImage(part);
//        path.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                if (response.isSuccessful()) {
//                    result.imagePath = response.body();
//                    // După ce primești calea imaginii, poți să o folosești cum dorești
//                    // result.imagePath = response.body();
//                    // Poți să continui cu afișarea rezultatelor
//                    if (onImageUploadListener != null) {
//                        onImageUploadListener.onImageUploaded(result);
//                    } else {
//                        showErrorDialog("The image was not processed. There was an error.");
//                        init();
//                    }
//
//                } else {
//                    progressDialog.dismiss();
//                    Toast.makeText(getContext(), ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
//                }
//                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                progressDialog.dismiss();
//                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    private void saveResultInDatabase(Result result) {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();
        Call<Result> saveCall = resultService.save(result);
        saveCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    // Verifică dacă răspunsul conține un obiect Result valid
                    Result savedResult = response.body();
                    if (savedResult != null) {
                        // Acum poți să deschizi fragmentul ResultsFragment cu informațiile salvate
                        if (onImageUploadListener != null) {
                            onImageUploadListener.onImageUploaded(savedResult);
                        }
                    } else {
                        // Răspunsul nu conține un obiect Result valid
                        showErrorDialog("The image was not processed. There was an error.");
                        init();
                    }
                } else {
                    // Răspunsul de la server nu a fost de succes
                    Toast.makeText(getContext(), ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Închide dialogul, nu trebuie să faci nimic special aici
                })
                .show();
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
