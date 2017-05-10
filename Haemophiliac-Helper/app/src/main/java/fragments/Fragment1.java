package fragments;

//Infusion page

/*
References:
android date picker dialog
CodeVideo
https://www.youtube.com/watch?v=eVsqDBvgd70
How to delete multiple items in android ListView
Indragni Soft Solutions
https://www.youtube.com/watch?v=luxE7oEKiic
Android notification at specific date
Wajdi Hh
http://stackoverflow.com/questions/9930683/android-notification-at-specific-date
* */
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ryankeith.haemophiliac_helper.DataBaseHelper;
import com.ryankeith.haemophiliac_helper.DateDialog;
import com.ryankeith.haemophiliac_helper.InfusionListAdapter;
import com.ryankeith.haemophiliac_helper.InfusionRecord;
import com.ryankeith.haemophiliac_helper.R;
import com.ryankeith.haemophiliac_helper.Receiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by RenruiLiu on 31/03/2017.
 */
public class Fragment1 extends ListFragment {

    //Properties
    DataBaseHelper DBHelper;
    private View v;
    private TextView lastDateTxt;
    private TextView nextDateTxt;
    private TextView LastDate;
    private TextView NextDate;
    private Button addNewInfusionRecordBtn;
    private Button setNotificationBtn;
    private Button setFrequencyBtn;
    private int[] ID=new int[100];
    private String[] date = new String[100];
    private String[] dose = new String[100];
    private String[] type = new String[100];
    private String[] description = new String[100];
    private String lastDateString;
    private List<InfusionRecord> infusionRecordList = new ArrayList<InfusionRecord>();
    private ListView lv;
    private InfusionListAdapter infusionListAdapter;
    private View tempDialog;
    private RadioGroup radioGroup;
    private String typeOfTreatment = "Prophylaxis";
    private Button pickBtn;
    private int frequency = 3;
    public static final String MyPREFERENCES = "AOP_PREFS";
    SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment1_layout, container, false);
        return v;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lastDateTxt = (TextView) v.findViewById(R.id.textView);
        nextDateTxt = (TextView) v.findViewById(R.id.textView2);
        LastDate = (TextView) v.findViewById(R.id.lastInfusionDate);
        NextDate = (TextView) v.findViewById(R.id.nextInfusionDate);
        addNewInfusionRecordBtn = (Button) v.findViewById(R.id.addNewInfusionRecordBtn);
        setNotificationBtn = (Button) v.findViewById(R.id.setNotificationBtn);
        setFrequencyBtn = (Button) v.findViewById(R.id.setFrequencyBtn);

        //get frequency from sharedPreferences
        prefs = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        frequency = prefs.getInt("frequency",frequency);

        //init Database
        DBHelper = new DataBaseHelper(getContext());
        DBHelper.getReadableDatabase();

        //get data from database to array.
        Cursor res = DBHelper.getAllData("infusionTable");
        if (res.getCount() == 0) {
            Toast.makeText(getContext(), "No Records in Database", Toast.LENGTH_SHORT).show();
            //hide the texts
            lastDateTxt.setText("");
            nextDateTxt.setText("");
            setNotificationBtn.setBackground(null);
            setFrequencyBtn.setBackground(null);
        } else {
            for (int i = 0; i < res.getCount() && i < 100; i++) {
                res.moveToPosition(i);
                ID[i] = res.getInt(0);
                date[i] = res.getString(1);
                dose[i] = res.getString(2);
                type[i] = res.getString(3);
                description[i] = res.getString(4);

                //set last and next infusion date.
                res.moveToFirst();
                lastDateString = res.getString(1);
                LastDate.setText(" "+lastDateString+" ");
                try {
                    NextDate.setText(" "+date2String(calendar2Date(getNextDate(lastDateString,frequency)))+" ");
                }
                catch (ParseException e){e.printStackTrace();}
            }
        }

        if (infusionRecordList.isEmpty()){
            //and then show lines(add record from array to listView).
            for (int k = 0; k<countArray(); k++){
                InfusionRecord infusionRecord = new InfusionRecord(date[k],dose[k],type[k],description[k]);
                infusionRecord.setID(ID[k]);
                infusionRecordList.add(infusionRecord);
            }
        }

        lv = (ListView) v.findViewById(android.R.id.list);
        infusionListAdapter = new InfusionListAdapter(getContext(),R.layout.infusion_list_row,infusionRecordList);
        lv.setAdapter(infusionListAdapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        //display description
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final InfusionRecord infusionRecord = infusionListAdapter.getItem(i);
                String descriptionTxt = infusionRecord.getData("description");
                AlertDialog.Builder abuilder = new AlertDialog.Builder(getContext());
                abuilder.setTitle(infusionRecord.getData("date"))
                        .setMessage(descriptionTxt)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .create().show();
            }
        });

        //use the multi choice mode to delete records.
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                final int checkedCount = lv.getCheckedItemCount();
                actionMode.setTitle(checkedCount + " records has been selected.");
                infusionListAdapter.toggleSelection(i);
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.menu_fragment1, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete:
                        SparseBooleanArray selected = infusionListAdapter.getSelectedIds();
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                InfusionRecord selectedItem = infusionListAdapter.getItem(selected.keyAt(i));
                                infusionListAdapter.remove(selectedItem);
                                DBHelper.getWritableDatabase();
                                DBHelper.deleteData(selectedItem.getID(),"infusionTable");
                                refreshActivity();
                            }
                        }
                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                infusionListAdapter.removeSelection();
            }
        });

        //add record dialog
        addNewInfusionRecordBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                addNewInfusionRecordDialog();
            }
        });

        //set notification
        setNotificationBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (countArray() != 0) {
                try {
                    //get next Injection date
                    Calendar cal=getNextDate(lastDateString,frequency);
                    Date strDate=calendar2Date(cal);
                    if (System.currentTimeMillis()>strDate.getTime()){
                        Toast.makeText(getContext(),"The next infusion date has passed, please set a new date.",Toast.LENGTH_SHORT).show();}
                    else {
                        cal.set(Calendar.HOUR_OF_DAY, 11);
                        cal.set(Calendar.MINUTE, 59);
                        //set alarm
                        AlarmManager alarms = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        Receiver receiver = new Receiver();
                        IntentFilter filter = new IntentFilter("ALARM_ACTION");
                        getContext().registerReceiver(receiver, filter);
                        Intent intent = new Intent("ALARM_ACTION");
                        intent.putExtra("Toast", "It's time to inject");
                        PendingIntent operation = PendingIntent.getBroadcast(getContext(), 0, intent, 0);
                        alarms.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), operation);
                        Toast.makeText(getContext(), "Set alarm at " + cal.getTime().toString(), Toast.LENGTH_LONG).show();
                }
            }catch (ParseException e){
                e.printStackTrace();}
            }
            else    Toast.makeText(getContext(), "Please add new record", Toast.LENGTH_LONG).show();
            }
        });


        //set frequency
        setFrequencyBtn.setOnClickListener(new View.OnClickListener() {
            LayoutInflater in = LayoutInflater.from(getContext());
            View freDialog = in.inflate(R.layout.frequency_dialog, null);
            NumberPicker numberPicker = (NumberPicker) freDialog.findViewById(R.id.numberPicker);

            @Override
            public void onClick(View view) {
                numberPicker.setMaxValue(365);
                numberPicker.setMinValue(1);
                numberPicker.setWrapSelectorWheel(false);
                //Add Record Dialog
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(getContext());
                aBuilder.setTitle("Set frequency")
                        .setView(freDialog)
                        .setNegativeButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                frequency = numberPicker.getValue();
                                //save frequency to sharedPreferences
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("frequency",frequency);
                                editor.apply();
                                refreshActivity();
                            }
                        })
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                if(freDialog.getParent()!=null)
                                    ((ViewGroup)freDialog.getParent()).removeView(freDialog);
                            }
                        })
                        .create().show();
            }
        });

    }

    //add record dialog
    public void addNewInfusionRecordDialog() {
        LayoutInflater in = LayoutInflater.from(getContext());
        tempDialog = in.inflate(R.layout.add_infusion_record_dialog, null);

        //radioButtons
        radioGroup = (RadioGroup) tempDialog.findViewById(R.id.r_g);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //get type from radio button
                if (i == R.id.prophylaxis)
                    typeOfTreatment = getString(R.string.Prophylaxis);
                else typeOfTreatment = getString(R.string.Demand);
            }
        });

        //DatePickerDialog inside this dialog
        pickBtn = (Button) tempDialog.findViewById(R.id.pickDateBtn);
        pickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialog dialog = new DateDialog();
                dialog.setter(view);
                android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
            }
        });
        final EditText doseTxt = (EditText) tempDialog.findViewById(R.id.infusionDoseTxt);
        final EditText descriptionTxt = (EditText) tempDialog.findViewById(R.id.infusionDescriptionTxt);

        //Add Record Dialog
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(getContext());
        aBuilder.setTitle("Add New Infusion Record")
                .setView(tempDialog)
                .setNegativeButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //insert data if selected a date
                        String date = pickBtn.getText().toString();
                        if (date.equals(getContext().getString(R.string.pick_a_date))) {
                            Toast.makeText(getContext(), "Please pick a date", Toast.LENGTH_SHORT).show();
                        }
                        else insertData2DB(date,doseTxt.getText().toString(),typeOfTreatment,descriptionTxt.getText().toString());
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create().show();

    }

    //insert data to db
    public void insertData2DB(String date,String dose,String type,String description) {
        DBHelper.insertInfusionData(date, dose, type, description);
        InfusionRecord infusionRecord = new InfusionRecord(date,dose,type,description);
        infusionRecordList.add(infusionRecord);
        infusionListAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "added Record", Toast.LENGTH_SHORT).show();
        refreshActivity();
    }

    public void refreshActivity() {
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }

    //Date calculator
    public String date2String(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(date);
    }

    public Date calendar2Date(Calendar cal){
        return cal.getTime();
    }

    public static Calendar getNextDate(String strTime, int frequency)
            throws ParseException {
        //convert string date to Date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = formatter.parse(strTime);
        //add i days
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, frequency);
        return cal;
    }

    //get the number of records in database
    public int countArray(){
        int j = 0;
        for (int i = 0; i < date.length; i++) {
            if (date[i] != null)
                j += 1;
        }
    return j;}
}
