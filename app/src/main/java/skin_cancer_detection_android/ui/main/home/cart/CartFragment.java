package skin_cancer_detection_android.ui.main.home.cart;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.ErrorHandler;
import skin_cancer_detection_android.net.Session;
import skin_cancer_detection_android.net.client.RetrofitClient;
import skin_cancer_detection_android.net.model.Cart;
import skin_cancer_detection_android.net.model.Product;
import skin_cancer_detection_android.net.model.ProductCategory;
import skin_cancer_detection_android.net.model.User;
import skin_cancer_detection_android.net.service.CartService;
import skin_cancer_detection_android.net.service.ProductCategoryService;
import skin_cancer_detection_android.net.service.ProductService;
import skin_cancer_detection_android.ui.common.ProgressDialog;
import skin_cancer_detection_android.ui.main.MainActivity;
import skin_cancer_detection_android.ui.main.common.product.ProductDialog;
import skin_cancer_detection_android.ui.main.home.cart.selector.CategoriesFragment;
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

public class CartFragment extends Fragment implements CartProductsAdapter.CartProductsListener, RecCategoriesAdapter.ProductClickListener {

    @BindView(R.id.cartRecyclerView)
    RecyclerView cartRecyclerView;

    @BindView(R.id.cartRecRecyclerView)
    RecyclerView recRecyclerView;

    @BindView(R.id.cartEmptyTextView)
    TextView cartEmptyTextView;

    @BindView(R.id.cartProductsNumberTextView)
    TextView cartProductsNumberTextView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private static final int PRODUCTS_LIMIT_PER_CATEGORY = 20;

    private Unbinder unbinder;
    private List<Product> products;

    private CartProductsAdapter cartProductsAdapter = new CartProductsAdapter();
    private RecCategoriesAdapter recCategoriesAdapter = new RecCategoriesAdapter();

    private CartService cartService = RetrofitClient.getRetrofitInstance().create(CartService.class);
    private ProductService productService = RetrofitClient.getRetrofitInstance().create(ProductService.class);
    private ProductCategoryService categoryService = RetrofitClient.getRetrofitInstance().create(ProductCategoryService.class);


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        unbinder = ButterKnife.bind(this, view);

        cartRecyclerView.setAdapter(cartProductsAdapter);
        cartProductsAdapter.setProducts(products);
        cartProductsAdapter.setListener(this);

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recRecyclerView.addItemDecoration(decoration);
        recRecyclerView.setAdapter(recCategoriesAdapter);
        recCategoriesAdapter.setListener(this);

        if (!products.isEmpty() && recCategoriesAdapter.getItemCount() == 0) {
            Handler handler = new Handler();
            handler.postDelayed(this::updateRecommendations, 400);
        }

        cartProductsNumberTextView.setText(String.valueOf(products.size()));
        updateView();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onRecProductClicked(Product product) {
        products.add(product);
        cartProductsAdapter.notifyItemInserted(cartProductsAdapter.getItemCount() - 1);
        cartProductsNumberTextView.setText(String.valueOf(products.size()));
        updateRecommendations();
    }

    @Override
    public void onRecProductDetails(Product product) {
        String info = String.format(getString(R.string.rec_product_content_info),
                product.name,
                product.commonCartsNumber);

        ProductDialog productDialog = new ProductDialog(getContext(), product);
        productDialog.setAdditionalInfo(info);
        productDialog.show();
    }

    @Override
    public void onCartProductClicked(Product product) {
        ProductDialog productDialog = new ProductDialog(getContext(), product);
        productDialog.show();
    }

    @Override
    public void onCartProductDeleteClicked(Product product) {
        int index = products.indexOf(product);
        products.remove(product);
        cartProductsAdapter.notifyItemRemoved(index);
        cartProductsNumberTextView.setText(String.valueOf(products.size()));

        updateView();
        if (!products.isEmpty()) {
            updateRecommendations();
        } else {
            removeRecommendations();
        }

    }

    @OnClick(R.id.cartAddProductButton)
    void onAddProductClicked() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();

        categoryService.getAll().enqueue(new Callback<List<ProductCategory>>() {
            @Override
            public void onResponse(Call<List<ProductCategory>> call, Response<List<ProductCategory>> response) {
                if (response.isSuccessful()) {

                    List<ProductCategory> categories = response.body();
                    CategoriesFragment fragment = new CategoriesFragment();
                    fragment.setCategories(categories);
                    fragment.setCartProducts(products);
                    ((MainActivity) requireActivity()).setFragment(fragment);

                } else {
                    Toast.makeText(getContext(), ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<ProductCategory>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    @OnClick(R.id.cartValidateButton)
    void onValidateClicked() {
        if (products.isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.cart_empty), Toast.LENGTH_LONG).show();
            return;
        }

        User currentUser = Session.getInstance().getUser();
        Cart cart = new Cart();
        cart.user = currentUser;
        cart.cartProducts = products;

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();

        cartService.save(cart).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    currentUser.cartsNumber++;
                    products.clear();

                    updateView();
                    removeRecommendations();

                    Toast.makeText(getContext(), getString(R.string.message_success), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                } else {
                    Toast.makeText(getContext(), ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    private void removeRecommendations() {
        recCategoriesAdapter.clearCategories();
        recCategoriesAdapter.notifyDataSetChanged();
    }

    private void updateRecommendations() {
        List<Long> productsIds = products.stream()
                .map(product -> product.id)
                .collect(Collectors.toList());

        productService.getRecommendations(productsIds).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {

                    List<Product> productRecs = response.body();
                    recCategoriesAdapter.setData(RecType.CONTENT, productRecs, PRODUCTS_LIMIT_PER_CATEGORY);
                    recCategoriesAdapter.notifyDataSetChanged();
                    recRecyclerView.scheduleLayoutAnimation();

                } else {
                    Toast.makeText(getContext(), ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateView() {
        if (cartEmptyTextView == null || cartRecyclerView == null) {
            return;
        }

        if (products.isEmpty()) {
            cartEmptyTextView.setVisibility(View.VISIBLE);
            cartRecyclerView.setVisibility(View.GONE);
        } else {
            cartRecyclerView.setVisibility(View.VISIBLE);
            cartEmptyTextView.setVisibility(View.GONE);
            cartProductsNumberTextView.setVisibility(View.VISIBLE);
        }
    }


}
