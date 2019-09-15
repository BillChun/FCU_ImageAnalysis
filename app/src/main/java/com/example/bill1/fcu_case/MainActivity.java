package com.example.bill1.fcu_case;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
    private LocationManager locationManager;
    private String commandStr;
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
    String GetImageNameFromEditText;
    URL url;
    HttpURLConnection httpURLConnection;
    OutputStream outputStream;
    BufferedWriter bufferedWriter;
    BufferedReader bufferedReader;
    int RC;
    StringBuilder stringBuilder;
    boolean check = true;
    private int GALLERY = 1, CAMERA = 2;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textLoc = (TextView) findViewById(R.id.textLoc);
        button1 = (Button)findViewById(R.id.button1);
        commandStr = LocationManager.GPS_PROVIDER;

       /* btnStart = (Button)findViewById(R.id.btnStart);
        btnStop = (Button)findViewById(R.id.btnStop);
        textView2 = (TextView)findViewById(R.id.text);
        btnStart.setOnClickListener(btnClickListener);
        btnStop.setOnClickListener(btnClickListener);*/


       // commandStr = LocationManager.GPS_PROVIDER;
            commandStr = LocationManager.NETWORK_PROVIDER;
        Spinner spinner = (Spinner) findViewById(R.id.idspinner);
        ArrayAdapter<CharSequence> lunchList = ArrayAdapter.createFromResource(MainActivity.this,
                R.array.questions,
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(lunchList);


        image_view = (ImageView) findViewById(R.id.image_view);
        seekBar = (SeekBar) findViewById(R.id.progress);
        textView = (TextView) findViewById(R.id.text1);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                textView.setText("感受程度指數: " + Integer.toString(progress - 5));
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
                else
                    textLoc.setText("定位中!!!");
            }
        });

        byteArrayOutputStream = new ByteArrayOutputStream();

        etUseNum = (EditText) findViewById(R.id.textNum);
        etUseName = (EditText) findViewById(R.id.textName);
        //etUseFeel = (EditText)findViewById(R.id.idspinner);
        //etUseSoc = (EditText)findViewById(R.id.progress);
        UploadImageOnServerButton = (Button) findViewById(R.id.butAdd);
        GetImageFromGalleryButton = (Button) findViewById(R.id.catch_BT);
        btCancel = (Button) findViewById(R.id.butCan);
        btCancel.setOnClickListener(btCanListener);

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

                UploadImageToServer();

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



    /*public Button.OnClickListener btnClickListener = new Button.OnClickListener() {

        public void onClick(View v) {
            Button btn = (Button) v;
            if (btn.getId() == R.id.btnStart) {
                if (!gpsIsOpen())
                    return;
                mLocation = getLocation();
                if (mLocation != null)
                    textView.setText("緯度:" + mLocation.getLatitude() + "\n經度:" + mLocation.getLongitude());
                else
                    textView.setText("獲取不到資料...");
            } else if (btn.getId() == R.id.btnStop) {
                mLocationManager.removeUpdates(locationListener);
            }
        }
    };

    private boolean gpsIsOpen()
    {
        boolean bRet = true;
        LocationManager alm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if(!alm.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Toast.makeText(this,"未開啟GPS!",Toast.LENGTH_SHORT).show();
            bRet = false;
        }
        else
        {
            Toast.makeText(this,"GPS已經開啟!",Toast.LENGTH_SHORT).show();
        }
        return bRet;
    }

    private Location getLocation()
    {
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = mLocationManager.getBestProvider(criteria,true);
        Location location = mLocationManager.getLastKnownLocation(provider);
        mLocationManager.requestLocationUpdates(provider,2000,5,locationListener);
        return location;
    }



    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(location!=null)
                textView2.setText("緯度:"+location.getLatitude()+"經度:"+location.getLongitude());
            else textView2.setText("獲取不到資料"+Integer.toString(RC));
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
    };*/


    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera" };
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


    public  void onResume() {
        super.onResume();
        if (dbHper == null) {
            dbHper = new SQLdata(this, DBname, null, DBversion);

        }

    }

    public void onPause()
    {
        super.onPause();
        if(dbHper!=null)
        {
            dbHper.close();
            dbHper=null;
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
    /*
    // 啟動照相應用程式用的請求代碼，全圖與縮圖
    private static final int TAKE_PICTURE_BIG = 0;
    private static final int TAKE_PICTURE_SMALL = 1;

    // 保存畫面照片物件的資料名稱
    private static final String BITMAP_KEY = "BITMAP_KEY";
    // 儲存顯示在畫面上的照片物件
    private Bitmap bitmap;

    // 全圖照片檔案的目錄與檔名
    private String bigPictureFileName;

    // 顯示照片用的ImageView元件
    private ImageView image_view;

    // 請求授權使用外部儲存設備的請求代碼
    private static final int REQUEST_STORAGE_PERMISSION = 100;



    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        // 裝置改變方向後，結束元件前先儲存目前畫面上的圖形物件
        bundle.putParcelable(BITMAP_KEY, bitmap);
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // 裝置改變方向後，重新啟動元件時先讀取之前儲存的圖形物件
        bitmap = savedInstanceState.getParcelable(BITMAP_KEY);
        // 把讀取的圖形物件設定給ImageView元件
        image_view.setImageBitmap(bitmap);
    }



    // 啟動照相元件並取得全圖
    public void clickBigButton(View view) {
        // 請求授權
        requestStoragePermission();
    }

    // 啟動照相元件並取得縮圖
    public void clickSmallButton(View view) {
        // 建立啟動相機元件的Intent物件
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 啟動照相元件
        startActivityForResult(intent, TAKE_PICTURE_SMALL);
    }

    private File getImageFile() {
        File result = null;

        // 判斷儲存設備是否可以讀寫
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            File storage;

            // 判斷版本是否為Froyo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                // 取得相片儲存目錄
                storage = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
            }
            // 如果是Froyo以前的版本
            else {
                // 取得相片儲存目錄
                storage = new File(Environment.getExternalStorageDirectory()
                        + "/dcim/");
            }

            // 使用年月日_時分秒格式為檔案名稱
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyyMMdd_HHmmss", Locale.getDefault());
            String timeStamp = sdf.format(new Date());

            // 加入前置和後置檔名
            String fileName = "IMG_" + timeStamp + ".jpg";

            result = new File(storage, fileName);
        } else {
            Log.d("Camera01Activity", "External Storage NOT MOUNTED!");
        }

        return result;
    }

    // 通知系統加入參數指定的照片檔案
    private void addGallery(String fileName) {
        // 準備通知系統的Intent物件
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 建立參數指定的File物件
        File file = new File(fileName);
        // 建立參數指定的Uri物件
        Uri uri = Uri.fromFile(file);
        // 設定資料
        intent.setData(uri);
        // 發出廣播事件，由系統接收後把指定的照片加入系統相簿
        sendBroadcast(intent);
    }

    // 讀取第一個參數指定的照片檔案，轉換為第二個ImageView元件的大小後，
    // 載入並顯示，這樣在顯示大型照片檔案的時候，可以節省很多資源
    private void pictureToImageView(String fileName, ImageView imageView) {
        // 取得ImageView元件在畫面上的寬與高
        int targetWidth = imageView.getWidth();
        int targetHeight = imageView.getHeight();

        // 建立調整照片用的Options物件
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 設定為只讀取大小的資訊
        options.inJustDecodeBounds = true;
        // 讀取指定照片檔案的資訊到Options物件
        BitmapFactory.decodeFile(fileName, options);

        // 取得照片檔案的寬與高
        int pictureWidth = options.outWidth;
        int pictureHeight = options.outHeight;
        // 比較ImageView和照片的大小後計算縮小比例
        int scaleFactor = Math.min(pictureWidth / targetWidth,
                pictureHeight / targetHeight);

        // 取消只讀取大小的資訊的設定
        options.inJustDecodeBounds = false;
        // 設定縮小比例
        options.inSampleSize = scaleFactor;

        // 使用建立好的設定載入照片檔案
        Bitmap bitmap = BitmapFactory.decodeFile(fileName, options);
        // 設定給參數指定的ImageView元件
        imageView.setImageBitmap(bitmap);
    }

    private void requestStoragePermission() {
        // 如果裝置版本是6.0（包含）以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 取得授權狀態，參數是請求授權的名稱
            int hasPermission = checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            // 如果未授權
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                // 請求授權
                //     第一個參數是請求授權的名稱
                //     第二個參數是請求代碼
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
                return;
            }
        }

        // 如果裝置版本是6.0以下，
        // 或是裝置版本是6.0（包含）以上，使用者已經授權，
        // 執行全圖拍照
        processImages();
    }

    // 使用者完成授權的選擇以後，Android會呼叫這個方法
    //     第一個參數：請求授權代碼
    //     第二個參數：請求的授權名稱
    //     第三個參數：使用者選擇授權的結果
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        // 如果是使用儲存設備授權請求
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            // 如果在授權請求選擇「允許」
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 執行全圖拍照
                processImages();
            }
            // 如果在授權請求選擇「拒絕」
            else {
                // 顯示沒有授權的訊息
                Toast.makeText(this, "沒有使用儲存設備授權",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
        }
    }

    // 執行全圖拍照
    private void processImages() {
        // 建立啟動相機元件的Intent物件
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 取得檔案位置與名稱，參數指定相簿目錄名稱
        File file = getImageFile();
        // 設定全圖照片檔案名稱
        bigPictureFileName = file.getAbsolutePath();

        // 建立檔案儲存位置的Uri物件
        Uri uri = null;

        if (Build.VERSION.SDK_INT > 21) {
            uri = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
        }
        else {
            uri = Uri.fromFile(file);
        }

        // 設定全圖照片檔案儲存的位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        // 啟動照相元件
        startActivityForResult(intent, TAKE_PICTURE_BIG);
    }

    // 轉換媒體資訊為實際檔案位置與名稱
    private String getUriPath(Uri uri) {
        String result = null;
        String scheme = uri.getScheme();

        if (scheme.equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null,
                    null, null);

            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(
                        MediaStore.Images.Media.DATA);
                result = cursor.getString(column_index);
            }

            cursor.close();
        }

        return result;
    }
    */

    //上傳相片

    // Request to Database
    public void UploadImageToServer(){

        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);

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

                String FinalData = imageProcessClass.ImageHttpRequest("http://140.134.137.224/AndroidUploadImagePhpfiles/upload-image-to-server.php", HashMapParams);

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


}
