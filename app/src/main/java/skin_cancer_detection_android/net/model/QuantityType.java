package skin_cancer_detection_android.net.model;

import androidx.annotation.StringRes;

import skin_cancer_detection_android.R;

public enum QuantityType {

    UNIT(R.string.product_price_unit), WEIGHT(R.string.product_price_weight);

    @StringRes
    int name;

    QuantityType(@StringRes int name) {
        this.name = name;
    }

    public int getName() {
        return name;
    }
}
