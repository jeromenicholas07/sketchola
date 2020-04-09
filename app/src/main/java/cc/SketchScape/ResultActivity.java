package cc.SketchScape;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ResultActivity extends AppCompatActivity {
    Bitmap mDrawing;
    ImageView outputView;
    ImageButton backBtn;
    ImageButton shareBtn;
    ImageButton saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        outputView = findViewById(R.id.output);
        backBtn = findViewById(R.id.back);
        shareBtn = findViewById(R.id.share);
        saveBtn = findViewById(R.id.save);

        byte[] byteArray = getIntent().getByteArrayExtra("img");
        if(byteArray != null){
            mDrawing = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }

        outputView.setImageBitmap(mDrawing);



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(ResultActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ResultActivity.this,
                        new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },0);
                }

                Intent share = new Intent(Intent.ACTION_SEND);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                mDrawing.setConfig(Bitmap.Config.ARGB_8888);
                mDrawing.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.png");
                try {
                    f.delete();
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                outputView.setImageURI(Uri.parse("file:///sdcard/temporary_file.png"));
                share.setType("image/png");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.png"));
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        });
    }
}
