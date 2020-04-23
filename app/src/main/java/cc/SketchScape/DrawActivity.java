package cc.SketchScape;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.androidifygeeks.library.fragment.PageFragment;
import com.androidifygeeks.library.fragment.TabDialogFragment;
import com.androidifygeeks.library.iface.IFragmentListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DrawActivity extends AppCompatActivity implements View.OnClickListener, IFragmentListener {


    LinearLayout bot_bar;
    LinearLayout baseLayout;

    ArrayList<String> labels;
    ArrayList<String> colors;
    DialogFragment colorSelect;
    AlertDialog  dialog;

    private DrawingView mDrawingView;
    private ImageButton newButton, nextButton, undoButton, redoButton, brushButton, fillButton, eraseButton;
    private ImageButton currentPaint;
    private int smallBrush, mediumBrush, largeBrush;
    private RelativeLayout colorPicker;
    private ImageView colorDisplay;
    private TextView colorNameDisplay;

    HashMap<String, HashMap<String, String>> colorDb;

    public DrawActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseLayout = findViewById(R.id.linear_layout);
        bot_bar = findViewById(R.id.bot_bar);

        mDrawingView = (DrawingView)findViewById(R.id.drawing);

        colorPicker = findViewById(R.id.select_color);
        colorDisplay = findViewById(R.id.color_display);
        colorNameDisplay = findViewById(R.id.color_name);


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

        labels = getJsonAsset("labels");
        colors = getJsonAsset("colors");

        colorDb.put("Misc.", generateMap(184,133,144,161,162,180,183));

        colorDb.put("Animals", generateMap(2,17,18,19,20,21,22,23,24,25,26));

        colorDb.put("Vehicles", generateMap(3,4,5,6,7,8,9,10));

        colorDb.put("Outdoor Objects", generateMap(11,12,13,14,15,16,35,36,37,38,39,40,41,42,43,
                44,95,96,97,98,107,113,114,120,121,135,136,137,139,141,143,145,146,147,148,152,163,164,
                165,166,167,172,173,174,175,176,177,178,179,182));

        colorDb.put("Ornaments", generateMap(27,28,29,30,31,32,33,34,105,106,138));

        colorDb.put("Indoor Objects", generateMap(45,46,47,48,49,50,51,52,
                63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83,
                84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94,99,100,101,102,103,104,108,109,110,111,
                115,116,117,118,119,124,132,134,140,142,153,157,168,169,181));

        colorDb.put("Eatables", generateMap(53,54,55,56,57,58,59,60,61,62,122,123,154,171));

        colorDb.put("Landscape", generateMap(112,125,126,127,128,129,130,131,149,150,151,155,156,158,159,160,170));


        showBrushDialog();
        brushButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.toolbar_grey_selected));
        setColor("#000000");
    }

    public void setColor(String hexColor){
        int color = Color.parseColor(hexColor);
        Drawable unwrappedDrawable = colorDisplay.getDrawable();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        colorDisplay.setImageDrawable(wrappedDrawable);
        colorNameDisplay.setText(labels.get(colors.indexOf(hexColor)));
        mDrawingView.setColor(color);
        if(colorSelect!=null && colorSelect.isVisible()){
            colorSelect.dismiss();
        }
    }

    public void setColor(int color){
        String hexCode = String.format("#%06X", (0xFFFFFF & color)).toLowerCase();
        Drawable unwrappedDrawable = colorDisplay.getDrawable();
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        colorDisplay.setImageDrawable(wrappedDrawable);
        colorNameDisplay.setText(labels.get(colors.indexOf(hexCode)));
        mDrawingView.setColor(color);
    }

    public HashMap<String, String> generateMap(int...a){
        HashMap<String, String> m = new HashMap<>();
        for(int i : a){
            m.put(labels.get(i-2), colors.get(i-2));
        }
        return m;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id){
            case R.id.select_color:

//                final Dialog dialog = new Dialog(DrawActivity.this);
//                dialog.setContentView(R.layout.fragment_color_select_dialog);
//                dialog.setTitle("Title...");
//
//                TabLayout tabLayout = dialog.findViewById(R.id.tab_layout);
//                final ViewPager viewPager = dialog.findViewById(R.id.view_pager);
//                ColorFragmentPagerAdapter colViewPagerAdapter = new ColorFragmentPagerAdapter(getSupportFragmentManager(), colorDb);
//                viewPager.setAdapter(colViewPagerAdapter);
//                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//                    @Override
//                    public void onTabSelected(TabLayout.Tab tab) {
//                        viewPager.setCurrentItem(tab.getPosition());
//                    }
//
//                    @Override
//                    public void onTabUnselected(TabLayout.Tab tab) {
//
//                    }
//
//                    @Override
//                    public void onTabReselected(TabLayout.Tab tab) {
//
//                    }
//                });
//                tabLayout.setupWithViewPager(viewPager);
//                dialog.show();


                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("select-color");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                colorSelect = new ColorSelectDialogFragment();
                colorSelect.show(ft, "select-color");



//                TabDialogFragment.createBuilder(DrawActivity.this, getSupportFragmentManager())
//                        .setTitle("Color picker")
//                        .setTabButtonText(colorDb.keySet().toArray(new String[20]))
//                        .setPositiveButtonText("< back")
//                        .show();

                break;
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

                processImage();

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

    private void processImage() {

        AlertDialog.Builder builder =  new AlertDialog.Builder(this)
                .setTitle("Processing")
                .setMessage("Please wait...")
                .setCancelable(false);

        dialog = builder.create();
        dialog.show();

        int[] flattenedArr = downsampleImage();

        if(flattenedArr == null){
            Toast.makeText(DrawActivity.this, "Draw something!", Toast.LENGTH_LONG);
        }
//        Bitmap inp = Bitmap.createBitmap(256,256, Bitmap.Config.RGBA_F16);
//        int ind = 0;
//        for(int i = 0; i < 256; i++){
//            for(int j =0; j < 256; j++){
//                inp.setPixel(j,i,Color.rgb(flattenedArr[ind],flattenedArr[ind],flattenedArr[ind]));
//                ind++;
//            }
//        }
//
//
//        Intent intent = new Intent(DrawActivity.this, ResultActivity.class);
//
//        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
//        inp = Bitmap.createScaledBitmap(inp, 1024,1024, false);
//        inp.compress(Bitmap.CompressFormat.PNG, 100, bStream);
//        byte[] byteArray = bStream.toByteArray();
//
//        intent.putExtra("img", byteArray);
//        startActivity(intent);

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        JSONArray jsonArray = new JSONArray(Arrays.asList(flattenedArr));
        multipartBodyBuilder.addFormDataPart("data", jsonArray.toString());

        RequestBody postBody = multipartBodyBuilder.build();

        new FlaskTask(DrawActivity.this).execute(postBody);



//            Tensor inputTensor = Tensor.fromBlob(flattenedArr , new long[]{1,1,256,256});
//            Map<String, IValue> inpMap = new HashMap<>();
//            inpMap.put("label", );
//            inpMap.put("instance", IValue.from(inputTensor));

//            Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();
//            System.out.println(Arrays.toString(outputTensor.shape()));
//
//            float[] output = outputTensor.getDataAsFloatArray();
//            int[] zzz = new int[output.length];
//
//            for(int i = 0 ; i < output.length; i++){
//                zzz[i] = (int)(((output[i] + 1)/2.0)*255.0);
//            }
//
//            int[] op_img = new int[256*256];
//
//            for(int i = 0; i < op_img.length; i++){
//
//                op_img[i] = Color.rgb(zzz[i],zzz[i+(256*256)], zzz[i+(256*256*2)]);
//            }


//            Bitmap outputImage = Bitmap.createBitmap(op_img, 256, 256, Bitmap.Config.RGBA_F16);
//
//            Intent intent = new Intent(DrawActivity.this, ResultActivity.class);
//
//            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
//            outputImage = Bitmap.createScaledBitmap(outputImage, 1024,1024, false);
//            outputImage.compress(Bitmap.CompressFormat.PNG, 100, bStream);
//            byte[] byteArray = bStream.toByteArray();
//
//            intent.putExtra("img", byteArray);
//            startActivity(intent);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


    public int[] downsampleImage(){
//        int[] ogpixels = new int[10000*1000];
//        Bitmap b = mDrawingView.getDrawing();
//        b.getPixels(ogpixels,0,b.getWidth(),0,0,b.getWidth(),b.getHeight());
//        List<String> uc = new ArrayList<>();
//        System.out.println("detected pixels:");
//        for(int i : ogpixels){
//            String hexCode = String.format("#%06X", (0xFFFFFF & i));
//            if(!uc.contains(hexCode)){
//                uc.add(hexCode);
//                System.out.println(hexCode);
//            }
//        }

        Bitmap drawing = mDrawingView.getDrawing();
        if(drawing == null){
            return null;
        }
        Bitmap bitmap = Bitmap.createScaledBitmap(drawing, 256, 256, false);
        ArrayList<String> colors = getJsonAsset("colors");
        assert colors != null;


        int [] allpixels = new int [bitmap.getHeight() * bitmap.getWidth()];
        bitmap.getPixels(allpixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        List<String> uniColors = new ArrayList<>();
        System.out.println("colors: ");
        for(int i = 0; i < allpixels.length; i++){
            String hexCode = String.format("#%06X", (0xFFFFFF & allpixels[i])).toLowerCase();

            if(colors.contains(hexCode)){
                if(!uniColors.contains(hexCode)){
                    uniColors.add(hexCode);
                    System.out.println(hexCode + " :: " + colors.indexOf(hexCode));
                }

                allpixels[i] = colors.indexOf(hexCode);
            }
            else{
                if(hexCode.equals("#ffffff")){
                    hexCode = "#000000";
                }

                double minDist = Double.MAX_VALUE;
                String newHex = "#000000";
                for(String col : colors){
                    double diff = colorDiff(col, hexCode);
                    if(minDist > diff){
                        minDist = diff;
                        newHex = col;
                    }
                }

                if(!uniColors.contains(hexCode.toLowerCase())){
                    uniColors.add(hexCode.toLowerCase());
                    System.out.println(hexCode + " :: unidentified : " + colors.indexOf(newHex));
                }
                allpixels[i] = colors.indexOf(newHex);
            }
        }
        return allpixels;
    }

    private double colorDiff(String hexA, String hexB){
        Color a = Color.valueOf(Color.parseColor(hexA));
        Color b = Color.valueOf(Color.parseColor(hexB));
        return Math.sqrt(Math.pow(a.red() - b.red(), 2)
                + Math.pow(a.green() - b.green(), 2)
                + Math.pow(a.blue() - b.blue(), 2));
    }

    private ArrayList<String> getJsonAsset(String fileName){
        ArrayList<String> list = new ArrayList<>();
        String json = null;

        try {
            InputStream is = getAssets().open(fileName + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            JSONArray labelName = new JSONArray(json);
            HashMap<String, String> m_li;

            for (int i = 0; i < labelName.length(); i++) {
                list.add(labelName.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            System.out.println("file found " + file.getAbsolutePath());
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
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

//        LinearLayout brushSizes = tools.findViewById(R.id.brush_sizes);
////        brushSizes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50,0));
//
//        LinearLayout panel = tools.findViewById(R.id.brushes_panel);
////        brushSizes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));
//
//
//        for (String cat : colorDb.keySet()) {
//            View catContainer = getLayoutInflater().inflate(R.layout.palette_container, null, false);
//            LinearLayout catTop = catContainer.findViewById(R.id.palette_top);
//            TextView catTitle = catContainer.findViewById(R.id.title_container);
//            catTitle.setText(cat);
//
//            Map<String, String> colors = colorDb.get(cat);
//            for(String cname : colors.keySet()){
//                String hex = colors.get(cname);
//
//
//                View colContainer = getLayoutInflater().inflate(R.layout.palette_container, null, false);
//                LinearLayout colTop = colContainer.findViewById(R.id.palette_top);
//                TextView colTitle = colContainer.findViewById(R.id.title_container);
//                colTitle.setText(cname);
//
//                ImageButton ib = new ImageButton(this);
//                ib.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
//                ib.setMinimumHeight(175);
//                ib.setMinimumWidth(220);
//
//                ib.setBackgroundColor(Color.parseColor(hex));
//                ib.setTag(hex);
//                ib.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        int color = 0xff000000;
//
////                        Drawable background = view.getBackground();
////                        if (background instanceof ColorDrawable)
////                            color = ((ColorDrawable) background).getColor();
//                        color = Color.parseColor((String) view.getTag());
//
//                        mDrawingView.setColor(color);
////                        System.out.println(color);
//                    }
//                });
//
//
//                colTop.addView(ib);
//
//                catTop.addView(colContainer);
//            }
//
//            View div = new View(this);
//            div.setMinimumWidth(6);
//
//
//
//            panel.addView(catContainer);
//            panel.addView(div);
//            panel.invalidate();
//        }

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

//        View tools = getLayoutInflater().inflate(R.layout.bucket_toolbar, baseLayout, false);


//        LinearLayout panel = tools.findViewById(R.id.brushes_panel);
////        brushSizes.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1));
//
//
//        for (String cat : colorDb.keySet()) {
//            View catContainer = getLayoutInflater().inflate(R.layout.palette_container, null, false);
//            LinearLayout catTop = catContainer.findViewById(R.id.palette_top);
//            TextView catTitle = catContainer.findViewById(R.id.title_container);
//            catTitle.setText(cat);
//
//            Map<String, String> colors = colorDb.get(cat);
//            for(String cname : colors.keySet()){
//                String hex = colors.get(cname);
//
//
//                View colContainer = getLayoutInflater().inflate(R.layout.palette_container, null, false);
//                LinearLayout colTop = colContainer.findViewById(R.id.palette_top);
//                TextView colTitle = colContainer.findViewById(R.id.title_container);
//                colTitle.setText(cname);
//
//                ImageButton ib = new ImageButton(this);
//                ib.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
//                ib.setMinimumHeight(175);
//                ib.setMinimumWidth(220);
//
//                ib.setBackgroundColor(Color.parseColor(hex));
//                ib.setTag(hex);
//                ib.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        int color = 0xff000000;
//                        color = Color.parseColor((String) view.getTag());
//
//                        mDrawingView.setColor(color);
//
////                        Drawable background = view.getBackground();
////                        if (background instanceof ColorDrawable)
////                            color = ((ColorDrawable) background).getColor();
////
////                        mDrawingView.setColor(color);
////                        System.out.println(color);
//                    }
//                });
//
//
//                colTop.addView(ib);
//
//                catTop.addView(colContainer);
//            }
//
//            View div = new View(this);
//            div.setMinimumWidth(6);
//
//
//
//            panel.addView(catContainer);
//            panel.addView(div);
//            panel.invalidate();
//        }

//        bot_bar.addView(tools);

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

    @Override
    public void onFragmentViewCreated(Fragment fragment) {
        int selectedTabPosition = fragment.getArguments().getInt(PageFragment.ARG_DAY_INDEX, 0);
        View rootContainer = fragment.getView().findViewById(R.id.root_container);

        switch (selectedTabPosition) {
            case 0:
                // add view in container for first tab
                fillSelectedTab(rootContainer, "Landscape");
            break;
            case 1:
                fillSelectedTab(rootContainer, "Animals");
            break;
            case 2:
                fillSelectedTab(rootContainer, "Outdoor Objects");
            break;
            case 3:
                fillSelectedTab(rootContainer, "Vehicles");
            break;
            case 4:
                fillSelectedTab(rootContainer, "Indoor Objects");
            break;
            case 5:
                fillSelectedTab(rootContainer, "Ornaments");
            break;
            case 6:
                fillSelectedTab(rootContainer, "Eatables");
            break;
            case 7:
                fillSelectedTab(rootContainer, "Misc.");
            break;
        }
    }

    private void fillSelectedTab(View rootContainer, String key){
        View tabLayout = getLayoutInflater().inflate(R.layout.color_list, (ViewGroup) rootContainer);

        ListView colorList = tabLayout.findViewById(R.id.colorListView);
        colorList.setAdapter(new ColorListAdapter(DrawActivity.this, colorDb.get(key)));
    }

    @Override
    public void onFragmentAttached(Fragment fragment) {

    }

    @Override
    public void onFragmentDetached(Fragment fragment) {

    }
}
