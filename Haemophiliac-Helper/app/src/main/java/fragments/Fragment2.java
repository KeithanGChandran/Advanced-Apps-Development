package fragments;
/*
References:
android date picker dialog
CodeVideo
https://www.youtube.com/watch?v=eVsqDBvgd70
How to delete multiple items in android ListView
Indragni Soft Solutions
https://www.youtube.com/watch?v=luxE7oEKiic
* */
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ryankeith.haemophiliac_helper.BleedingListAdapter;
import com.ryankeith.haemophiliac_helper.BleedingRecord;
import com.ryankeith.haemophiliac_helper.DataBaseHelper;
import com.ryankeith.haemophiliac_helper.DateDialog;
import com.ryankeith.haemophiliac_helper.InputFilterMinMax;
import com.ryankeith.haemophiliac_helper.R;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by RenruiLiu on 31/03/2017.
 */
public class Fragment2 extends Fragment {

    //Properties
    private BleedingListAdapter bleedingListAdapter;
    private ListView lv;
    private View v;
    DataBaseHelper DBHelper;
    int[] ID = new int[100];
    String[] date = new String[100];
    String[] part = new String[100];
    int[] condition = new int[100];
    String[] description = new String[100];
    String[] picture = new String[100];
    private List<BleedingRecord> bleedingRecordList = new ArrayList<BleedingRecord>();
    private View tempDialog;
    private View displayDialog;
    private EditText partTxt;
    private EditText conditionTxt;
    private EditText descriptionTxt;
    private Button pickBtn;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_OK = -1;
    private Uri selectedImageUri;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment2_layout, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //inflater dialogs

        //init Database
        DBHelper = new DataBaseHelper(getContext());
        DBHelper.getReadableDatabase();

        //get data from database to array.
        Cursor res = DBHelper.getAllData("bleedingTable");
        if (res.getCount() == 0) {
            Toast.makeText(getContext(), "No Records in Database", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < res.getCount() && i < 100; i++) {
                res.moveToPosition(i);
                ID[i]=res.getInt(0);
                date[i] = res.getString(1);
                part[i] = res.getString(2);
                condition[i] = res.getInt(3);
                description[i]=res.getString(4);
                picture[i] = res.getString(5);
            }
        }

        //addRecord button
        v.findViewById(R.id.addNewBleedingRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewBleedingRecordDialog();
            }
        });

        //get the number of records in database
        int j = 0;
        for (int i = 0; i < date.length; i++) {
            if (date[i] != null)
                j = j + 1;
        }
        //and then show lines(add record from array to listView).
        if (bleedingRecordList.isEmpty()){
            for (int k = 0; k < j; k++) {
                BleedingRecord bleedingRecord = new BleedingRecord(date[k], part[k],condition[k],description[k],picture[k]);
                bleedingRecord.setID(ID[k]);
                bleedingRecordList.add(bleedingRecord);
            }}

        //set arrayAdapter to listView.
        lv = (ListView) v.findViewById(android.R.id.list);
        bleedingListAdapter = new BleedingListAdapter(getContext(), R.layout.bleeding_list_row, bleedingRecordList);
        lv.setAdapter(bleedingListAdapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        //display description
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BleedingRecord bleedingRecord = bleedingListAdapter.getItem(i);

                LayoutInflater in = LayoutInflater.from(getContext());
                displayDialog = in.inflate(R.layout.bleeding_description_dialog,null);
                TextView partTxt = (TextView)displayDialog.findViewById(R.id.display_part);
                partTxt.setText(bleedingRecord.getData("part"));
                TextView conditionTxt = (TextView)displayDialog.findViewById(R.id.display_condition);
                conditionTxt.setText(bleedingRecord.getData("condition"));
                TextView descriptionTxt = (TextView)displayDialog.findViewById(R.id.display_description);
                descriptionTxt.setText(bleedingRecord.getData("description"));
                if (bleedingRecord.getData("picture")!=null){
                ImageView picture = (ImageView)displayDialog.findViewById(R.id.display_picture);
                picture.setImageURI(Uri.parse(bleedingRecord.getData("picture")));}

                AlertDialog.Builder abuilder = new AlertDialog.Builder(getContext());
                abuilder.setTitle(bleedingRecord.getData("date"))
                        .setView(displayDialog)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .create().show();
            }
        });


        //handle the multi choice mode to delete records.
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                final int checkedCount = lv.getCheckedItemCount();
                actionMode.setTitle(checkedCount + " records has been selected.");
                bleedingListAdapter.toggleSelection(i);
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
                        SparseBooleanArray selected = bleedingListAdapter.getSelectedIds();
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                BleedingRecord selecteditem = bleedingListAdapter.getItem(selected.keyAt(i));
                                bleedingListAdapter.remove(selecteditem);
                                DBHelper.getWritableDatabase();
                                DBHelper.deleteData(selecteditem.getID(),"bleedingTable");
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
                bleedingListAdapter.removeSelection();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            selectedImageUri = data.getData();
            ImageView selectedImageView=(ImageView)tempDialog.findViewById(R.id.selectedImage);
            selectedImageView.setImageURI(selectedImageUri);
        } else selectedImageUri=null;
    }

    //add record dialog
    public void addNewBleedingRecordDialog() {

        LayoutInflater in = LayoutInflater.from(getContext());
        tempDialog = in.inflate(R.layout.add_bleeding_record_dialog, null);

        partTxt=(EditText)tempDialog.findViewById(R.id.partTxt);
        conditionTxt=(EditText)tempDialog.findViewById(R.id.conditionTxt);
        conditionTxt.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "10")});
        descriptionTxt=(EditText)tempDialog.findViewById(R.id.bleedingDescriptionTxt);

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

        //select picture
        Button pickImgBtn=(Button)tempDialog.findViewById(R.id.pickImageBtn);
        pickImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
            }
        });


        //Add Record Dialog
        AlertDialog.Builder abuilder = new AlertDialog.Builder(getContext());
        abuilder.setTitle("Add New Bleeding Record")
                .setView(tempDialog)
                .setNegativeButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //insert data if selected a date
                        String date1 = pickBtn.getText().toString();
                        if (date1.equals(getContext().getString(R.string.pick_a_date))) {
                            Toast.makeText(getContext(), "Please pick a date", Toast.LENGTH_SHORT).show();
                        } else if (selectedImageUri==null){
                            insertData2DB(date1, partTxt.getText().toString(),Integer.parseInt(conditionTxt.getText().toString()),descriptionTxt.getText().toString());
                        }
                        else insertData2DB(date1, partTxt.getText().toString(),Integer.parseInt(conditionTxt.getText().toString()),descriptionTxt.getText().toString(),selectedImageUri.toString());
                        //InsertData2DB(date1, type);

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

    public void insertData2DB(String date, String part,int condition,String description, String picture) {
        DBHelper.insertBleedingData(date, part,condition,description, picture);

        BleedingRecord bleedingRecord = new BleedingRecord(date, part,condition,description,picture);
        bleedingRecordList.add(bleedingRecord);
        bleedingListAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Added Bleeding Record", Toast.LENGTH_SHORT).show();
        refreshActivity();
    }
    public void insertData2DB(String date, String part,int condition,String description) {
        DBHelper.insertBleedingData(date, part,condition,description);
        BleedingRecord bleedingRecord = new BleedingRecord(date, part,condition,description,null);
        bleedingRecordList.add(bleedingRecord);
        bleedingListAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Added Bleeding Record", Toast.LENGTH_SHORT).show();
        refreshActivity();
    }

    public void refreshActivity() {
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }
}
