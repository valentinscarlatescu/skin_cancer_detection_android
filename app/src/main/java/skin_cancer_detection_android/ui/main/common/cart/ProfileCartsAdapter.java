//package skin_cancer_detection_android.ui.main.common.cart;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.time.format.DateTimeFormatter;
//import java.time.format.FormatStyle;
//import java.util.List;
//
//import skin_cancer_detection_android.R;
//import skin_cancer_detection_android.net.model.Cart;
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//public class ProfileCartsAdapter extends RecyclerView.Adapter<ProfileCartsAdapter.CartViewHolder> {
//
//    private List<Cart> carts;
//    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
//    private CartClickListener listener;
//
//    @NonNull
//    @Override
//    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_cart, parent, false);
//        return new CartViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
//        Cart cart = carts.get(position);
//        holder.productsNumberTextView.setText(String.valueOf(cart.productsNumber));
//        holder.dateTimeTextView.setText(cart.dateTime.format(dateTimeFormatter));
//
//        StringBuilder builder = new StringBuilder();
//        cart.cartProducts.forEach(product -> {
//            builder.append(product.name);
//            builder.append(", ");
//        });
//        String result = builder.toString();
//        result = result.substring(0, result.length() - 2);
//        holder.productsTextView.setText(result);
//
//        if (listener != null) {
//            holder.itemView.setOnClickListener(v -> listener.onCartClicked(cart));
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return carts == null ? 0 : carts.size();
//    }
//
//    public void setCarts(List<Cart> carts) {
//        this.carts = carts;
//    }
//
//    public void setOnCartClickListener(CartClickListener listener) {
//        this.listener = listener;
//    }
//
//    class CartViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.itemProfileCartsProductsTextView)
//        TextView productsTextView;
//        @BindView(R.id.itemProfileCartsDateTimeTextView)
//        TextView dateTimeTextView;
//        @BindView(R.id.itemProfileProductsNumberTextView)
//        TextView productsNumberTextView;
//
//        public CartViewHolder(@NonNull View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//
//    }
//
//    interface CartClickListener {
//        void onCartClicked(Cart cart);
//    }
//}
