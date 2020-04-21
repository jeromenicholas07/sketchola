package cc.SketchScape;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class ColorListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "colorDb";

    // TODO: Rename and change types of parameters
    private HashMap<String, String> colorList;

    public ColorListFragment() {
        // Required empty public constructor
    }

    public static ColorListFragment newInstance(HashMap<String, String> colorList) {
        System.out.println("new inst called!");
        ColorListFragment fragment = new ColorListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, colorList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            colorList = (HashMap<String, String>) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.out.println("list fragment on create view called.");
        View tabLayout = inflater.inflate(R.layout.color_list, container, false);

        ListView colorListView = tabLayout.findViewById(R.id.colorListView);
        colorListView.setAdapter(new ColorListAdapter((DrawActivity)getActivity(), colorList));
        return tabLayout;
    }




}
