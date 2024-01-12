package skin_cancer_detection_android.ui.main.common.result;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.model.Result;

public class HistoryResultsAdapter extends RecyclerView.Adapter<HistoryResultsAdapter.ResultViewHolder> {

    private List<Result> results;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
    private HistoryResultsAdapter.ResultClickListener listener;

    @NonNull
    @Override
    public HistoryResultsAdapter.ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_result, parent, false);
        return new HistoryResultsAdapter.ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryResultsAdapter.ResultViewHolder holder, int position) {
        Result result = results.get(position);
        holder.dateTimeTextView.setText(result.dateTime.format(dateTimeFormatter));

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onResultClicked(result));
        }
    }

    @Override
    public int getItemCount() {
        return results == null ? 0 : results.size();
    }

    public void setResults(List<Result> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    public void setOnResultClickListener(ResultClickListener listener) {
//        this.listener = listener;
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemHistoryResultDateTextView)
        TextView dateTimeTextView;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public interface ResultClickListener {
        void onResultClicked(Result result);
    }
}
