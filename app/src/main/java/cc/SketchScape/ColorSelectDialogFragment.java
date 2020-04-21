package cc.SketchScape;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;


public class ColorSelectDialogFragment extends DialogFragment {
//    TabLayout tabLayout;
//    ViewPager viewPager;
    private HashMap<String, HashMap<String, String>> colorDb;
    private DrawActivity context;

    public ColorSelectDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = (DrawActivity) context;
        this.colorDb = this.context.colorDb;
    }

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // Get the layout inflater
//        LayoutInflater inflater = context.getLayoutInflater();
//
//        View dialog = inflater.inflate(R.layout.fragment_color_select_dialog, null);
////        context.setContentView(dialog);
//        TabLayout tabLayout = dialog.findViewById(R.id.tab_layout);
//
//        final ViewPager viewPager = new ViewPager(context);
//        ColorFragmentPagerAdapter colViewPagerAdapter = new ColorFragmentPagerAdapter(getFragmentManager(), colorDb);
//        viewPager.setAdapter(colViewPagerAdapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//        tabLayout.setupWithViewPager(viewPager);
////        tabLayout.addTab(tabLayout.newTab().setText("Landscape").setCustomView(fillSelectedTab("Landscape")));
//////        tabLayout.addView(fillSelectedTab("Landscape"));
////        tabLayout.addTab(tabLayout.newTab().setText("Animals"));
////        tabLayout.addTab(tabLayout.newTab().setText("Outdoor Objects"));
////        tabLayout.addTab(tabLayout.newTab().setText("Vehicles"));
////        tabLayout.addTab(tabLayout.newTab().setText("Indoor Objects"));
////        tabLayout.addTab(tabLayout.newTab().setText("Ornaments"));
////        tabLayout.addTab(tabLayout.newTab().setText("Eatables"));
////        tabLayout.addTab(tabLayout.newTab().setText("Misc."));
//
//
//        builder.setView(dialog);
//        return builder.create();
//    }

    private View fillSelectedTab(String key){
        View tabLayout = getLayoutInflater().inflate(R.layout.color_list, null);
        ListView colorList = tabLayout.findViewById(R.id.colorListView);
        colorList.setAdapter(new ColorListAdapter(context, colorDb.get(key)));

        return tabLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup dialog = (ViewGroup) inflater.inflate(R.layout.fragment_color_select_dialog, container, true);

//        context.setContentView(dialog);
        TabLayout tabLayout = dialog.findViewById(R.id.tab_layout);

        final ViewPager viewPager = dialog.findViewById(R.id.view_pager);
        viewPager.setBackgroundColor(Color.GREEN);

        ColorFragmentPagerAdapter colViewPagerAdapter = new ColorFragmentPagerAdapter(context, colorDb);
        viewPager.setAdapter(colViewPagerAdapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                System.out.println("tab changed");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        return dialog;
    }
}
