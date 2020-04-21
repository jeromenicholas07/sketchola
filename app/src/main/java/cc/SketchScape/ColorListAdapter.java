package cc.SketchScape;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

public class ColorListAdapter extends ArrayAdapter {
    private String[] labels;
    private String[] colors;
    private final DrawActivity context;

    ColorListAdapter(DrawActivity context, Map<String, String> colors){
        super(context,R.layout.color_row, colors.keySet().toArray(new String[10]));
        System.out.println("color list adapter constructor called :: " + colors.keySet().toArray(new String[10]).length);
        this.context = context;
        this.labels = colors.keySet().toArray(new String[10]);
        this.colors = colors.values().toArray(new String[10]);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.color_row, null,true);

        ImageView colorDisplay = rowView.findViewById(R.id.color_row_display);
        TextView colorName = rowView.findViewById(R.id.color_row_name_display);

        int color = Color.parseColor(colors[position]);
        Drawable unwrappedDrawable = colorDisplay.getDrawable();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        colorDisplay.setImageDrawable(wrappedDrawable);
        colorName.setText(labels[position]);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.setColor(colors[position]);

            }
        });

        return rowView;
    }
}
