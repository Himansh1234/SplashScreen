package com.example.vsvll.splashscreen;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Upload_post extends AppCompatActivity implements LocationListener {

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    LocationManager locationManager;
    String pictureFilePath;
    ImageView circleImageView;
    LinearLayout layout;
    private static final String IMAGE_DIRECTORY = "/YourDirectName";
    private Context mContext;
    ArrayList<Uri> bitmaps;
    RecyclerView mRecyclerView;
    Upload_Adapter adapter;
    GridView gridView;
    ArrayAdapter<Bitmap> arrayAdapter;
    private int GALLERY = 1, CAMERA = 2;
    public int PERMISSIONS_MULTIPLE_REQUEST=1;

    int i,success=1;
    String city = null,userId;

    StorageReference sr = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        bitmaps = new ArrayList<>();

        layout = findViewById(R.id.linearlayout);
        Button upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(((Upload_Adapter) mRecyclerView.getAdapter()).getItemCount()<5)
                    checkPermission();
                else
                    Toast.makeText(v.getContext(),"ONLY FIVE PHOTOS ARE ALLOWS",Toast.LENGTH_SHORT).show();

            }
        });

        mRecyclerView = findViewById(R.id.upload_pic_list);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        Upload_Adapter myAdapter = new Upload_Adapter(this,mRecyclerView, new ArrayList<Bitmap>());
        mRecyclerView.setAdapter(myAdapter);

        Button uploadPost = findViewById(R.id.upload_post);
        Button curLocation = findViewById(R.id.upload_currentlocation);

        uploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
                uploadImg();
            }
        });

        curLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocation();

            }
        });


    }
    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
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
        //  Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent,"SELECT..."), GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    int pic_count =((Upload_Adapter) mRecyclerView.getAdapter()).getItemCount();
                    if (count > 5 - pic_count) {
                        count = 5 - pic_count;
                        Toast.makeText(this, "ONLY FIVE PHOTOS ARE ALLOWS", Toast.LENGTH_SHORT).show();
                    }

                    for (int i = 0; i < count && pic_count <= 5; i++) {
                        Uri contentURI = data.getClipData().getItemAt(i).getUri();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                            ((Upload_Adapter) mRecyclerView.getAdapter()).update(bitmap);
                            bitmaps.add(contentURI);


                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }

                else{
                    Uri uri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                        ((Upload_Adapter) mRecyclerView.getAdapter()).update(bitmap);
                        bitmaps.add(uri);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }

                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ((Upload_Adapter) mRecyclerView.getAdapter()).update(thumbnail);
            bitmaps.add(data.getData());
            Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(Upload_post.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(Upload_post.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showPictureDialog();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 9);
        }
    }

    private void checkLocation() {
        if ( ContextCompat.checkSelfPermission(Upload_post.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,this);
          if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

                  new AlertDialog.Builder(this)
                          .setMessage("Please activate your GPS Location!")
                          .setCancelable(false)
                          .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int id) {
                                  Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                  startActivity(i);
                              }
                          })
                          .setNegativeButton("Cancel", null)
                          .show();
          }

        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 9:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(cameraPermission && readExternalFile)
                    {
                        showPictureDialog();
                    } else {
                        Toast.makeText(this,"Please Give Permission",Toast.LENGTH_SHORT).show();

                    }
                }
                break;

            case 1:
                if (grantResults.length > 0) {
                    boolean loc_fine = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if( loc_fine)
                    {

                    } else {
                        Toast.makeText(this,"Please Give Permission",Toast.LENGTH_SHORT).show();

                    }
                }

                break;
        }
    }


    void uploadData(){
        Long ts =(System.currentTimeMillis());
        String timestamp = ts.toString();
        String date = DateFormat.format("dd-MM-yyyy HH:mm",ts).toString();



        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;

         userId = timestamp+"_"+ user.getUid();


        EditText title = findViewById(R.id.upload_title);
        EditText add = findViewById(R.id.upload_address);
        EditText desc = findViewById(R.id.upload_desc);
        EditText cityy = findViewById(R.id.upload_city);

        String name,address,descp,city_t;
        name = title.getText().toString();
        address = add.getText().toString();
        descp = desc.getText().toString();
        city = cityy.getText().toString().toLowerCase();
        city_t=cityy.getText().toString().toLowerCase();






       if(name.equals("")||address.equals("")||descp.isEmpty()||city.isEmpty())
       {
           Toast.makeText(getApplicationContext(),"FILL ALL REQUIRED DETAILS",Toast.LENGTH_SHORT).show();
       }
       else {
           DatabaseReference dr = FirebaseDatabase.getInstance().getReference("post");
           dr = dr.child(city_t).child(userId).child("detail");
           dr.child("name").setValue(name);
           dr.child("desc").setValue(descp);
           dr.child("time").setValue(date);
           dr.child("place").setValue(address);
           dr.child("username").setValue(user.getDisplayName());
           dr.child("city").setValue(city_t);


       }

    }


    void uploadImg(){

           final ProgressDialog progress = new ProgressDialog(this);
           progress.setMessage("Loading ");
           progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
           progress.show();


           for (i = 0; i < bitmaps.size(); i++) {
              StorageReference msr = sr.child(userId).child(bitmaps.get(i).getLastPathSegment());
              // Toast.makeText(getApplicationContext(),bitmaps.get(i).toString(),Toast.LENGTH_SHORT).show();

               msr.putFile(bitmaps.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       Toast.makeText(getApplicationContext(),"IMAGE UPLOADed",Toast.LENGTH_SHORT).show();

                       DatabaseReference finalDr = FirebaseDatabase.getInstance().getReference("post").child(city).child(userId).child("img");
                       finalDr.child( "1").setValue(taskSnapshot.getUploadSessionUri());
                      // Toast.makeText(getApplicationContext(),i+".."+bitmaps.size(),Toast.LENGTH_SHORT).show();

                       if (i == bitmaps.size() - 1) {
                           progress.dismiss();

                           Intent in = new Intent(getApplicationContext(), MainActivity.class);
                           startActivity(in);
                           finish();
                       }
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       progress.dismiss();
                       Toast.makeText(getApplicationContext(),"IMAGE UPLOAD FAILED"+e.toString(),Toast.LENGTH_SHORT).show();
                   }
               });
           }

    }
    @Override
    public void onLocationChanged(Location location) {
        EditText addre =  findViewById(R.id.upload_address);
        EditText city =  findViewById(R.id.upload_city);

        String cityName=null,address=null;
        Geocoder gcd = new Geocoder(getApplicationContext(),
                Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location
                    .getLongitude(), 1);
            if (addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0);
                cityName = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        city.setText(cityName);
        addre.setText(address);

    }

    @Override
    public void onProviderDisabled(String provider) {
      //  Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
      //  Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Log.d("Latitude","status");
    }
}

