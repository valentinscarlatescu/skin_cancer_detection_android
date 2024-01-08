package skin_cancer_detection_android.ui.main.home.cart.selector;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.model.Product;
import skin_cancer_detection_android.net.model.ProductCategory;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProductsFragment extends Fragment implements ProductsAdapter.Listener {

    @BindView(R.id.productRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.productHeaderTextView)
    TextView headerTextView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private Unbinder unbinder;
    private ProductsAdapter adapter = new ProductsAdapter();
    private List<Product> products;
    private List<Product> cartProducts;
    private ProductCategory productCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        unbinder = ButterKnife.bind(this, view);

        adapter.setProducts(products);
        adapter.setCartProducts(cartProducts);
        adapter.setListener(this);

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        headerTextView.setText(productCategory.name);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onProductClicked(Product product) {
        if (cartProducts.contains(product)) {
            String message = String.format(getString(R.string.rec_product_already_in_cart), product.name);
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            return;
        }

        cartProducts.add(product);
        adapter.notifyItemChanged(products.indexOf(product));
    }

    @Override
    public void onProductDeleteClicked(Product product) {
        cartProducts.remove(product);
        adapter.notifyItemChanged(products.indexOf(product));
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setCartProducts(List<Product> cartProducts) {
        this.cartProducts = cartProducts;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

}
