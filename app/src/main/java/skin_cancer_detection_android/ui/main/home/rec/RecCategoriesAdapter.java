package skin_cancer_detection_android.ui.main.home.rec;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.model.Product;
import skin_cancer_detection_android.net.model.ProductCategory;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecCategoriesAdapter extends RecyclerView.Adapter<RecCategoriesAdapter.RecCategoryViewHolder>
        implements RecCategoryProductsAdapter.ProductClickListener {

    private List<Product> products;
    private List<ProductCategory> categories;
    private int productsLimit;
    private ProductClickListener listener;
    private RecType type;

    @NonNull
    @Override
    public RecCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rec_category, parent, false);
        return new RecCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecCategoryViewHolder holder, int position) {
        ProductCategory category = categories.get(position);

        holder.categoryTextView.setText(category.name);

        RecCategoryProductsAdapter productsAdapter = new RecCategoryProductsAdapter();
        holder.categoryRecyclerView.setAdapter(productsAdapter);

        List<Product> categoryProducts = products.stream()
                .filter(product -> category.id == null || product.productCategory.name.equals(category.name))
                .limit(productsLimit)
                .collect(Collectors.toList());
        productsAdapter.setProducts(categoryProducts);
        productsAdapter.setType(type);

        productsAdapter.setListener(this);
    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    @Override
    public void onProductClicked(Product product) {
        if (listener != null) {
            listener.onRecProductClicked(product);
        }
    }

    @Override
    public void onProductDetailsClicked(Product product) {
        if (listener != null) {
            listener.onRecProductDetails(product);
        }
    }

    public void setData(RecType type, List<Product> products, int productsLimit) {
        this.type = type;
        this.productsLimit = productsLimit;
        this.products = products;
        this.categories = new ArrayList<>();

        if (type == RecType.POPULARITY) {
            ProductCategory popularityCategory = new ProductCategory();
            popularityCategory.name = "Most popular products";
            this.categories.add(popularityCategory);
        }

        List<ProductCategory> realCategories = this.products.stream()
                .map(product -> product.productCategory)
                .distinct()
                .sorted((pc1, pc2) -> pc1.name.compareTo(pc2.name))
                .collect(Collectors.toList());

        this.categories.addAll(realCategories);
    }

    public void setListener(ProductClickListener listener) {
        this.listener = listener;
    }

    public interface ProductClickListener {
        void onRecProductClicked(Product product);

        void onRecProductDetails(Product product);
    }

    public void clearCategories() {
        categories.clear();
    }

    class RecCategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemRecCategoryProductsTextView)
        TextView categoryTextView;
        @BindView(R.id.itemRecCategoryProductsRecyclerView)
        RecyclerView categoryRecyclerView;

        public RecCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
