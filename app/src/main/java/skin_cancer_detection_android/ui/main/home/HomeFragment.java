package skin_cancer_detection_android.ui.main.home;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.ErrorHandler;
import skin_cancer_detection_android.net.client.RetrofitClient;
import skin_cancer_detection_android.net.model.Product;
import skin_cancer_detection_android.net.service.ProductService;
import skin_cancer_detection_android.ui.main.MainActivity;
import skin_cancer_detection_android.ui.main.common.product.ProductDialog;
import skin_cancer_detection_android.ui.main.home.cart.CartFragment;
import skin_cancer_detection_android.ui.main.home.rec.RecCategoriesAdapter;
import skin_cancer_detection_android.ui.main.home.rec.RecType;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements RecCategoriesAdapter.ProductClickListener {

    @BindView(R.id.homeRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.homeCartProductsCounter)
    TextView cartProductsCountTextView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private static final int PRODUCT_MIN_PERCENTAGE = 0;
    private static final int PRODUCTS_LIMIT_PER_CATEGORY = 20;

    private Unbinder unbinder;
    private RecCategoriesAdapter adapter = new RecCategoriesAdapter();
    private ProductService productService = RetrofitClient.getRetrofitInstance().create(ProductService.class);

    private List<Product> cartProducts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        adapter.setListener(this);

        productService.getMostPopularProducts(PRODUCT_MIN_PERCENTAGE).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    List<Product> products = response.body();
                    if (products != null) {
                        adapter.setData(RecType.POPULARITY, products, PRODUCTS_LIMIT_PER_CATEGORY);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(getContext(), ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        cartProductsCountTextView.setText(String.valueOf(cartProducts.size()));

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onRecProductClicked(Product product) {
        if (cartProducts.contains(product)) {
            String message = String.format(getString(R.string.rec_product_already_in_cart), product.name);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        cartProducts.add(product);
        updateCartCounter();

        String message = String.format(getString(R.string.rec_product_added_in_cart), product.name);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRecProductDetails(Product product) {
        ProductDialog productDialog = new ProductDialog(getContext(), product);
        productDialog.show();
    }

    @OnClick(R.id.homeCartButton)
    void onCartClicked() {
        CartFragment fragment = new CartFragment();
        fragment.setProducts(cartProducts);
        ((MainActivity) requireActivity()).setFragment(fragment);
    }


    private void updateCartCounter() {
        long animDuration = 250L;
        cartProductsCountTextView.animate().alpha(0.1F).setDuration(animDuration).withEndAction(() -> {
            cartProductsCountTextView.setText(String.valueOf(cartProducts.size()));
            cartProductsCountTextView.animate().alpha(1).setDuration(animDuration);
        });
    }

}

