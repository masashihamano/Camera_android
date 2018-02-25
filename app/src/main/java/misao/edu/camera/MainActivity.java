package misao.edu.camera;

//①activity_main.xmlにて次を設定　→　<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //send the code when click the button for image click
    private static final int REQUEST_CAPTURE_CODE = 100;

    //check the media type is image or not
    private static final int MEDIA_TYPE_IMAGE = 1;

    // it's directory name where the capture image is save
    private static final String IMAGE_DIRECTORY_NAME = "Camera";

    // Uri(Uniform Resource Identifier) to store image uri
    private Uri fileUri;

    ImageView img;
    Button btn_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        img = findViewById( R.id.imageview );
        btn_img = findViewById( R.id.btn );

        btn_img.setOnClickListener( this );


        if (!isDeviceSupportCamera()) {
            Toast.makeText( this, "Sorry, Your device device doesn't support camera", Toast.LENGTH_SHORT ).show();
            finish();
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        requestRuntimePermission();

    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature( PackageManager.FEATURE_CAMERA )) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE ); // Implicit Intent( It call Hardware component)
        fileUri = getOutputMediaFileUri( MEDIA_TYPE_IMAGE );
        intent.putExtra( MediaStore.EXTRA_OUTPUT, fileUri );
        startActivityForResult( intent, REQUEST_CAPTURE_CODE );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CAPTURE_CODE && resultCode == RESULT_OK) {
            previewCaptureImage();
        } else {
            Toast.makeText( getApplicationContext(), "Sorry for capture image", Toast.LENGTH_SHORT ).show();
        }

    }

    // this method get image and convert in uri
    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile( getOutputMediaFile( type ) );
    }

    public void requestRuntimePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    //this is get the image from external storage directory
    private File getOutputMediaFile(int type) {

        //External Sd card location
        File mediaStorageDir = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES ),IMAGE_DIRECTORY_NAME );

        // It's create the directory if your directory is not available in your phone


        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d( IMAGE_DIRECTORY_NAME, "oops..Failed Create" + IMAGE_DIRECTORY_NAME + "directory" );
                return null;
            }
        }

        //create a file name
        String timeStamp = new SimpleDateFormat( "yyyyMMdd__HHmmss", Locale.getDefault() ).format( new Date() );
        File mediaFile;

        mediaFile = new File( mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg" );

        return mediaFile;
    }

    private void previewCaptureImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();

        //down size the image as it throws out of memory Exception for larger image.

        options.inSampleSize = 8;

        final Bitmap bitmap = BitmapFactory.decodeFile( fileUri.getPath(), options );
        img.setImageBitmap( bitmap );

    }
}




