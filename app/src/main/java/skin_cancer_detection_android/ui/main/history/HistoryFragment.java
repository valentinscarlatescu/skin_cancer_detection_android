package skin_cancer_detection_android.ui.main.history;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.Session;
import skin_cancer_detection_android.net.client.RetrofitClient;
import skin_cancer_detection_android.net.model.Result;
import skin_cancer_detection_android.net.model.User;
import skin_cancer_detection_android.net.service.ResultService;
import skin_cancer_detection_android.ui.main.MainActivity;
import skin_cancer_detection_android.ui.main.history.HistoryResultsAdapter;
import skin_cancer_detection_android.ui.main.common.result.ResultsFragment;

public class HistoryFragment extends Fragment implements HistoryResultsAdapter.ResultClickListener {

    @BindView(R.id.historyRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;


    private Unbinder unbinder;
    private List<Result> results;
    private User user = new User();
    private HistoryResultsAdapter historyResultsAdapter = new HistoryResultsAdapter();
    private ResultService resultService = RetrofitClient.getRetrofitInstance().create(ResultService.class);
    User sessionUser = Session.getInstance().getUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        unbinder = ButterKnife.bind(this, view);

        user.id = sessionUser.id;

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(historyResultsAdapter);

        historyResultsAdapter.setResults(results);
        historyResultsAdapter.setOnResultClickListener(this);

        resultService.getResults(user.id).enqueue(new Callback<List<Result>>() {
            @Override
            public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                if (response.isSuccessful()) {
                    HistoryFragment.this.results = response.body();
                    historyResultsAdapter.setResults(HistoryFragment.this.results);
                    historyResultsAdapter.setOnResultClickListener(HistoryFragment.this);
                }
            }

            @Override
            public void onFailure(Call<List<Result>> call, Throwable t) {

            }
        });
        init();
        return view;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    void init() {

        user.id = sessionUser.id;
        // Verifică dacă utilizatorul este null
        if (user != null) {
            resultService.getResults(user.id).enqueue(new Callback<List<Result>>() {
                @Override
                public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                    if (response.isSuccessful()) {
                        results = response.body();
                        historyResultsAdapter.setResults(results);
                        historyResultsAdapter.setOnResultClickListener(HistoryFragment.this);
                    }
                }

                @Override
                public void onFailure(Call<List<Result>> call, Throwable t) {
                    // Tratează eroarea în funcție de nevoile tale
                }
            });
        }
    }

    @Override
    public void onResultClicked(Result result) {
        ResultsFragment fragment = new ResultsFragment();
        fragment.setResults(result);
        ((MainActivity) requireActivity()).setFragment(fragment);
    }

}