package com.example.bill1.fcu_case;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private static final String DBname = "DB2019.db";
    private static final int DBversion = 1;
    private static final String TBname = "DB2019";
    private EditText etUseNum;
    private EditText etUseName;
    private EditText etUseSoc;
    private EditText etUseFeel;
    private EditText etUseAdd;
    private Button btAdd, btCancel;
    private SQLdata dbHper;

    private SeekBar seekBar;
    private TextView textView;

    /*private Button btnStart;
    private Button btnStop;
    private TextView textView2;
    private Location mLocation;
    private LocationManager mLocationManager;
    private String best;
    private double lat = 25.0402555,lng=121.512377;*/
    private TextView textLoc;
    private Button button1;
    private Button reset;
    private Button global;
    private LocationManager locationManager;
    private String commandStr;
    private Context context;
    public static final int My_PERMISSION_COARSE_LOCATION = 11;

    ImageView image_view;
    Bitmap bitmap;
    //EditText imageName;
    Button GetImageFromGalleryButton, UploadImageOnServerButton;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    ProgressDialog progressDialog;
    String ImageTag = "image_tag";
    String ImageName = "image_data";
    String ImageEmotionIndex = "image_emotionIndex";
    String ImageEmotionArea = "image_emotionArea";
    String ImageEmotionPoint = "image_emotionPoint" ;
    String ImageLongitude = "image_longitude";
    String ImageLatitude = "image_latitude";


    String GetImageNameFromEditText;
    String GetImageEmotionIndex;
    String GetImageEmotionArea;
    String GetImageEmotionPoint;
    String GetImageLongitude;
    String GetImageLatitude;

    URL url;
    HttpURLConnection httpURLConnection;
    OutputStream outputStream;
    BufferedWriter bufferedWriter;
    BufferedReader bufferedReader;
    int RC;
    StringBuilder stringBuilder;
    boolean check = true;
    private int GALLERY = 1, CAMERA = 2;

    TextClock textclock;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        reset = (Button)findViewById(R.id.butCan);
        global =(Button)findViewById(R.id.global);
        textclock = (TextClock)findViewById(R.id.textclock);
        textLoc = (TextView) findViewById(R.id.textLoc);
        button1 = (Button)findViewById(R.id.button1);


        commandStr = LocationManager.NETWORK_PROVIDER;


        //時間設定
        textclock.setFormat24Hour("yyyy/MM/dd hh:mm");



        final Spinner spinner = (Spinner) findViewById(R.id.idspinner);
        ArrayAdapter<CharSequence> lunchList = ArrayAdapter.createFromResource(MainActivity.this,
                R.array.questions,
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(lunchList);

        final Spinner spinner2 = (Spinner) findViewById(R.id.spinnertwo);
        ArrayAdapter<CharSequence> lunchList2 = ArrayAdapter.createFromResource(MainActivity.this,
                R.array.questions2,
                android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(lunchList2);


        image_view = (ImageView) findViewById(R.id.image_view);
        seekBar = (SeekBar) findViewById(R.id.progress);
        textView = (TextView) findViewById(R.id.text1);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                textView.setText(Integer.toString(progress - 5));
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "開始滑動！");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "停止滑動！");
            }
        });

        final TextView textView3 = textView;

        button1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick (View view)
            {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]
                                    {
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                    },
                            My_PERMISSION_COARSE_LOCATION);
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }



                locationManager.requestLocationUpdates(commandStr, 1000, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(commandStr);

                if (location != null)
                    textLoc.setText("經度" + location.getLongitude() + "\n緯度" + location.getLatitude());
                    //textLoc.setText(commandStr);

                else
                    textLoc.setText("定位中!!!");

                GetImageLongitude  = String.valueOf(location.getLongitude());
                GetImageLatitude = String.valueOf(location.getLatitude());
            }
        });





        byteArrayOutputStream = new ByteArrayOutputStream();

        //etUseNum = (EditText) findViewById(R.id.textNum);
        etUseName = (EditText) findViewById(R.id.textName);
        //etUseFeel = (EditText)findViewById(R.id.idspinner);
        //etUseSoc = (EditText)findViewById(R.id.progress);
        UploadImageOnServerButton = (Button) findViewById(R.id.butAdd);
        GetImageFromGalleryButton = (Button) findViewById(R.id.catch_BT);
        btCancel = (Button) findViewById(R.id.butCan);
        btCancel.setOnClickListener(btCanListener);



        global.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Uri uri=Uri.parse("http://140.134.26.3/webmap/fcumap.php");
                Intent i=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(i);
            }
        });

        GetImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPictureDialog();


            }
        });

        UploadImageOnServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetImageNameFromEditText = etUseName.getText().toString();
                GetImageEmotionIndex = spinner.getSelectedItem().toString();
                GetImageEmotionPoint = textView3.getText().toString();
                GetImageEmotionArea = spinner2.getSelectedItem().toString();


                UploadImageToServer();

            }
        });

        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }

        reset.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent restartIntent = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(restartIntent);
            }
        });
    }

    public LocationListener locationListener = new LocationListener()
    {

        @Override
        public void onLocationChanged(Location location) {
            textLoc.setText("經度" + location.getLongitude() + "\n緯度" + location.getLatitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };










    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action(選擇紀錄影像方式)");
        String[] pictureDialogItems = {
                "Photo Gallery(本地照片)",
                "Camera(開啟照相機)" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {


        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, CAMERA);


    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    // String path = saveImage(bitmap);
                    //Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    image_view.setImageBitmap(bitmap);
                    UploadImageOnServerButton.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {

            bitmap = (Bitmap) data.getExtras().get("data");


            image_view.setImageBitmap(bitmap);
            UploadImageOnServerButton.setVisibility(View.VISIBLE);
            //  saveImage(thumbnail);
            //Toast.makeText(ShadiRegistrationPart5.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }







    private View.OnClickListener btCanListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                etUseName.setText("");
                etUseNum.setText("");
                etUseFeel.setText("");
                etUseSoc.setText("");
            }
        };

    //上傳相片

    // Request to Database
    public void UploadImageToServer(){

        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(MainActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog.dismiss();

                Toast.makeText(MainActivity.this,string1,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageTag, GetImageNameFromEditText);

                HashMapParams.put(ImageName, ConvertImage);

                HashMapParams.put(ImageEmotionIndex,GetImageEmotionIndex);

                HashMapParams.put(ImageEmotionArea,GetImageEmotionArea);

                HashMapParams.put(ImageEmotionPoint,GetImageEmotionPoint);

                HashMapParams.put(ImageLongitude,GetImageLongitude);

                HashMapParams.put(ImageLatitude,GetImageLatitude);

                String FinalData = imageProcessClass.ImageHttpRequest("http://140.134.26.3/AndroidUploadImage/upload-image-to-server.php", HashMapParams);

                //String FinalData = imageProcessClass.ImageHttpRequest("http://192.168.56.1/AndroidUpload/upload-image-to-server.php", HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }




    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);

                httpURLConnection.setConnectTimeout(20000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }

    }

    /*public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera

            }
            else {

                Toast.makeText(MainActivity.this, "Unable to use Camera..Please Allow us to use Camera", Toast.LENGTH_LONG).show();

            }
        }
    }*/





}
