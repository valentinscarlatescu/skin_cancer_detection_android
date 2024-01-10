//package skin_cancer_detection_android.ui.main.common.product;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//import skin_cancer_detection_android.R;
//import skin_cancer_detection_android.net.ImageHandler;
//import skin_cancer_detection_android.net.model.Product;
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//public class ProfileCartProductsAdapter extends RecyclerView.Adapter<ProfileCartProductsAdapter.ProductViewHolder> {
//
//    private List<Product> products;
//    private ProductClickListener listener;
//
//    @NonNull
//    @Override
//    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_cart_product, parent, false);
//        return new ProductViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
//        Product product = products.get(position);
//        ImageHandler.loadImage(holder.imageView, product.imagePath, holder.itemView.getContext().getDrawable(R.drawable.item_placeholder_padding));
//
//        holder.nameTextView.setText(product.name);
//
//        String productCategory = holder.itemView.getContext().getString(R.string.product_category);
//        holder.categoryTextView.setText(String.format(productCategory, product.productCategory.name));
//
//        if (listener != null) {
//            holder.itemView.setOnClickListener(v -> listener.onProductClicked(product));
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return products == null ? 0 : products.size();
//    }
//
//    public void setProducts(List<Product> products) {
//        this.products = products;
//    }
//
//    public void setOnProductClickListener(ProductClickListener listener) {
//        this.listener = listener;
//    }
//
//    class ProductViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.itemProfileCartProductImageView)
//        ImageView imageView;
//        @BindView(R.id.itemProfileCartProductNameTextView)
//        TextView nameTextView;
//        @BindView(R.id.itemProfileCartProductCategoryTextView)
//        TextView categoryTextView;
//
//        public ProductViewHolder(@NonNull View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//
//    }
//
//    interface ProductClickListener {
//        void onProductClicked(Product product);
//    }
//
//}
