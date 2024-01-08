package skin_cancer_detection_android.net.model;

import androidx.annotation.StringRes;

import skin_cancer_detection_android.R;

public enum Gender {

    MALE(R.string.gender_male), FEMALE(R.string.gender_female);

    @StringRes
    int name;

    Gender(@StringRes int name) {
        this.name = name;
    }

    public int getName() {
        return name;
    }

}
