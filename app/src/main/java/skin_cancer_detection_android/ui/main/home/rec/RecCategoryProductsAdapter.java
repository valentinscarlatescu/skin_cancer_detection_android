package skin_cancer_detection_android.ui.main.home.rec;

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
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecCategoryProductsAdapter extends RecyclerView.Adapter<RecCategoryProductsAdapter.RecCategoryProductViewHolder> {

    private List<Product> products;
    private ProductClickListener listener;
    private RecType type;

    @NonNull
    @Override
    public RecCategoryProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rec_category_product, parent, false);
        return new RecCategoryProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecCategoryProductViewHolder holder, int position) {
        Product product = products.get(position);

        ImageHandler.loadImage(holder.photoImageView, product.imagePath, holder.itemView.getContext().getDrawable(R.drawable.item_placeholder_padding));
        holder.productTextView.setText(product.name);

        switch (type) {
            case POPULARITY:
                String percentage = (int) product.percentage + "%";
                holder.percentageTextView.setText(percentage);
                break;
            case CONTENT:
                String commonPercentage = product.commonPercentage + "%";
                holder.percentageTextView.setText(commonPercentage);
                break;
        }

        if (listener != null) {
            holder.percentageTextView.setOnClickListener(view -> listener.onProductDetailsClicked(product));
            holder.itemView.setOnClickListener(view -> listener.onProductClicked(product));
        }
    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setListener(ProductClickListener listener) {
        this.listener = listener;
    }

    public void setType(RecType type) {
        this.type = type;
    }

    interface ProductClickListener {
        void onProductClicked(Product product);

        void onProductDetailsClicked(Product product);
    }

    class RecCategoryProductViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemRecProductImageView)
        ImageView photoImageView;
        @BindView(R.id.itemRecCategoryProductTextView)
        TextView productTextView;
        @BindView(R.id.itemRecCategoryProductPercentageTextView)
        TextView percentageTextView;

        public RecCategoryProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
