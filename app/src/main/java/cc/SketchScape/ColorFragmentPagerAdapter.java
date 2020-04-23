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
                return createView(position, container, "Landscape");
            case 1:
                return createView(position,container, "Animals");
            case 2:
                return createView(position, container, "Outdoor Objects");
            case 3:
                return createView(position, container, "Vehicles");
            case 4:
                return createView(position, container, "Indoor Objects");
            case 5:
                return createView(position, container, "Ornaments");
            case 6:
                return createView(position, container, "Eatables");
            case 7:
                return createView(position, container, "Misc.");
            default:
                return null;
        }
    }

    private View createView(int position, ViewGroup container, String category){
        View tabLayout = context.getLayoutInflater().inflate(R.layout.color_list, container, false);
        ListView colorListView = tabLayout.findViewById(R.id.colorListView);
        colorListView.setAdapter(new ColorListAdapter(context, colorDb.get(category)));
        container.addView(tabLayout);
        return tabLayout;
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
