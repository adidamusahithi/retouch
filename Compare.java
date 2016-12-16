package pro.sample.my.last_pro;

/**
 * Created by admin on 12/14/2016.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by admin on 11/29/2016.
 */
public class Compare extends AppCompatActivity {
    Uri uri;
    android.widget.ImageView afterImageView, beforeImageView;
    Intent i;
    String img_a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compare);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);
        Button saveButton = (Button)findViewById(R.id.saveButton);
        afterImageView = (ImageView)findViewById(R.id.afterImageView);
        beforeImageView = (ImageView)findViewById(R.id.beforeImageView);

        uri = getIntent().getData();
     // Bitmap change = getIntent().get
        // uri = Uri.parse(extras.getString("imageUri"));
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(uri, filePath, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePath[0]);
        String picturePath = c.getString(columnIndex);
        c.close();
        final Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
        afterImageView.setImageBitmap(thumbnail);
        // iv2.setImageBitmap(thumbnail);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("before cancel");
                //Intent inten1 = new Intent(Compare.this, Edit_p.class);
                //startActivity(inten1);
                //dialog.dismiss();
                finish();
                System.out.println("after cancel");

            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = thumbnail;
                String ImagePath = MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        bitmap,
                        "demo_image",
                        "demo_image"
                );

                Uri uri = Uri.parse(ImagePath);

                Toast.makeText(Compare.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();


                final CharSequence[] options = { "Continue Editing", "Select a photo","quit" };

                AlertDialog.Builder builder = new AlertDialog.Builder(Compare.this);
                builder.setTitle("GO TO..");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Continue Editing"))
                        {
                            System.out.println("before cont. ediit");
                            dialog.dismiss();
                            finish();
                            // Intent intent = new   Intent(Compare.this,Edit_p.class);
                            //startActivity(intent);
                            //System.out.println("after cont. ediit");

                        }
                        else if (options[item].equals("Select a photo"))
                        {
                            Intent intent = new   Intent(Compare.this,MainActivity.class);
                            startActivity(intent);

                        }
                        else if (options[item].equals("quit")) {
                            dialog.dismiss();
                            finish();
                        }
                    }
                });
                builder.show();

            }
        });



        //  viewImage=(android.widget.ImageView)findViewById(R.id.viewImage);

    }

}


