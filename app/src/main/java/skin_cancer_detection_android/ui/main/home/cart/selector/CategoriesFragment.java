package skin_cancer_detection_android.ui.main.home.cart.selector;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import skin_cancer_detection_android.net.model.ProductCategory;
import skin_cancer_detection_android.net.service.ProductService;
import skin_cancer_detection_android.ui.common.ProgressDialog;
import skin_cancer_detection_android.ui.main.MainActivity;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesFragment extends Fragment implements CategoriesAdapter.ClickListener {

    @BindView(R.id.categoryRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private Unbinder unbinder;
    private CategoriesAdapter adapter = new CategoriesAdapter();
    private List<ProductCategory> categories;
    private List<Product> cartProducts;
    private ProductService productService = RetrofitClient.getRetrofitInstance().create(ProductService.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        unbinder = ButterKnife.bind(this, view);

        adapter.setCategories(categories);
        adapter.setCartProducts(cartProducts);
        adapter.setOnCategoryClickListener(this);

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onCategoryClicked(ProductCategory category) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();

        productService.getByCategoryId(category.id).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {

                if (response.isSuccessful()) {

                    List<Product> products = response.body();
                    ProductsFragment fragment = new ProductsFragment();
                    fragment.setProducts(products);
                    fragment.setCartProducts(cartProducts);
                    fragment.setProductCategory(category);
                    ((MainActivity) requireActivity()).setFragment(fragment);

                } else {
                    Toast.makeText(getContext(), ErrorHandler.getServerError(response), Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    public void setCategories(List<ProductCategory> categories) {
        this.categories = categories;
    }

    public void setCartProducts(List<Product> cartProducts) {
        this.cartProducts = cartProducts;
    }
}
