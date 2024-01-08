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
import skin_cancer_detection_android.ui.main.common.product.ProductDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {

    private List<Product> products;
    private List<Product> cartProducts;
    private Listener listener;

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductsAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        ImageHandler.loadImage(holder.photoImageView, product.imagePath, holder.itemView.getContext().getDrawable(R.drawable.item_placeholder_padding));
        holder.nameTextView.setText(product.name);

        if (cartProducts.contains(product)) {
            holder.deleteImageView.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getColor(R.color.colorProductBackground));
        } else {
            holder.deleteImageView.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(0);
        }

        if (listener != null) {
            holder.itemView.setOnClickListener(view -> listener.onProductClicked(product));
            holder.deleteImageView.setOnClickListener(view -> listener.onProductDeleteClicked(product));
        }

        holder.detailsImageView.setOnClickListener(view -> {
            ProductDialog productDialog = new ProductDialog(holder.itemView.getContext(), product);
            productDialog.show();
        });

    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setCartProducts(List<Product> cartProducts) {
        this.cartProducts = cartProducts;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onProductClicked(Product product);

        void onProductDeleteClicked(Product product);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemProductImageView)
        ImageView photoImageView;
        @BindView(R.id.itemProductNameTextView)
        TextView nameTextView;
        @BindView(R.id.itemProductDeleteImageView)
        ImageView deleteImageView;
        @BindView(R.id.itemProductDetailsImageView)
        ImageView detailsImageView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
