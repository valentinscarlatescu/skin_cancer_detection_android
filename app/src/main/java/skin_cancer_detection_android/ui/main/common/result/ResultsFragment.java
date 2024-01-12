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

public class ResultsFragment extends Fragment implements ResultsAdapter.ResultClickListener {

    @BindView(R.id.resultImageView)
    ImageView photoImageView;
    @BindView(R.id.imageDateTextView)
    TextView dateTimeTextView;
    @BindView(R.id.malignPercentageTextView)
    TextView malignTextView;
    @BindView(R.id.benignPercentageTextView)
    TextView benignTextView;

    @BindString(R.string.home_image_date)
    String resultDateFormat;


    private Result result;
    private ResultsAdapter resultsAdapter = new ResultsAdapter();
    private Unbinder unbinder;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }


    void init() {
        Result sessionResult = Session.getInstance().getResult();
        result.id = sessionResult.id;
        result.imagePath = sessionResult.imagePath;
        result.malign = sessionResult.malign;
        result.benign = sessionResult.benign;
        result.dateTime = sessionResult.dateTime;

        ImageHandler.loadImage(photoImageView, result.imagePath, requireContext().getDrawable(R.drawable.item_placeholder_padding));
        benignTextView.setText(result.benign);
        malignTextView.setText(result.malign);
        dateTimeTextView.setText(String.format(resultDateFormat, result.dateTime.format(dateTimeFormatter)));
    }

    private void showError() {
        Toast.makeText(requireContext(), getString(R.string.message_default_error), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onResultClicked(Result result) {
        ResultsFragment fragment = new ResultsFragment();
        fragment.setResults(result);
        ((MainActivity) requireActivity()).setFragment(fragment);
    }

    public void setResults(Result result) {
        if (result != null) {
            // Actualizează detaliile imaginii
            ImageHandler.loadImage(photoImageView, result.imagePath, requireContext().getDrawable(R.drawable.item_placeholder_padding));
            benignTextView.setText(result.benign);
            malignTextView.setText(result.malign);
            dateTimeTextView.setText(String.format(resultDateFormat, result.dateTime.format(dateTimeFormatter)));
        }
    }
}
