package skin_cancer_detection_android.ui.main.history;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.model.Result;
import skin_cancer_detection_android.ui.main.MainActivity;
import skin_cancer_detection_android.ui.main.common.result.HistoryResultsAdapter;
import skin_cancer_detection_android.ui.main.common.result.ResultsFragment;

public class HistoryFragment extends Fragment implements HistoryResultsAdapter.ResultClickListener {

    @BindView(R.id.historyRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private Unbinder unbinder;
    private List<Result> results;
    private HistoryResultsAdapter resultsAdapter = new HistoryResultsAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, view);

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(resultsAdapter);

        resultsAdapter.setResults(results);
//        resultsAdapter.setOnResultClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public void onResultClicked(Result result) {
        ResultsFragment fragment = new ResultsFragment();
        fragment.setResults(result);
        ((MainActivity) requireActivity()).setFragment(fragment);
    }

}