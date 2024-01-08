package skin_cancer_detection_android.ui.main.home.cart.selector;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.ImageHandler;
import skin_cancer_detection_android.net.model.Product;
import skin_cancer_detection_android.net.model.ProductCategory;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private List<ProductCategory> categories;
    private List<Product> cartProducts;
    private ClickListener listener;

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoriesAdapter.CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ProductCategory category = categories.get(position);

        ImageHandler.loadImage(holder.photoImageView, category.imagePath, holder.itemView.getContext().getDrawable(R.drawable.item_placeholder_padding));
        holder.nameTextView.setText(category.name);
        holder.productsNumberTextView.setText(String.valueOf(category.productsNumber));

        long cartProductsNumber = cartProducts.stream()
                .filter(product -> product.productCategory.equals(category))
                .count();

        if (cartProductsNumber > 0) {
            holder.cartProductsNumberTextView.setVisibility(View.VISIBLE);
            holder.cartProductsNumberTextView.setText(String.valueOf(cartProductsNumber));
        } else {
            holder.cartProductsNumberTextView.setVisibility(View.GONE);
        }

        if (listener != null) {
            holder.itemView.setOnClickListener(view -> listener.onCategoryClicked(category));
        }

    }

    @Override
    public int getItemCount() {
        return categories == null ? 0 : categories.size();
    }

    public void setCategories(List<ProductCategory> categories) {
        this.categories = categories;
    }

    public void setCartProducts(List<Product> cartProducts) {
        this.cartProducts = cartProducts;
    }

    public void setOnCategoryClickListener(ClickListener listener) {
        this.listener = listener;
    }

    interface ClickListener {
        void onCategoryClicked(ProductCategory category);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemCategoryImageView)
        ImageView photoImageView;
        @BindView(R.id.itemCategoryNameTextView)
        TextView nameTextView;
        @BindView(R.id.itemCategoryCartProductsNumberTextView)
        TextView cartProductsNumberTextView;
        @BindView(R.id.itemCategoryProductsNumberTextView)
        TextView productsNumberTextView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
