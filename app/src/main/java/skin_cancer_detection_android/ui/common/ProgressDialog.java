package skin_cancer_detection_android.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import skin_cancer_detection_android.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressDialog extends Dialog {

    @BindView(R.id.dialogProgressBar)
    ProgressBar progressBar;

    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        ButterKnife.bind(this);
        setCancelable(false);

        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        progressBar.animate().alpha(0).withEndAction(super::dismiss);
    }
}
