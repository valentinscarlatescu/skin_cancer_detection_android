package skin_cancer_detection_android.ui.main.common.result;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.ImageHandler;
import skin_cancer_detection_android.net.Session;
import skin_cancer_detection_android.net.model.Result;
import skin_cancer_detection_android.ui.main.MainActivity;
import skin_cancer_detection_android.ui.main.history.HistoryFragment;

public class ResultsFragment extends Fragment {

    private Result result;
    private Unbinder unbinder;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);

    @BindView(R.id.resultImageView)
    ImageView photoImageView;
    @BindView(R.id.imageDateTextView)
    TextView dateTimeTextView;
    @BindView(R.id.malignPercentageTextView)
    TextView malignTextView;
    @BindView(R.id.benignPercentageTextView)
    TextView benignTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        unbinder = ButterKnife.bind(this, view);

        init();

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    void init() {
        ImageHandler.loadImage(photoImageView, result.imagePath, requireContext().getDrawable(R.drawable.item_placeholder_padding));
        benignTextView.setText(String.valueOf(result.benign));
        malignTextView.setText(String.valueOf(result.malign));
        dateTimeTextView.setText(result.dateTime != null ? result.dateTime.format(dateTimeFormatter) : "DateTime is null");
    }

    public void setResults(Result result) {
        this.result = result;
    }
}
