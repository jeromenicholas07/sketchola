package cc.SketchScape;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FlaskTask extends AsyncTask<RequestBody, Void, Bitmap> {

    final private String url = "http://104.197.55.76:5000/api/process";
    private DrawActivity context;

    public FlaskTask(DrawActivity context) {
        this.context = context;
    }

    @Override
    protected void onPostExecute(Bitmap outputImage) {
        super.onPostExecute(outputImage);
        Intent intent = new Intent(context, ResultActivity.class);

        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        outputImage = Bitmap.createScaledBitmap(outputImage, 1024, 1024, false);
        outputImage.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] byteArray = bStream.toByteArray();

        intent.putExtra("img", byteArray);
        context.startActivity(intent);
        context.dialog.dismiss();
    }

    @Override
    protected Bitmap doInBackground(RequestBody... requestBodies) {
        RequestBody postBody = requestBodies[0];

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build();
        ;

        Request request = new Request.Builder()
                .url(url)
                .post(postBody)
                .header("Connection", "close")
                .build();

        try {
            Response response = client.newCall(request).execute();
            System.out.println(response.headers().toString());

            byte[] bytes = response.body().bytes();
//            BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
//            System.out.println("bytes len: " + is.);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
//        enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, final IOException e) {
//                // Cancel the post on failure.
//                call.cancel();
//                Log.d("FAIL", e.getMessage());
//                System.out.println("Failed: " + e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) {
//
//
//                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
//
//                System.out.println(response.toString());
//
//                System.out.println(response.headers().toString());
//                System.out.println(response.body().contentType());
//
//                Bitmap outputImage = BitmapFactory.decodeStream(response.body().byteStream());
//
//
//            }
//        });
//        return null;
    }
}


