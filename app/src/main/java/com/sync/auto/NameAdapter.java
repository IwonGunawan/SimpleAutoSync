package com.sync.auto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class NameAdapter extends ArrayAdapter<NameModel> {

    private List<NameModel> names;
    Context context;

    public NameAdapter(@NonNull Context context, int resource, @NonNull List<NameModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.names = objects;
    }

    public View getView(int position, View view, ViewGroup parent) {
        // getting layout inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // getting listview
        View listViewItem = inflater.inflate(R.layout.activity_item, null, true);
        TextView tvName     = listViewItem.findViewById(R.id.tv_name);
        ImageView imageView = listViewItem.findViewById(R.id.iv_success);

        NameModel nameModel = names.get(position);
        tvName.setText(nameModel.getName());

        if (nameModel.getStatus() == 0) {
            imageView.setBackgroundResource(R.drawable.ic_access_time_black_24dp);
        }
        else {
            imageView.setBackgroundResource(R.drawable.ic_check_green_24dp);
        }

        return listViewItem;
    }
}
