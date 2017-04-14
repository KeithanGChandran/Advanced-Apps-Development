package fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ryankeith.haemophiliac_helper.R;

import java.lang.reflect.Array;

/**
 * Created by RenruiLiu on 31/03/2017.
 */
public class Fragment3 extends Fragment {

    //Properties
    private View v;
    private ListView lv;
    String[] websiteTitles = new String[]{
            "Haemophilia - Wikipedia, the free encyclopedia",
            "Hemophilia - KidsHealth",
            "Guidelines for the management of haemophilia in Australia",
            "Haemophilia - Better Health Channel",
            "Haemophilia | healthdirect",
            "Fitness & Exercise - Living With Hemophilia",
            "Exercises for People with Hemophilia",
            "Physical Therapy in Hemophilia Care Manual - IHTC",
            "How Is Hemophilia Treated?",
            "Hemophilia Treatments and drugs - Mayo Clinic",
            "Factor replacement therapy"};
    String[] websiteLinks = new String[]{
            "https://en.m.wikipedia.org/wiki/Haemophilia",
            "http://kidshealth.org/en/teens/hemophilia.html",
            "https://www.blood.gov.au/haemophilia-guidelines",
            "https://www.betterhealth.vic.gov.au/health/conditionsandtreatments/haemophilia",
            "https://www.healthdirect.gov.au/haemophilia",
            "http://livingwithhemophilia.ca/managing/fitness-exercise.php",
            "https://www.haemophilia.ie/uploaded/Exercise_Guide_med.pdf",
            "http://www.ihtc.org/wp-content/uploads/2013/07/5-PT-Hemophilia-Care-Manual.pdf",
            "https://www.nhlbi.nih.gov/health/health-topics/topics/hemophilia/treatment",
            "http://www.mayoclinic.org/diseases-conditions/hemophilia/basics/treatment/con-20029824",
            "http://www.hemophilia.ca/en/bleeding-disorders/hemophilia-a-and-b/the-treatment-of-hemophilia/factor-replacement-therapy/"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment3_layout, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //set listView
        lv = (ListView) v.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, websiteTitles);
        lv.setAdapter(adapter);

        //open website
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri uri = Uri.parse(websiteLinks[i]);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
    }
}
