package skin_cancer_detection_android.ui.main.social;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import skin_cancer_detection_android.R;
import skin_cancer_detection_android.net.ImageHandler;
import skin_cancer_detection_android.net.model.User;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SocialUsersAdapter extends RecyclerView.Adapter<SocialUsersAdapter.UserViewHolder> {

    private List<User> users;
    private UserClickListener listener;

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.firstNameTextView.setText(user.firstName);
        holder.lastNameTextView.setText(user.lastName);
        holder.cartsNumberTextView.setText(String.valueOf(user.cartsNumber));
        ImageHandler.loadImage(holder.photoImageView, user.imagePath, holder.itemView.getContext().getDrawable(R.drawable.item_placeholder_padding));

        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onUserClicked(user));
        }
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setOnUserClickListener(UserClickListener listener) {
        this.listener = listener;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemUserPhotoImageView)
        ImageView photoImageView;
        @BindView(R.id.itemUserFirstNameTextView)
        TextView firstNameTextView;
        @BindView(R.id.itemUserLastNameTextView)
        TextView lastNameTextView;
        @BindView(R.id.itemUserCartsNumberTextView)
        TextView cartsNumberTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    interface UserClickListener {
        void onUserClicked(User user);
    }

}
