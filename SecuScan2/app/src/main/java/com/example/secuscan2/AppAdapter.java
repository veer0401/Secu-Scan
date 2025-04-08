package com.example.secuscan2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppAdapter extends BaseAdapter {

    private final LayoutInflater layoutInflater;
    private final List<AppList> listStorage;

    public AppAdapter(Context context, List<AppList> customizedListView) {
        this.layoutInflater = LayoutInflater.from(context);
        this.listStorage = customizedListView;
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return listStorage.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder listViewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.content_listapp, parent, false);
            listViewHolder = new ViewHolder(convertView);
            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) convertView.getTag();
        }

        // Bind data to views
        AppList app = listStorage.get(position);
        listViewHolder.textInListView.setText(app.getName());
        listViewHolder.imageInListView.setImageDrawable(app.getIcon());

        return convertView;
    }

    // ViewHolder pattern to avoid redundant view lookups
    static class ViewHolder {
        final TextView textInListView;
        final ImageView imageInListView;

        ViewHolder(View view) {
            textInListView = view.findViewById(R.id.list_app_name);
            imageInListView = view.findViewById(R.id.app_icon);
        }
    }
}
