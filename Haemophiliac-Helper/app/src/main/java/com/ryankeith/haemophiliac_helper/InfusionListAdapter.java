package com.ryankeith.haemophiliac_helper;
/*
ArrayAdapter for infusion records listView.
 *  */

/*
References:
How to delete multiple items in android ListView
Indragni Soft Solutions
https://www.youtube.com/watch?v=luxE7oEKiic
* */
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by RenruiLiu on 6/04/2017.
 */
public class InfusionListAdapter extends ArrayAdapter<InfusionRecord> {

    private Context context;
    List<InfusionRecord> infusionRecordList;
    LayoutInflater inflater;
    private SparseBooleanArray selectedItemsIDs;

    public InfusionListAdapter(Context context, int resourceID, List<InfusionRecord> infusionRecordList ){
        super(context, resourceID, infusionRecordList);
        this.context = context;
        selectedItemsIDs = new SparseBooleanArray();
        this.infusionRecordList = infusionRecordList;
        inflater = LayoutInflater.from(context);

    }

    //ViewHolder
    private class ViewHolder {
        TextView date;
        TextView dose;
        TextView type;
        TextView description;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.infusion_list_row, null);
            holder.date = (TextView) view.findViewById(R.id.infusion_date);
            holder.dose = (TextView) view.findViewById(R.id.infusion_dose);
            holder.type = (TextView) view.findViewById(R.id.infusion_type);
            holder.description = (TextView) view.findViewById(R.id.infusion_description) ;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.date.setText(infusionRecordList.get(position).getData("date"));
        holder.dose.setText(infusionRecordList.get(position).getData("dose"));
        holder.type.setText(infusionRecordList.get(position).getData("type"));
        holder.description.setText(infusionRecordList.get(position).getData("description"));
        return view;
    }

    public void remove(InfusionRecord object) {
        infusionRecordList.remove(object);
        notifyDataSetChanged();
    }

    public List<InfusionRecord> getInfusionRecord() {return infusionRecordList;}

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
