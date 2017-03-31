package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ryankeith.haemophiliac_helper.R;

/**
 * Created by RenruiLiu on 31/03/2017.
 */
public class Fragment1 extends Fragment {

    //Properties
    private View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment1_layout, container, false);
        return v;
    }
}
