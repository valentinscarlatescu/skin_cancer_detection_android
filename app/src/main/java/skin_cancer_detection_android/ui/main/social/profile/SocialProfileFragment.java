package skin_cancer_detection_android.ui.main.social.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.ErrorHandler;
import skin_cancer_detection_android.net.ImageHandler;
import skin_cancer_detection_android.net.client.RetrofitClient;
import skin_cancer_detection_android.net.model.Cart;
import skin_cancer_detection_android.net.model.User;
import skin_cancer_detection_android.net.service.CartService;
import skin_cancer_detection_android.ui.common.ProgressDialog;
import skin_cancer_detection_android.ui.main.MainActivity;
import skin_cancer_detection_android.ui.main.common.cart.ProfileCartsAdapter;
import skin_cancer_detection_android.ui.main.common.cart.ProfileCartsFragment;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocialProfileFragment extends Fragment {

    private Unbinder unbinder;
    private User user;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
    private CartService cartService = RetrofitClient.getRetrofitInstance().create(CartService.class);

    @BindView(R.id.socialProfilePhotoImageView)
    ImageView socialProfilePhotoImageView;
    @BindView(R.id.socialProfileFirstNameTextView)
    TextView socialProfileFirstNameText;
    @BindView(R.id.socialProfileLastNameTextView)
    TextView socialProfileLastNameText;
    @BindView(R.id.socialProfileGenderTextView)
    TextView socialProfileGenderText;
    @BindView(R.id.socialProfileJoinDateTextView)
    TextView socialProfileJoinDateTextView;
    @BindView(R.id.socialProfileCartsNumberTextView)
    TextView socialProfileCartsNumberTextView;

    @BindString(R.string.profile_user_join_date)
    String userJoinDateFormat;
    @BindString(R.string.carts_number)
    String cartsNumberFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social_profile, container, false);
        unbinder = ButterKnife.bind(this, view);

        init();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.socialProfileViewCartsButton)
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

    public void setUser(User user) {
        this.user = user;
    }

    void init() {
        ImageHandler.loadImage(socialProfilePhotoImageView, user.imagePath, requireContext().getDrawable(R.drawable.item_placeholder_padding));
        socialProfileFirstNameText.setText(user.firstName);
        socialProfileLastNameText.setText(user.lastName);
        socialProfileGenderText.setText(user.gender == null ? "" : getString(user.gender.getName()));
        socialProfileJoinDateTextView.setText(String.format(userJoinDateFormat, user.joinDateTime.format(dateTimeFormatter)));
        socialProfileCartsNumberTextView.setText(String.format(cartsNumberFormat, user.cartsNumber));
    }
}
