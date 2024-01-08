package skin_cancer_detection_android.ui.main.home.cart;

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

public class CartProductsAdapter extends RecyclerView.Adapter<CartProductsAdapter.CartProductViewHolder> {

    private List<Product> products;
    private CartProductsListener listener;

    @NonNull
    @Override
    public CartProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
        return new CartProductsAdapter.CartProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductViewHolder holder, int position) {
        Product product = products.get(position);

        ImageHandler.loadImage(holder.photoImageView, product.imagePath, holder.itemView.getContext().getDrawable(R.drawable.item_placeholder_padding));
        holder.nameTextView.setText(product.name);

        if (listener != null) {
            holder.itemView.setOnClickListener(view -> listener.onCartProductClicked(product));
            holder.deleteImageView.setOnClickListener(view -> listener.onCartProductDeleteClicked(product));
        }
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setListener(CartProductsListener listener) {
        this.listener = listener;
    }

    interface CartProductsListener {
        void onCartProductClicked(Product product);

        void onCartProductDeleteClicked(Product product);
    }

    class CartProductViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemCartProductImageView)
        ImageView photoImageView;
        @BindView(R.id.itemCartProductTextView)
        TextView nameTextView;
        @BindView(R.id.itemCartDeleteImageView)
        ImageView deleteImageView;

        public CartProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
