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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import okhttp3.MultipartBody;
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
import skin_cancer_detection_android.net.service.FileService;
import skin_cancer_detection_android.net.service.ResultService;
import skin_cancer_detection_android.ui.common.ProgressDialog;
import skin_cancer_detection_android.ui.main.MainActivity;
import skin_cancer_detection_android.ui.main.common.result.ResultsFragment;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    @BindView(R.id.resultHomeImageView)
    ImageView resultHomeImageView;
    @BindView(R.id.imagePhotoImageView)
    Button photoButton;
    @BindView(R.id.validatePhotoImageView)
    Button validatePhotoButton;

    private Unbinder unbinder;
    private Result result = new Result();
    private Bitmap bitmap;
    private Uri cameraImageUri;
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
    private FileService fileService = RetrofitClient.getRetrofitInstance().create(FileService.class);
    private ResultService resultService = RetrofitClient.getRetrofitInstance().create(ResultService.class);
    private User user = Session.getInstance().getUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homescd, container, false);
        unbinder = ButterKnife.bind(this, view);

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
                        resultHomeImageView.setImageBitmap(bitmap);
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
                        resultHomeImageView.setImageBitmap(bitmap);
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
        ImageHandler.loadImage(resultHomeImageView, result.imagePath, requireContext().getDrawable(R.drawable.item_homescd));

        bitmap = null;
        cameraImageUri = null;
    }


    @OnClick(R.id.validatePhotoImageView)
    void processImage() {
        result.user = user;
        result.dateTime = LocalDateTime.now();

        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.show();

        if (bitmap == null) {
            saveResultInDatabase(progressDialog);
        } else {
            MultipartBody.Part part = FileService.getPartFromBitmap(bitmap, "imagescd_");
            Call<String> pathCall = fileService.saveImage(part);
            pathCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        result.imagePath = response.body();
                        saveResultInDatabase(progressDialog);
                        init();
                    } else {
                        progressDialog.dismiss();
                        String errorMessage = response.body() != null && !response.body().isEmpty() ?
                                response.body() : ErrorHandler.getServerError(response);
                        Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void saveResultInDatabase(ProgressDialog progressDialog) {
        Call<Result> saveCall = resultService.processImage(result);
        saveCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    Log.d("ImageViewDebug", "Result.imagePath in saveCall onResponse(): " + result.imagePath);
                    Result latestResult = response.body();
                    openResultsFragment(latestResult);

                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("ServerError", errorBody);
                        Toast.makeText(getContext(), "Error: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void openResultsFragment(Result result) {
        ResultsFragment fragment = new ResultsFragment();
        fragment.setResults(result);
        ((MainActivity) requireActivity()).setFragment(fragment);

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

}
