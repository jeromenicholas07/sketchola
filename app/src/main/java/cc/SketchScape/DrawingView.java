package cc.SketchScape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class DrawingView extends View {

    // To hold the path that will be drawn.
    private Path drawPath;
    // Paint object to draw drawPath and drawCanvas.
    private Paint drawPaint, canvasPaint;
    // initial color
    private int paintColor = 0xff000000;
    private int previousColor = paintColor;
    // canvas on which drawing takes place.
    private Canvas drawCanvas;
    // canvas bitmap
    private Bitmap canvasBitmap;
    // Brush stroke width
    private float brushSize;
    private int lastBrushSize;
    // To enable and disable erasing mode.
    private boolean erase = false;
    private boolean fill = false;

    private Stack<Bitmap> bitmaps = new Stack<>();
    private Stack<Bitmap> undoneBitmaps = new Stack<>();


    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setUpDrawing();

    }

    public Bitmap getDrawing(){
        return bitmaps.peek();
    }

    public boolean undoPaths(){

        if(bitmaps.size()>1){
            Bitmap copy = Bitmap.createBitmap(bitmaps.pop());
            undoneBitmaps.push(copy);
            canvasBitmap = Bitmap.createBitmap(bitmaps.peek());
            drawCanvas.setBitmap(canvasBitmap);

            invalidate();
            return true;
        }
        else if(bitmaps.size() == 1){
            Bitmap copy = Bitmap.createBitmap(bitmaps.pop());
            undoneBitmaps.push(copy);
            canvasBitmap.eraseColor(Color.WHITE);
            drawCanvas.setBitmap(canvasBitmap);
            invalidate();
            return true;
        }
        return false;
    }

    public boolean redoPaths(){

        if (undoneBitmaps.size()>0)
        {

            Bitmap copy = Bitmap.createBitmap(undoneBitmaps.pop());
            canvasBitmap = Bitmap.createBitmap(bitmaps.push(copy));
            drawCanvas.setBitmap(canvasBitmap);


            invalidate();
            return true;
        }

        invalidate();
        return false;
    }


    private void setUpDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        // Making drawing smooth.
        drawPaint.setAntiAlias(false);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
        canvasPaint.setAntiAlias(false);

        // Initial brush size is medium.
        brushSize = getContext().getResources().getInteger(R.integer.medium_size);
        lastBrushSize = getContext().getResources().getInteger(R.integer.medium_size);;
        drawPaint.setStrokeWidth(brushSize);




    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);

        drawCanvas = new Canvas(canvasBitmap);
        drawCanvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(drawPath, drawPaint);
//        if(bitmaps.size()>0)
//            canvas.drawBitmap(bitmaps.peek(), 0, 0, canvasPaint);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

//        for (Path p : paths.keySet()){
//            canvas.drawPath(p, paths.get(p));
//            System.out.println(p.hashCode() +"  __"+ paths.get(p).getStrokeWidth());
//        }

        canvas.drawPath(drawPath, drawPaint);
    }

    public void setFill(boolean a){
        fill = a;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // X and Y position of user touch.
        float touchX = event.getX();
        float touchY = event.getY();
        // Draw the path according to the touch event taking place.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:


                if(fill){
                    undoneBitmaps.clear();
                    
                    Point p = new Point( (int)touchX, (int)touchY );
                    int col = canvasBitmap.getPixel((int)touchX, (int)touchY);
                    floodFill(canvasBitmap, p, col, paintColor);
                    Bitmap copy = Bitmap.createBitmap(canvasBitmap);
                    bitmaps.push(copy);
                }
                else{
                    drawPath.moveTo(touchX, touchY);
                }

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if(!fill)
                    drawPath.lineTo(touchX, touchY);


                invalidate();
                break;
            case MotionEvent.ACTION_UP:
//                if (erase){
//                    drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//                }
                if(!fill){
                    undoneBitmaps.clear();


                    drawCanvas.drawPath(drawPath, drawPaint);
                    Bitmap copy = Bitmap.createBitmap(canvasBitmap);
                    bitmaps.push(copy);

//                    drawPaint.setXfermode(null);
                }


                drawPath = new Path();

                invalidate();
                break;
            default:
                return false;
        }

        // invalidate the view so that canvas is redrawn.
        invalidate();
        return true;
    }


    float threshold;
    public void setColor(String newColor){
        // invalidate the view
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
        previousColor = paintColor;
    }

    public void setColor(int newColor){
        // invalidate the view
        invalidate();
        paintColor = newColor;
        drawPaint.setColor(paintColor);
        previousColor = paintColor;
    }

    public void setBrushSize(int newSize){
        setLastBrushSize(newSize);
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }
    public void setLastBrushSize(int lastSize){
        lastBrushSize=lastSize;
    }

    public int getLastBrushSize(){
        return lastBrushSize;
    }



    public void setErase(boolean isErase){
        //set erase true or false
        erase = isErase;
        if(erase) {
            drawPaint.setColor(Color.WHITE);
            //drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        else {
            drawPaint.setColor(previousColor);
//            drawPaint.setXfermode(null);
        }
    }

    public void startNew(){

        undoneBitmaps.clear();
        bitmaps.clear();

        canvasBitmap.recycle();
        canvasBitmap = Bitmap.createBitmap(canvasBitmap.getWidth(), canvasBitmap.getHeight(), Bitmap.Config.RGB_565);

        drawCanvas = new Canvas(canvasBitmap);
        onSizeChanged(canvasBitmap.getWidth(), canvasBitmap.getHeight(),canvasBitmap.getWidth(), canvasBitmap.getHeight());
        draw(drawCanvas);
        invalidate();
    }
    int minR, maxR, minG, maxG, minB, maxB;

    private boolean compareColors(int a, int b){

        Color A = Color.valueOf(a);
        Color B = Color.valueOf(a);



        double d = ((A.red()-B.red())*(A.red()-B.red()))+((A.green()-B.green())*(A.green()-B.green()))+
                ((A.blue()-B.blue())*(A.blue()-B.blue()));


        if(d>0){
            System.out.println(d);
        }
        if (d > threshold){
            return false;
        }
        return true;
    }

    private boolean isTolerable(int currentColor){
        int r = currentColor & 0xFF0000;
        int g = currentColor & 0x00FF00;
        int b = currentColor & 0x0000FF;

        if(r<minR || r>maxR || g<minG || g>maxG || b<minB || b>maxB)
            return false;   // less than or grater than tolerable values
        else
            return true;
    }
    private void setThreshParams(int targetColor, float tolerance) {
        /* tolerable values */
        minR = (int) (((targetColor & 0xFF0000) >> 16) - tolerance );
        if(minR < 0) minR = 0;
        else minR = minR << 16;
        maxR = (int)( ((targetColor & 0xFF0000) >> 16) + tolerance );
        if(maxR > 0xFF) maxR = 0xFF0000;
        else maxR = maxR << 16;

        minG = (int)( ((targetColor & 0x00FF00) >> 8) - tolerance );
        if(minG < 0) minG = 0;
        else minG = minG << 8;
        maxG = (int)( ((targetColor & 0x00FF00) >> 8) + tolerance );
        if(maxG > 0xFF) maxG = 0x00FF00;
        else maxG = maxG << 8;

        minB = (int)((targetColor & 0x0000FF) - tolerance);
        if(minB < 0) minB = 0;
        maxB = (int)( (targetColor & 0x0000FF) + tolerance );
        if(maxB > 0xFF) maxB = 0x0000FF;
        /* tolerable values */
    }
    private void floodFill(Bitmap bmp, Point pt, int targetColor, int replacementColor){
        Queue<Point> q = new LinkedList<Point>();
        Path pw = new Path();

        q.add(pt);
        while (q.size() > 0) {
            Point n = q.poll();
            if (bmp.getPixel(n.x, n.y) != targetColor)
                continue;

            Point w = n, e = new Point(n.x + 1, n.y);
            while ((w.x > 0) && (bmp.getPixel(w.x, w.y) == targetColor)) {
                bmp.setPixel(w.x, w.y, replacementColor);
                if ((w.y > 0) && (bmp.getPixel(w.x, w.y - 1) == targetColor))
                    q.add(new Point(w.x, w.y - 1));
                if ((w.y < bmp.getHeight() - 1)
                        && (bmp.getPixel(w.x, w.y + 1) == targetColor))
                    q.add(new Point(w.x, w.y + 1));
                w.x--;
            }
            while ((e.x < bmp.getWidth() - 1)
                    && (bmp.getPixel(e.x, e.y) == targetColor)) {
                bmp.setPixel(e.x, e.y, replacementColor);

                if ((e.y > 0) && (bmp.getPixel(e.x, e.y - 1) == targetColor))
                    q.add(new Point(e.x, e.y - 1));
                if ((e.y < bmp.getHeight() - 1)
                        && (bmp.getPixel(e.x, e.y + 1) == targetColor))
                    q.add(new Point(e.x, e.y + 1));
                e.x++;
            }
        }}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, (int) (4.0/3*width));
    }
}
