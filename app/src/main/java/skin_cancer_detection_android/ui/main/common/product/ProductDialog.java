package skin_cancer_detection_android.ui.main.common.product;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.ImageHandler;
import skin_cancer_detection_android.net.model.Product;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDialog extends Dialog {

    @BindView(R.id.dialogProductDetailsImageView)
    ImageView imageView;
    @BindView(R.id.dialogProductDetailsNameTextView)
    TextView nameTextView;
    @BindView(R.id.dialogProductDetailsCategoryTextView)
    TextView categoryTextView;
    @BindView(R.id.dialogProductDetailsPriceTextView)
    TextView priceTextView;
    @BindView(R.id.dialogProductDetailsCartsNumberTextView)
    TextView cartsNumberTextView;
    @BindView(R.id.dialogProductDetailsAdditionalInfoTextView)
    TextView additionalInfoTextView;


    @BindString(R.string.product_name)
    String productNameFormat;
    @BindString(R.string.product_category)
    String productCategoryFormat;
    @BindString(R.string.product_carts_number)
    String cartsNumberFormat;
    @BindString(R.string.product_additional_info)
    String additionalInfoFormat;

    private Product product;
    private String additionalInfo;

    public ProductDialog(@NonNull Context context, Product product) {
        super(context);
        this.product = product;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_product);
        ButterKnife.bind(this);

        ImageHandler.loadImage(imageView, product.imagePath, getContext().getDrawable(R.drawable.item_placeholder_padding));
        nameTextView.setText(String.format(productNameFormat, product.name));

        categoryTextView.setText(String.format(productCategoryFormat, product.productCategory.name));
        priceTextView.setText(String.format(getContext().getString(product.quantityType.getName()), product.averagePrice));
        cartsNumberTextView.setText(String.format(cartsNumberFormat, product.cartsNumber));

        if (additionalInfo != null) {
            additionalInfoTextView.setText(String.format(additionalInfoFormat, additionalInfo));
        } else {
            additionalInfoTextView.setVisibility(View.GONE);
        }

        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
