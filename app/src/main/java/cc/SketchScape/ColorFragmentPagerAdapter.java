package cc.SketchScape;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

public class ColorFragmentPagerAdapter extends PagerAdapter {
    DrawActivity context;
    HashMap<String, HashMap<String, String>> colorDb;
    public ColorFragmentPagerAdapter(DrawActivity context, HashMap<String, HashMap<String, String>> colorDb) {
//        super(fm);
        this.context = context;
        this.colorDb = colorDb;
        System.out.println("ColorFragmentPagerAdapter initialized");
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        System.out.println("ColorFragmentPagerAdapter getItem position : " + position);
        switch(position){
            case 0:
                View tabLayout = context.getLayoutInflater().inflate(R.layout.color_list, container, false);

                ListView colorListView = tabLayout.findViewById(R.id.colorListView);
                colorListView.setAdapter(new ColorListAdapter(context, colorDb.get("Landscape")));
                return tabLayout;
            case 1:
                return ColorListFragment.newInstance(colorDb.get("Animals")).getView();
            case 2:
                return ColorListFragment.newInstance(colorDb.get("Outdoor Objects")).getView();
            case 3:
                return ColorListFragment.newInstance(colorDb.get("Vehicles")).getView();
            case 4:
                return ColorListFragment.newInstance(colorDb.get("Indoor Objects")).getView();
            case 5:
                return ColorListFragment.newInstance(colorDb.get("Ornaments")).getView();
            case 6:
                return ColorListFragment.newInstance(colorDb.get("Eatables")).getView();
            case 7:
                return ColorListFragment.newInstance(colorDb.get("Misc.")).getView();
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return 8;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Landscape";
            case 1:
                return "Animals";
            case 2:
                return "Outdoor Objects";
            case 3:
                return "Vehicles";
            case 4:
                return "Indoor Objects";
            case 5:
                return "Ornaments";
            case 6:
                return "Eatables";
            case 7:
                return "Misc.";
            default:
                return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

}
