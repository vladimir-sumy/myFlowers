package it_school.sumdu.edu.myflowers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private Context context;
    private List<Flower> flowerList;
    private List<Flower> filteredList;
    private OnItemClickListener listener;

    public ItemAdapter(Context context, List<Flower> flowerList) {
        this.context = context;
        this.flowerList = flowerList;
        this.filteredList = new ArrayList<>(flowerList);
    }

    public ItemAdapter(List<Flower> flowerList) {
        this.flowerList = flowerList;
        this.filteredList = new ArrayList<>(flowerList);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Flower flower = filteredList.get(position);
        if (flower != null) {
            holder.flowerNameTextView.setText(flower.getName());

            // Set flower image using an image loading library or custom logic
            holder.flowerImageView.setImageURI(flower.getImageUri());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(position);
                    }
                    // Handle item click, navigate to flower details activity
                    openFlowerDetailsActivity(flower, position);
                }
            });
        }

    }

    private void openFlowerDetailsActivity(Flower flower, int position) {
        Intent intent = new Intent(context, ViewItemActivity.class);
        intent.putExtra("flower", flower);
        intent.putExtra("position", position);

        ((Activity) context).startActivityForResult(intent, MainActivity.VIEW_ITEM_REQUEST_CODE);
    }


    @Override
    public int getItemCount() {
        return filteredList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView flowerImageView;
        TextView flowerNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flowerImageView = itemView.findViewById(R.id.flowerImageView);
            flowerNameTextView = itemView.findViewById(R.id.flowerNameTextView);
        }
    }

    public void deleteFlower(Flower flower, Context context) {
        int position = flowerList.indexOf(flower);
        if (position != -1) {
            flowerList.remove(position);
            notifyItemRemoved(position);

            DbHelper dbHelper = new DbHelper(context);
            dbHelper.deleteFlower(flower.getId());
        }
    }

    public void filterList(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(flowerList);
        } else {
            query = query.toLowerCase();
            for (Flower flower : flowerList) {
                if (flower.getName().toLowerCase().contains(query)) {
                    filteredList.add(flower);
                }
            }
        }
        notifyDataSetChanged();
    }


}
