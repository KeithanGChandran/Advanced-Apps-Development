package com.ryankeith.haemophiliac_helper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * Created by RenruiLiu on 6/04/2017.
 */
public class DateDialog extends android.support.v4.app.DialogFragment implements DatePickerDialog.OnDateSetListener {
    TextView txtDate;
    String date;

    public DateDialog(){
    }
    public void setter(View view){
        txtDate = (TextView) view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        final java.util.Calendar c = java.util.Calendar.getInstance();
        int year = c.get(java.util.Calendar.YEAR);
        int month = c.get(java.util.Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    public String getDate() {return date;}

    public void onDateSet(DatePicker view, int year, int month, int day) {
        date = year + "-" + fixDate(month + 1) + "-" + fixDate(day);
        txtDate.setText(date);
    }

    private static String fixDate(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

}
