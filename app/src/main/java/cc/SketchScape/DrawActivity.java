package cc.SketchScape;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrawActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout bot_bar;
    LinearLayout baseLayout;

    private DrawingView mDrawingView;
    private ImageButton newButton, nextButton, undoButton, redoButton, brushButton, fillButton, eraseButton;
    private ImageButton currentPaint;
    private int smallBrush, mediumBrush, largeBrush;

    Map<String, Map<String, String>> colorDb;

    public DrawActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseLayout = findViewById(R.id.linear_layout);
        bot_bar = findViewById(R.id.bot_bar);

        mDrawingView = (DrawingView)findViewById(R.id.drawing);

        newButton = (ImageButton) findViewById(R.id.buttonNew);
        newButton.setOnClickListener(this);

        nextButton = (ImageButton) findViewById(R.id.buttonNext);
        nextButton.setOnClickListener(this);

        undoButton = (ImageButton) findViewById(R.id.buttonUndo);
        undoButton.setOnClickListener(this);

        redoButton = (ImageButton) findViewById(R.id.buttonRedo);
        redoButton.setOnClickListener(this);

        brushButton = (ImageButton) findViewById(R.id.buttonBrush);
        brushButton.setOnClickListener(this);

        fillButton = (ImageButton) findViewById(R.id.buttonFillBucket);
        fillButton.setOnClickListener(this);

        eraseButton = (ImageButton) findViewById(R.id.buttonErase);
        eraseButton.setOnClickListener(this);


        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        // Set the initial brush size
        int initBrush = getApplicationContext().getResources().getInteger(R.integer.medium_size);
        mDrawingView.setBrushSize(initBrush);

        colorDb = new HashMap<>();

        Map<String, String> m = new HashMap<>();
        m.put("unlabeled", "#000000");
//        m.put("ego vehicle", "#000000");
//        m.put("rectification border", "#000000");
//        m.put("out of roi", "#000000");
//        m.put("static", "#000000");
        m.put("dynamic", "#6f4a00");
        m.put("ground", "#510051");
        colorDb.put("void", m);

        m = new HashMap<>();
        m.put("road", "#804080");
        m.put("sidewalk", "#f423e8");
        m.put("parking", "#faaaa0");
        m.put("rail track", "#e6968c");
        colorDb.put("ground", m);

        m = new HashMap<>();
        m.put("building", "#464646");
        m.put("wall", "#66669c");
        m.put("fence", "#be9999");
        m.put("guard rail", "#b4a5b4");
        m.put("bridge", "#966464");
        m.put("tunnel", "#96785a");
        colorDb.put("construction", m);

        m = new HashMap<>();
        m.put("pole", "#999999");
        m.put("polegroup", "#999999");
        m.put("traffic light", "#faaa1e");
        m.put("traffic sign", "#dcdc00");
        colorDb.put("object", m);

        m = new HashMap<>();
        m.put("vegetation", "#6b8e23");
        m.put("terrain", "#98fb98");
        colorDb.put("nature", m);

        m = new HashMap<>();
        m.put("sky", "#4682b4");
        colorDb.put("sky", m);

        m = new HashMap<>();
        m.put("car", "#00008e");
        m.put("truck", "#000046");
        m.put("bus", "#003c64");
        m.put("caravan", "#00005a");
        m.put("trailer", "#00006e");
        m.put("train", "#005064");
        m.put("motorcycle", "#0000e6");
        m.put("bicycle", "#770b20");
        m.put("license plate", "#00008e");
        colorDb.put("vehicle", m);

        showBrushDialog();
        brushButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.toolbar_grey_selected));

    }

//    public void paintClicked(View view){
//        if (view != currPaint){
//            // Update the color
//            ImageButton imageButton = (ImageButton) view;
//            String colorTag = imageButton.getTag().toString();
//            mDrawingView.setColor(colorTag);
//            // Swap the backgrounds for last active and currently active image button.
//            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.pallet_pressed));
//            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.pallet));
//            currPaint = (ImageButton)view;
//            mDrawingView.setErase(false);
//            mDrawingView.setBrushSize(mDrawingView.getLastBrushSize());
//        }
//    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.buttonBrush:
                // Show brush size chooser dialog
                showBrushDialog();
                brushButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.toolbar_grey_selected));
                eraseButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.toolbar_grey));
                fillButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.toolbar_grey));
                break;
            case R.id.buttonErase:
//                // Show eraser size chooser dialog
                showEraserDialog();
                brushButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.toolbar_grey));
                eraseButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.toolbar_grey_selected));
                fillButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.toolbar_grey));
                break;
            case R.id.buttonFillBucket:
//                // Show new painting alert dialog
                showColorFillDialog();
                brushButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.toolbar_grey));
                eraseButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.toolbar_grey));
                fillButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.toolbar_grey_selected));
                break;
            case R.id.buttonNew:
//                // Show save painting confirmation dialog.
                mDrawingView.startNew();
                break;
            case R.id.buttonNext:
                Intent intent = new Intent(DrawActivity.this, ResultActivity.class);
                Bitmap mDrawing = mDrawingView.getDrawing();

                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                mDrawing.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();

                intent.putExtra("img", byteArray);
                startActivity(intent);
                break;
            case R.id.buttonUndo:
                if (!mDrawingView.undoPaths()){
                    Toast.makeText(getApplicationContext(), "Undo not possible", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonRedo:

                if (!mDrawingView.redoPaths()){
                    Toast.makeText(getApplicationContext(), "Redo not possible", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private void showBrushDialog() {
        bot_bar.removeAllViews();
        mDrawingView.setErase(false);
        mDrawingView.setFill(false);

        View tools = getLayoutInflater().inflate(R.layout.brush_toolbar, baseLayout, false);

        final ImageButton small_button = tools.findViewById(R.id.small_brush);
        final ImageButton medium_button = tools.findViewById((R.id.medium_brush));
        final ImageButton large_button = tools.findViewById(R.id.large_brush);

        small_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.setBrushSize(smallBrush);
                small_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
                medium_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
                large_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            }
        });
        medium_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.setBrushSize(mediumBrush);
                small_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
                medium_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
                large_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            }
        });
        large_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.setBrushSize(largeBrush);
                small_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
                medium_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
                large_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
            }
        });

        int currSize = mDrawingView.getLastBrushSize();
        if(currSize == smallBrush){
            small_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
            medium_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            large_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
        }
        if(currSize == mediumBrush){
            small_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            medium_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
            large_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
        }
        if(currSize == largeBrush){
            small_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            medium_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            large_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
        }

        LinearLayout brushSizes = tools.findViewById(R.id.brush_sizes);
//        brushSizes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50,0));

        LinearLayout panel = tools.findViewById(R.id.brushes_panel);
//        brushSizes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));


        for (String cat : colorDb.keySet()) {
            View catContainer = getLayoutInflater().inflate(R.layout.palette_container, null, false);
            LinearLayout catTop = catContainer.findViewById(R.id.palette_top);
            TextView catTitle = catContainer.findViewById(R.id.title_container);
            catTitle.setText(cat);

            Map<String, String> colors = colorDb.get(cat);
            for(String cname : colors.keySet()){
                String hex = colors.get(cname);


                View colContainer = getLayoutInflater().inflate(R.layout.palette_container, null, false);
                LinearLayout colTop = colContainer.findViewById(R.id.palette_top);
                TextView colTitle = colContainer.findViewById(R.id.title_container);
                colTitle.setText(cname);

                ImageButton ib = new ImageButton(this);
                ib.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                ib.setMinimumHeight(175);
                ib.setMinimumWidth(220);

                ib.setBackgroundColor(Color.parseColor(hex));
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int color = 0xff000000;

                        Drawable background = view.getBackground();
                        if (background instanceof ColorDrawable)
                            color = ((ColorDrawable) background).getColor();

                        mDrawingView.setColor(color);
//                        System.out.println(color);
                    }
                });


                colTop.addView(ib);

                catTop.addView(colContainer);
            }

            View div = new View(this);
            div.setMinimumWidth(6);



            panel.addView(catContainer);
            panel.addView(div);
            panel.invalidate();
        }

        bot_bar.addView(tools);

    }

    private void showEraserDialog() {
        bot_bar.removeAllViews();
        mDrawingView.setErase(true);
        mDrawingView.setFill(false);

        View tools = getLayoutInflater().inflate(R.layout.eraser_toolbar, baseLayout, false);

        final ImageButton small_button = tools.findViewById(R.id.small_brush);
        final ImageButton medium_button = tools.findViewById((R.id.medium_brush));
        final ImageButton large_button = tools.findViewById(R.id.large_brush);

        small_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.setBrushSize(smallBrush);
                small_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
                medium_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
                large_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            }
        });
        medium_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.setBrushSize(mediumBrush);
                small_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
                medium_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
                large_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            }
        });
        large_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.setBrushSize(largeBrush);
                small_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
                medium_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
                large_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
            }
        });

        int currSize = mDrawingView.getLastBrushSize();
        if(currSize == smallBrush){
            small_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
            medium_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            large_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
        }
        if(currSize == mediumBrush){
            small_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            medium_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
            large_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
        }
        if(currSize == largeBrush){
            small_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            medium_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            large_button.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
        }

        LinearLayout brushSizes = tools.findViewById(R.id.brush_sizes);
//        brushSizes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50,0));

        LinearLayout panel = tools.findViewById(R.id.brushes_panel);
//        brushSizes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));



        bot_bar.addView(tools);

    }

    private void showColorFillDialog() {
        bot_bar.removeAllViews();
        mDrawingView.setErase(false);
        mDrawingView.setFill(true);

        View tools = getLayoutInflater().inflate(R.layout.bucket_toolbar, baseLayout, false);


        LinearLayout panel = tools.findViewById(R.id.brushes_panel);
//        brushSizes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));


        for (String cat : colorDb.keySet()) {
            View catContainer = getLayoutInflater().inflate(R.layout.palette_container, null, false);
            LinearLayout catTop = catContainer.findViewById(R.id.palette_top);
            TextView catTitle = catContainer.findViewById(R.id.title_container);
            catTitle.setText(cat);

            Map<String, String> colors = colorDb.get(cat);
            for(String cname : colors.keySet()){
                String hex = colors.get(cname);


                View colContainer = getLayoutInflater().inflate(R.layout.palette_container, null, false);
                LinearLayout colTop = colContainer.findViewById(R.id.palette_top);
                TextView colTitle = colContainer.findViewById(R.id.title_container);
                colTitle.setText(cname);

                ImageButton ib = new ImageButton(this);
                ib.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                ib.setMinimumHeight(175);
                ib.setMinimumWidth(220);

                ib.setBackgroundColor(Color.parseColor(hex));
                ib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int color = 0xff000000;

                        Drawable background = view.getBackground();
                        if (background instanceof ColorDrawable)
                            color = ((ColorDrawable) background).getColor();

                        mDrawingView.setColor(color);
//                        System.out.println(color);
                    }
                });


                colTop.addView(ib);

                catTop.addView(colContainer);
            }

            View div = new View(this);
            div.setMinimumWidth(6);



            panel.addView(catContainer);
            panel.addView(div);
            panel.invalidate();
        }

        bot_bar.addView(tools);

    }

    private void selectCurrentBrush(){
        int currBrush = mDrawingView.getLastBrushSize();
        if(currBrush == smallBrush){
            findViewById(R.id.small_brush)
                    .setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
            findViewById(R.id.small_brush).invalidate();

            findViewById(R.id.medium_brush)
                    .setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            findViewById(R.id.medium_brush).invalidate();

            findViewById(R.id.large_brush)
                    .setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            findViewById(R.id.large_brush).invalidate();


        }
        if(currBrush == mediumBrush){
            findViewById(R.id.small_brush)
                    .setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            findViewById(R.id.small_brush).invalidate();

            findViewById(R.id.medium_brush)
                    .setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
            findViewById(R.id.medium_brush).invalidate();

            findViewById(R.id.large_brush)
                    .setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            findViewById(R.id.large_brush).invalidate();
        }
        if(currBrush == largeBrush){
            findViewById(R.id.small_brush)
                    .setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            findViewById(R.id.small_brush).invalidate();

            findViewById(R.id.medium_brush)
                    .setBackgroundColor(getApplicationContext().getResources().getColor(R.color.transparent));
            findViewById(R.id.medium_brush).invalidate();

            findViewById(R.id.large_brush)
                    .setBackgroundColor(getApplicationContext().getResources().getColor(R.color.select_grey));
            findViewById(R.id.large_brush).invalidate();
        }
    }
}
