package com.example.miniproject;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> implements Filterable {

    private ArrayList<User> mListUsers;
    private ArrayList<User> mListUsersOld;

    public UserAdapter(ArrayList<User> mListUsers) {
        this.mListUsers = mListUsers;
        this.mListUsersOld = mListUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mListUsers.get(position);
        if (user == null) {
            return;
        }

        holder.ivIcon.setImageResource(user.getImage());
        holder.tvTitle.setText(user.getTitle());
        holder.tvProvince.setText(user.getProvince());

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGoToDetail(user, view);
            }
        });
    }

    private void onClickGoToDetail(User user, View view) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.recruitment_information);

        TextView title = (TextView) dialog.findViewById(R.id.tvTitle);
        TextView province = (TextView) dialog.findViewById(R.id.tvProvince);
        TextView phone = (TextView) dialog.findViewById(R.id.tvPhone);
        TextView salary = (TextView) dialog.findViewById(R.id.tvSalary);
        TextView requirements = (TextView) dialog.findViewById(R.id.tvRequirements);
        TextView description = (TextView) dialog.findViewById(R.id.tvDescription);
        Button btnCall = (Button) dialog.findViewById(R.id.btnCall);


        title.setText(user.getTitle());
        province.setText(user.getProvince());
        phone.setText(String.valueOf(user.getPhone()));
        salary.setText(String.valueOf(user.getMinSalary())+"-"+
                String.valueOf(user.getMaxSalary())+" VND/Month");
        requirements.setText(user.getRequirements().replaceAll("newline", "\n"));
        description.setText(user.getDescription());

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone.getText().toString().trim()));
                if (intent.resolveActivity(view.getContext().getPackageManager()) != null) {
                    view.getContext().startActivity(intent);
                }
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        if (mListUsers != null) {
            return mListUsers.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivIcon;
        private TextView tvTitle;
        private TextView tvProvince;
        private RelativeLayout layoutItem;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvProvince = itemView.findViewById(R.id.tvProvince);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString();
                if (strSearch.isEmpty()) {
                    mListUsers = mListUsersOld;
                } else {
                    ArrayList<User> list = new ArrayList<>();
                    for (User user : mListUsersOld) {
                        if (user.getTitle().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(user);
                        }
                    }

                    mListUsers = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListUsers;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mListUsers = (ArrayList<User>)  filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
