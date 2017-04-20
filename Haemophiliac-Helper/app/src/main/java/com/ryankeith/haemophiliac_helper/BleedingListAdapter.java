package com.ryankeith.haemophiliac_helper;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jiechun on 20/04/2017.
 */
public class BleedingListAdapter extends ArrayAdapter<BleedingRecord> {

    private Context context;
    List<BleedingRecord> bleedingRecordList;
    LayoutInflater inflater;
    private SparseBooleanArray selectedItemsIDs;

    public BleedingListAdapter(Context context, int resourceId, List<BleedingRecord> bleedingRecordList) {
        super(context, resourceId, bleedingRecordList);

        this.context = context;
        selectedItemsIDs = new SparseBooleanArray();
        this.bleedingRecordList = bleedingRecordList;
        inflater = LayoutInflater.from(context);
    }

    //ViewHolder
    private class ViewHolder {
        TextView date;
        TextView part;
        TextView condition;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.bleeding_list_row, null);
            holder.date = (TextView) view.findViewById(R.id.bleeding_date);
            holder.part = (TextView) view.findViewById(R.id.bleeding_part);
            holder.condition = (TextView) view.findViewById(R.id.bleeding_condition);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.date.setText(bleedingRecordList.get(position).getData("date"));
        holder.part.setText(bleedingRecordList.get(position).getData("part"));
        holder.condition.setText(bleedingRecordList.get(position).getData("condition"));
        return view;
    }

    public void remove(BleedingRecord object) {
        bleedingRecordList.remove(object);
        notifyDataSetChanged();
    }

    public List<BleedingRecord> getBleedingRecord() {return bleedingRecordList;}

    public void toggleSelection(int position) {
        selectView(position, !selectedItemsIDs.get(position));
    }

    public void removeSelection() {
        selectedItemsIDs = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value) {
            selectedItemsIDs.put(position, value);
        } else {
            selectedItemsIDs.delete(position);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {return selectedItemsIDs.size();}

    public SparseBooleanArray getSelectedIds() {return selectedItemsIDs;}

}
