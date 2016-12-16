package pro.sample.my.last_pro;

/**
 * Created by admin on 12/14/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;

/*import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;*/

public class Edit extends Activity  {
    private RelativeLayout drawingLayout;
    //private MyView myView;
    Button red, blue, yellow, cyan, gray, green, magenta, white,detect,effectsButton,fill;
    ImageButton backButton,saveButton;
    Paint paint;
    ImageView viewImage;
    Bitmap mBitmap,bBitmap,cBitmap,thumbnail, change;
    Uri uri;
    String picturePath,img_a;
    TextView tv;
    File imageFile;
    String path_p;
    final static int KERNAL_WIDTH = 3;
    final static int KERNAL_HEIGHT = 3;

    int[][] kernal ={
            {0, -1, 0},
            {-1, 4, -1},
            {0, -1, 0}
    };
//    ProgressBar progressBar;

    private Handler handler;
    Bitmap afterProcess,bitmap_Source;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        effectsButton = (Button) findViewById(R.id.effectsButton);
        fill = (Button) findViewById(R.id.fill);
        backButton = (ImageButton) findViewById(R.id.backButton);
        detect = (Button)findViewById(R.id.detect);
        ImageButton undoButton = (ImageButton) findViewById(R.id.undoButton);
        ImageButton redoButton = (ImageButton) findViewById(R.id.redoButton);
        saveButton = (ImageButton) findViewById(R.id.saveButton);
        // ImageButton colorPickerButton = (ImageButton) findViewById(R.id.colorPickerButton);
        ImageButton toolsButton = (ImageButton) findViewById(R.id.toolsButton);
        // tv = (TextView)findViewById(R.id.textView3);
        red = (Button) findViewById(R.id.red);
        blue = (Button) findViewById(R.id.blue);
        yellow = (Button) findViewById(R.id.yellow);
        cyan = (Button) findViewById(R.id.cyan);
        gray = (Button) findViewById(R.id.gray);
        magenta = (Button) findViewById(R.id.magenta);
        green = (Button) findViewById(R.id.green);
        white = (Button) findViewById(R.id.white);
        detect = (Button) findViewById(R.id.detect);
        viewImage = (ImageView) findViewById(R.id.viewImage);
        uri = getIntent().getData();
       // imageFile = new File(getRealPathFromURI(uri));
        //path_p = imageFile.toString();
        //tv.setText(""+path_p);
        //System.out.print("uri is");
        //System.out.print(uri);
        // Bitmap bitMap = getIntent().getParcelableExtra("bitmap");
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(uri, filePath, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePath[0]);
        picturePath = c.getString(columnIndex);
        c.close();
        thumbnail = (BitmapFactory.decodeFile(picturePath));
        viewImage.setImageBitmap(thumbnail);
        viewImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Intent i = new Intent(edit.this, ColorDet.class);
                //startActivity(i);
                //return true;
                float eventX = event.getX();
                float eventY = event.getY();
                float[] eventXY = new float[]{eventX, eventY};

                Matrix invertMatrix = new Matrix();
                ((android.widget.ImageView) v).getImageMatrix().invert(invertMatrix);

                invertMatrix.mapPoints(eventXY);
                int  x = Integer.valueOf((int) eventXY[0]);
                int  y = Integer.valueOf((int) eventXY[1]);

                if (x < 0) {
                    x = 0;
                } else if (x > thumbnail.getWidth() - 1) {
                    x = thumbnail.getWidth() - 1;
                }

                if (y < 0) {
                    y = 0;
                } else if (y > thumbnail.getHeight() - 1) {
                    y = thumbnail.getHeight() - 1;
                }

                int touchedRGB = thumbnail.getPixel(x, y);
                String img_touch = "#" + Integer.toHexString(touchedRGB);
                Toast toast = Toast.makeText(Edit.this, img_touch, Toast.LENGTH_SHORT);
                toast.show();

                // colorRGB.setText(img_touch);

                return true;
            }
        });
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap_Source = thumbnail;

                handler = new Handler();
                StratBackgroundProcess();
            }
            private void StratBackgroundProcess(){

                    Runnable runnable = new Runnable(){

                        @Override
                        public void run() {
                            afterProcess = processingBitmap(bitmap_Source, kernal);

                            handler.post(new Runnable(){

                                @Override
                                public void run() {
                                    //progressBar.setVisibility(View.GONE);
                                    viewImage.setImageBitmap(afterProcess);
                                }

                            });
                        }
                    };
                    new Thread(runnable).start();
                }

                private Bitmap processingBitmap(Bitmap src, int[][] knl){
                    Bitmap dest = Bitmap.createBitmap(
                            src.getWidth(), src.getHeight(), src.getConfig());

                    int bmWidth = src.getWidth();
                    int bmHeight = src.getHeight();
                    int bmWidth_MINUS_2 = bmWidth - 2;
                    int bmHeight_MINUS_2 = bmHeight - 2;

                    for(int i = 1; i <= bmWidth_MINUS_2; i++){
                        for(int j = 1; j <= bmHeight_MINUS_2; j++){

                            //get the surround 3*3 pixel of current src[i][j] into a matrix subSrc[][]
                            int[][] subSrc = new int[KERNAL_WIDTH][KERNAL_HEIGHT];
                            for(int k = 0; k < KERNAL_WIDTH; k++){
                                for(int l = 0; l < KERNAL_HEIGHT; l++){
                                    subSrc[k][l] = src.getPixel(i-1+k, j-1+l);
                                }
                            }

                            //subSum = subSrc[][] * knl[][]
                            int subSumA = 0;
                            int subSumR = 0;
                            int subSumG = 0;
                            int subSumB = 0;

                            for(int k = 0; k < KERNAL_WIDTH; k++){
                                for(int l = 0; l < KERNAL_HEIGHT; l++){
                                    subSumR += Color.red(subSrc[k][l]) * knl[k][l];
                                    subSumG += Color.green(subSrc[k][l]) * knl[k][l];
                                    subSumB += Color.blue(subSrc[k][l]) * knl[k][l];
                                }
                            }

                            subSumA = Color.alpha(src.getPixel(i, j));

                            if(subSumR <0){
                                subSumR = 0;
                            }else if(subSumR > 255){
                                subSumR = 255;
                            }

                            if(subSumG <0){
                                subSumG = 0;
                            }else if(subSumG > 255){
                                subSumG = 255;
                            }

                            if(subSumB <0){
                                subSumB = 0;
                            }else if(subSumB > 255){
                                subSumB = 255;
                            }

                            dest.setPixel(i, j, Color.argb(
                                    subSumA,
                                    subSumR,
                                    subSumG,
                                    subSumB));
                        }
                    }
                    change = dest;
                    return dest;
                }


            });

        fill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBitmap = thumbnail;
                viewImage.setImageBitmap(processingBitmap(mBitmap));
            }
        });


        red.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // paint.setColor(Color.RED);
            }
        });

        yellow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               //paint.setColor(Color.YELLOW);
            }
        });
        blue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              //  paint.setColor(Color.BLUE);
            }
        });
        cyan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //paint.setColor(Color.CYAN);
            }
        });
        gray.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // paint.setColor(Color.GRAY);
            }
        });
        green.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //paint.setColor(Color.GREEN);
            }
        });
        magenta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // paint.setColor(Color.MAGENTA);
            }
        });
        white.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //paint.setColor(Color.WHITE);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Tag = "at backButton";
                Log.i(Tag, "before intent");
                Intent i = new Intent(Edit.this, MainActivity.class);
                startActivity(i);

            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Tag = "at save button";
                Log.i(Tag, "before intent");
               // img_a = BitMapToString(change);
                Intent i = new Intent(Edit.this, Compare.class);
                i.setData(uri);

                startActivity(i);
            }
        });
        effectsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadGrayBitmap(thumbnail);
               // adjustedContrast(thumbnail,500);
            }

        });



    }
    private Bitmap adjustedContrast(Bitmap src, double value)
    {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.green(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.blue(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
               // viewImage.setImageBitmap(bmOut);
            }
        }
        change = bmOut;
        return bmOut;
    }
    private void loadGrayBitmap(Bitmap src) {
        if (src != null) {

            //Array to generate Identity image
            float[] IdentityArray = {
                    1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            };
            //Array to generate Gray-Scale image
            float[] GrayArray = {
                    0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                    0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                    0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            };
            ColorMatrix colorMatrixGray = new ColorMatrix(GrayArray);
            int w = src.getWidth();
            int h = src.getHeight();
            Bitmap bitmapResult = Bitmap
                    .createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvasResult = new Canvas(bitmapResult);
            Paint paint = new Paint();
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrixGray);
            paint.setColorFilter(filter);
            canvasResult.drawBitmap(src, 0, 0, paint);
            viewImage.setImageBitmap(bitmapResult);
            change = bitmapResult;
        }
    }

    private Bitmap processingBitmap(Bitmap src){

        Bitmap dest = Bitmap.createBitmap(
                src.getWidth(), src.getHeight(), src.getConfig());

        for(int x = 0; x < src.getWidth(); x++){
            for(int y = 0; y < src.getHeight(); y++){
                int pixelColor = src.getPixel(x, y);
                int pixelAlpha = Color.alpha(pixelColor);
                int pixelRed = Color.red(pixelColor);
                int pixelGreen = Color.green(pixelColor);
                int pixelBlue = Color.blue(pixelColor);
                                int newPixel= Color.argb(
                        pixelAlpha, pixelBlue, pixelRed, pixelGreen);
                dest.setPixel(x, y, newPixel);
            }
        }
        change = dest;
        return dest;
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
