//package skin_cancer_detection_android.ui.main.common.result;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.time.format.DateTimeFormatter;
//import java.time.format.FormatStyle;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import skin_cancer_detection_android.R;
//import skin_cancer_detection_android.net.ImageHandler;
//import skin_cancer_detection_android.net.model.Result;
//
//public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultViewHolder> {
//
//    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT);
//    private List<Result> results;
//    private ResultClickListener listener;
//
//    @NonNull
//    @Override
//    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_result, parent, false);
//        return new ResultViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
//        Result result = results.get(position);
//        holder.malignTextView.setText(result.malign);
//        holder.benignTextView.setText(result.benign);
//        holder.dateTimeTextView.setText(result.dateTime.format(dateTimeFormatter));
//        ImageHandler.loadImage(holder.photoImageView, result.imagePath, holder.itemView.getContext().getDrawable(R.drawable.item_placeholder_padding));
//
//        if (listener != null) {
//            holder.itemView.setOnClickListener(v -> listener.onResultClicked(result));
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return results == null ? 0 : results.size();
//    }
//
//
//    public void setOnResultClickListener(ResultClickListener listener) {
//        this.listener = listener;
//    }
//
//    public class ResultViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.resultImageView)
//        ImageView photoImageView;
//        @BindView(R.id.imageDateTextView)
//        TextView dateTimeTextView;
//        @BindView(R.id.malignPercentageTextView)
//        TextView malignTextView;
//        @BindView(R.id.benignPercentageTextView)
//        TextView benignTextView;
//
//        public ResultViewHolder(@NonNull View itemView) {
//            super(itemView);
//            ButterKnife.bind(this, itemView);
//        }
//    }
//
//    public interface ResultClickListener {
//        void onResultClicked(Result result);
//    }
//
//    public void setResults(List<Result> results) {
//        this.results = results;
//        notifyDataSetChanged();
//    }
//
//}
