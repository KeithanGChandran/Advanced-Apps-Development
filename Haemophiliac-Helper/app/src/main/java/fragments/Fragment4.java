package fragments;

/*
References:

[Android - java - Tutorial] How to write and read txt-files from your Android Application
Pixel_95
https://www.youtube.com/watch?v=x3pyyQbwLko

How to send an email with a file attachment in Android
Agarwal Shankar, user914425
http://stackoverflow.com/questions/9974987/how-to-send-an-email-with-a-file-attachment-in-android
* */

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ryankeith.haemophiliac_helper.AboutUs;
import com.ryankeith.haemophiliac_helper.DataBaseHelper;
import com.ryankeith.haemophiliac_helper.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by RenruiLiu on 31/03/2017.
 */
public class Fragment4 extends Fragment {

    //Properties
    private View v;
    private String[] options = new String[]{"Send Infusion Record","Send Bleeding Record","About Us"};
    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/haemophiliac_helper";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment4_layout, container, false);
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //set listView
        ListView lv = (ListView) v.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, options);
        lv.setAdapter(adapter);

        //set file
        File dir = new File(path);
        dir.mkdirs();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i)
                {
                    case 0:
                        saveRecord("infusion");
                        emailRecord("infusion");
                        break;

                    case 1:
                        saveRecord("bleeding");
                        emailRecord("bleeding");
                        break;

                    case 2:
                        //display about page
                        Intent intent = new Intent(getContext(), AboutUs.class);
                        startActivity(intent);
                        break;

                    default:break;
                }
            }
        });
    }

    public void saveRecord (String record)
    {
        File file = new File (path + "/"+record+"File.txt");// /infusionFile.txt

        DataBaseHelper DBHelper = new DataBaseHelper(getContext());
        DBHelper.getReadableDatabase();
        Cursor res = DBHelper.getAllData(record+"Table"); // infusionTable
        String SumData = "";

        if (res.getCount() == 0) {
            Toast.makeText(getContext(), "No Records in Database", Toast.LENGTH_SHORT).show();
        }
        else if (record.equals("infusion")) {
            // get infusion data to data String, and add it on SumData.
            for (int i = 0; i < res.getCount() && i < 100; i++) {
                res.moveToPosition(i);
                String data = String.valueOf(res.getString(1) + "  Dose:" + res.getString(2) + "  Type:" + res.getString(3) + "  Description:" + res.getString(4) + "\n");
                SumData += data;
            }
        }
        else {
            for (int i = 0; i < res.getCount() && i < 100; i++) {
                //get bleeding data
                res.moveToPosition(i);
                String data = String.valueOf(res.getString(1) + "  Part:" + res.getString(2) + "  Condition:" + res.getString(3) + "  Description:" + res.getString(4) + "\n");
                SumData += data;
            }
        }

        String [] saveText = SumData.split(System.getProperty("line.separator"));
        Save (file, saveText);
    }

    //from reference [Android - java - Tutorial] How to write and read txt-files from your Android Application
    public static void Save(File file, String[] data)
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);
        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        try
        {
            try
            {
                for (int i = 0; i<data.length; i++)
                {
                    fos.write(data[i].getBytes());
                    if (i < data.length-1)
                    {
                        fos.write("\n".getBytes());
                    }
                }
            }
            catch (IOException e) {e.printStackTrace();}
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch (IOException e) {e.printStackTrace();}
        }
    }

    public void emailRecord(final String record){
        //make dialog
        LayoutInflater in = LayoutInflater.from(getContext());
        View emailDialog = in.inflate(R.layout.email_dialog, null);

        //properties
        final EditText recipientTxt = (EditText) emailDialog.findViewById(R.id.recipient);
        final EditText subjectTxt = (EditText) emailDialog.findViewById(R.id.subject);
        final EditText textTxt = (EditText) emailDialog.findViewById(R.id.text);

        //build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Send Email")
                .setView(emailDialog)
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //make file path
                        File fileLocation = new File (path + "/"+record+"File.txt");// /infusionFile.txt
                        Uri filePath = Uri.fromFile(fileLocation);

                        //make email intent
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent .setType("vnd.android.cursor.dir/email");
                        emailIntent .putExtra(Intent.EXTRA_EMAIL, recipientTxt.getText().toString());
                        emailIntent .putExtra(Intent.EXTRA_STREAM, filePath);
                        emailIntent .putExtra(Intent.EXTRA_SUBJECT, subjectTxt.getText().toString());
                        emailIntent .putExtra(Intent.EXTRA_TEXT, textTxt.getText().toString());
                        startActivity(Intent.createChooser(emailIntent , "Send email..."));
                    }
                })
                .create().show();


    }


}
