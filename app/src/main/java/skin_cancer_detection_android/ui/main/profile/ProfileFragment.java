package skin_cancer_detection_android.ui.main.profile;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import skin_cancer_detection_android.Constants;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.ErrorHandler;
import skin_cancer_detection_android.net.ImageHandler;
import skin_cancer_detection_android.net.Session;
import skin_cancer_detection_android.net.client.RetrofitClient;
import skin_cancer_detection_android.net.model.Cart;
import skin_cancer_detection_android.net.model.Gender;
import skin_cancer_detection_android.net.model.User;
import skin_cancer_detection_android.net.service.AuthService;
import skin_cancer_detection_android.net.service.CartService;
import skin_cancer_detection_android.net.service.FileService;
import skin_cancer_detection_android.net.service.UserService;
import skin_cancer_detection_android.ui.auth.AuthActivity;
import skin_cancer_detection_android.ui.common.ProgressDialog;
import skin_cancer_detection_android.ui.main.MainActivity;
import skin_cancer_detection_android.ui.main.common.cart.ProfileCartsFragment;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    @BindView(R.id.profilePhotoImageView)
    ImageView photoImageView;
    @BindView(R.id.profileIdTextView)
    TextView idTextView;
    @BindView(R.id.profileEmailTextView)
    TextView emailTextView;
    @BindView(R.id.profilePasswordTextView)
    TextView passwordTextView;
    @BindView(R.id.profileFirstNameEditText)
    EditText firstNameEditText;
    @BindView(R.id.profileLastNameEditText)
    EditText lastNameEditText;
    @BindView(R.id.profileGenderTextView)
    TextView genderTextView;
    @BindView(R.id.profileJoinDateTextView)
    TextView joinDateTextView;
    @BindView(R.id.profileCartsNumberTextView)
    TextView cartsNumberTextView;

    @BindString(R.string.profile_user_id)
    String userIdFormat;
    @BindString(R.string.profile_user_email)
    String userEmailFormat;
    @BindString(R.string.profile_user_password)
    String userPasswordFormat;
    @BindString(R.string.profile_user_join_date)
    String userJoinDateFormat;
    @BindString(R.string.carts_number)
    String cartsNumberFormat;

    private Unbinder unbinder;
    private User user = new User();
    private Bitmap bitmap;
    private Uri cameraImageUri;
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;
    private AuthService authService = RetrofitClient.getRetrofitInstance().create(AuthService.class);
    private UserService userService = RetrofitClient.getRetrofitInstance().create(UserService.class);
    private FileService fileService = RetrofitClient.getRetrofitInstance().create(FileService.class);
    private CartService cartService = RetrofitClient.getRetrofitInstance().create(CartService.class);

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
                    try {
                        InputStream imageStream = requireContext().getContentResolver().openInputStream(cameraImageUri);
                        bitmap = BitmapFactory.decodeStream(imageStream);
                        photoImageView.setImageBitmap(bitmap);
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
                        photoImageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        requireActivity().runOnUiThread(this::showError);
                    }
                    break;
                }
        }
    }

    @OnClick(R.id.profilePhotoImageView)
    void onProfilePhotoClicked() {
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

    @OnClick(R.id.profileGenderFrameLayout)
    void onGenderClicked() {
        user.gender = user.gender == null ? Gender.MALE : user.gender == Gender.MALE ? Gender.FEMALE : Gender.MALE;
        genderTextView.animate().alpha(0).setDuration(150)
                .withEndAction(() -> {
                    genderTextView.setText(getString(user.gender.getName()));
                    genderTextView.animate().alpha(1).setDuration(150);
                });
    }

    @OnClick(R.id.profileLogoutButton)
    void onLogoutClicked() {
        Activity activity = requireActivity();
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.show();
        Call<Void> voidCall = authService.logout();
        voidCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    Intent intent = new Intent(activity, AuthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    activity.finish();

                } else {
                    Toast.makeText(activity, ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @OnClick(R.id.profileViewCartsButton)
    void onViewCartsClicked() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();

        cartService.getByUserId(user.id).enqueue(new Callback<List<Cart>>() {
            @Override
            public void onResponse(Call<List<Cart>> call, Response<List<Cart>> response) {
                if (response.isSuccessful()) {
                    ProfileCartsFragment fragment = new ProfileCartsFragment();
                    fragment.setCarts(response.body());
                    ((MainActivity) requireActivity()).setFragment(fragment);
                } else {
                    Toast.makeText(getContext(), ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Cart>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void init() {
        User sessionUser = Session.getInstance().getUser();
        user.id = sessionUser.id;
        user.imagePath = sessionUser.imagePath;
        user.email = sessionUser.email;
        user.password = sessionUser.password;
        user.firstName = sessionUser.firstName;
        user.lastName = sessionUser.lastName;
        user.gender = sessionUser.gender;
        user.joinDateTime = sessionUser.joinDateTime;
        user.cartsNumber = sessionUser.cartsNumber;

        String password = requireActivity().getApplicationContext()
                .getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
                .getString(Constants.USER_PASSWORD, "");

        ImageHandler.loadImage(photoImageView, user.imagePath, requireContext().getDrawable(R.drawable.item_placeholder_padding));
        idTextView.setText(String.format(userIdFormat, user.id));
        emailTextView.setText(String.format(userEmailFormat, user.email));
        passwordTextView.setText(String.format(userPasswordFormat, password.replaceAll("(?s).", "\u2022")));
        firstNameEditText.setText(user.firstName);
        lastNameEditText.setText(user.lastName);
        genderTextView.setText(user.gender == null ? "" : getString(user.gender.getName()));
        joinDateTextView.setText(String.format(userJoinDateFormat, user.joinDateTime.format(dateTimeFormatter)));
        cartsNumberTextView.setText(String.format(cartsNumberFormat, user.cartsNumber));

        bitmap = null;
        cameraImageUri = null;
    }

    @OnClick(R.id.profileValidateButton)
    void updateUser() {
        user.firstName = firstNameEditText.getText().toString();
        user.lastName = lastNameEditText.getText().toString();

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();

        if (bitmap == null) {
            updateUser(progressDialog);
        } else {
            MultipartBody.Part part = FileService.getPartFromBitmap(bitmap, "user_");
            Call<String> path = fileService.saveImage(part);
            path.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        user.imagePath = response.body();
                        updateUser(progressDialog);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void updateUser(ProgressDialog progressDialog) {
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
