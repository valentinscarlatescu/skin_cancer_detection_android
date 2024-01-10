//package skin_cancer_detection_android.ui.main.common.product;
//
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//import skin_cancer_detection_android.R;
//import skin_cancer_detection_android.net.model.Product;
//import butterknife.BindDrawable;
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.Unbinder;
//
//public class ProfileCartProductsFragment extends Fragment implements ProfileCartProductsAdapter.ProductClickListener {
//
//    @BindView(R.id.profileCartProductsRecyclerView)
//    RecyclerView recyclerView;
//
//    @BindDrawable(R.drawable.vertical_separator)
//    Drawable separator;
//
//    private Unbinder unbinder;
//    private List<Product> products;
//    private ProfileCartProductsAdapter productsAdapter = new ProfileCartProductsAdapter();
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_profile_cart_products, container, false);
//        unbinder = ButterKnife.bind(this, view);
//
//        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        decoration.setDrawable(separator);
//
//        recyclerView.addItemDecoration(decoration);
//        recyclerView.setAdapter(productsAdapter);
//
//        productsAdapter.setProducts(products);
//        productsAdapter.setOnProductClickListener(this);
//
//        return view;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        unbinder.unbind();
//    }
//
//    public void setProducts(List<Product> products) {
//        this.products = products;
//    }
//
//    @Override
//    public void onProductClicked(Product product) {
//        ProductDialog productDialog = new ProductDialog(getContext(), product);
//        productDialog.show();
//    }
//
//}
