package com.example.rahulstudy.blubeekon_admin;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bluvision.sdk.beacons.Beacon;
import com.bluvision.sdk.beacons.BeaconManager;
import com.bluvision.sdk.constants.Distance;
import com.bumptech.glide.Glide;
import com.example.rahulstudy.blubeekon_admin.Data.BleDevice;
import com.example.rahulstudy.blubeekon_admin.Data.ProjectData;
import com.example.rahulstudy.blubeekon_admin.other.CustomListAdapter;
import com.example.rahulstudy.blubeekon_admin.other.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import static com.example.rahulstudy.blubeekon_admin.HomeActivity.myuser;

public class AdminPageActivity extends AppCompatActivity {

   ImageView projectpic;
   TextView title,content,like,dislike,setupDevice;
   SharedPreferences pref;
   String projectkey;
   ProjectData project;
   Vibrator v;
   Boolean isValueSet=false;
   ListView listView;
   ArrayList<BleDevice> devices=new ArrayList<>();
   Dialog setupDialog,editMyProjectDialog;
   //Details for recycler view
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    BeaconManager beaconManager;
    private TextView latitudeText,longitudeText;
    //Attributes for timer
   Calendar cal;
   long starttime;
   long endtime;
   long timeused;

   //Attributes for Project Editor dialog
    EditText editName,editAbout;
    ImageView editImagePic;
    int PICK_IMAGE_REQUEST=21;
    File imagefile;
    TextView editError;


    //Used for fetching location
    private LocationManager locationManager;
    private LocationListener locationListener;

    //Loading Dialog
    Dialog loadingDialog;
    TextView message;

    //attributes used for loading the like and dislike chart
    DecoView likeArc,dislikeArc;
    SeriesItem seriesItem1,seriesItem2;

    ProgressBar blufiprogress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test1);
       initialization();
        projectDataConnector();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false
        );
    }

    public void initialization() {
        pref=getSharedPreferences("blubeekonadmin",this.MODE_PRIVATE);
        projectkey=pref.getString("projectkey","");
        projectpic=(ImageView)findViewById(R.id.mainprojectpic);
        title=(TextView)findViewById(R.id.mainprojectname);
        content=(TextView)findViewById(R.id.mainprojectcontent);
        like=(TextView)findViewById(R.id.like);
        dislike=(TextView)findViewById(R.id.dislike);
        //dislikeArc = (DecoView)findViewById(R.id.dynamicArcViewDislike);
        //likeArc = (DecoView)findViewById(R.id.dynamicArcViewLike);


        Toast.makeText(this,projectkey,Toast.LENGTH_LONG).show();
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        BeaconListener listener=new BeaconListener();
         beaconManager=new BeaconManager(getBaseContext(),listener);

        setProjectList();
        setupInitialization();
        EditProjectInitialization();
         cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));


        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        message=(TextView)loadingDialog.findViewById(R.id.message);

    }

    public void EditProjectInitialization() {
        editMyProjectDialog=new Dialog(this);
        editMyProjectDialog.setContentView(R.layout.project_edit_popup);
        editMyProjectDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme; //style id
        editName=(EditText) editMyProjectDialog.findViewById(R.id.editname);
        editAbout=(EditText) editMyProjectDialog.findViewById(R.id.editcontent);
        editImagePic=(ImageView) editMyProjectDialog.findViewById(R.id.editpic);
        editError=(TextView)editMyProjectDialog.findViewById(R.id.editerror);





    }

    public void setLikeInformationChart(int like,int dislike)
    {
        int total=like+dislike;
        int percentageLike=(int)(((float)like/total)*100);
        likeArc.deleteAll();
        likeArc.addSeries(new SeriesItem.Builder(Color.argb(255, 197, 194, 191))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setLineWidth(32f)
                .build());
        seriesItem1=new SeriesItem.Builder(Color.argb(255, 31, 163, 233))
                .setRange(0, 100, percentageLike)
                .setLineWidth(32f)
                .build();
        likeArc.addSeries(seriesItem1);


        dislikeArc.deleteAll();
        dislikeArc.addSeries(new SeriesItem.Builder(Color.argb(255, 197, 194, 191))
                .setRange(0, 100, 100)
                .setInitialVisibility(true)
                .setLineWidth(32f)
                .build());
        seriesItem2=new SeriesItem.Builder(Color.argb(255, 253, 0, 2))
                .setRange(0, 100, 100-percentageLike)
                .setLineWidth(32f)
                .build();
        dislikeArc.addSeries(seriesItem2);




    }
    public void setupInitialization() {
        setupDialog=new Dialog(this);
        setupDialog.setContentView(R.layout.setup_booth_popup);
        setupDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme; //style id
        blufiprogress=(ProgressBar)setupDialog.findViewById(R.id.blufiProgressbar);
        listView=(ListView)setupDialog.findViewById(R.id.beaconlist);
        setupDevice=(TextView)setupDialog.findViewById(R.id.textView6);
        CustomListAdapter listadpater=new CustomListAdapter(this,devices);
        listView.setAdapter(listadpater);


    }

    public void initializeRecyclerView() {
        //Initialization of Recycler View
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
       RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames, mImageUrls);
        recyclerView.setAdapter(adapter);

    }

    public void downloadfile() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        StorageReference myRef=storageRef.child("images/"+myuser.getProjectkey()+".jpg");
        File localFile = null;
        try {
            localFile = File.createTempFile(myuser.getProjectkey(), "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        myRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });
    }

    public void setData(ProjectData project) {
        if(isValueSet)
        {
            like.setText(project.getLike()+"");
            dislike.setText(project.getDislike()+"");
            //setLikeInformationChart(project.getLike(),project.getDislike());
            v.vibrate(25);

        }
        else {
            title.setText(project.getName());
            content.setText(project.getAbout());
            like.setText(project.getLike() +"");
            dislike.setText(project.getDislike() + "");
            //setLikeInformationChart(project.getLike(),project.getDislike());

            Glide.with(this)
                    .asBitmap()
                    .load(project.getImageurl())
                    .into(projectpic);
            isValueSet=true;
        }

    }

    public void projectDataConnector() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("projects");
        myRef=myRef.child(projectkey);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                project=dataSnapshot.getValue(ProjectData.class);
                setData(project);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getBaseContext(),"Database error",Toast.LENGTH_LONG).show();
            }
        });

    }

    public void setUpBooth(View view) {
        view.playSoundEffect(SoundEffectConstants.CLICK);
        setupDialog.show();
    }

    public void setProjectList() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("projects");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProjectData project;
                mNames.clear();
                mImageUrls.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    project=snapshot.getValue(ProjectData.class);
                    if(!project.getName().equals(""))
                    { mNames.add(project.getName());
                        mImageUrls.add(project.getImageurl());}


                }
                initializeRecyclerView();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Activity when a beacon i s selected from the llist declared over here
    public void updateBeaconList() {
        //creating the adapter
        CustomListAdapter adapter = new CustomListAdapter(this, devices);

        //attaching adapter to the listview
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                setupDevice.setText("Selected : "+devices.get(position).getMacId());
                project.setBeacon(devices.get(position).getMacId());
                beaconManager.stopScanning();
            }
        });
    }

    public void searchBeacon(View view) {
        beaconManager.startScanning();
        blufiprogress.setVisibility(View.VISIBLE);
    }

    public void saveSetup(View view) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("projects");
        myRef.child(projectkey).setValue(project);
        myRef=database.getReference("projectsetups");
        myRef.child(project.getBeacon()).setValue(project.getName());
        setupDialog.dismiss();
        beaconManager.stopScanning();
        blufiprogress.setVisibility(View.INVISIBLE);
    }



    public void logout(View view) {
        SharedPreferences.Editor configEditor=pref.edit();
        configEditor.putBoolean("login",false);
        configEditor.remove("username");
        configEditor.remove("password");
        configEditor.remove("projectkey");
        configEditor.remove("projectState");
        configEditor.apply();
        Intent loginscreen=new Intent(this,HomeActivity.class);
        startActivity(loginscreen);
    }

    public class BeaconListener implements com.bluvision.sdk.beacons.BeaconManager.BeaconListener   {
        BleDevice device;
        @Override
        public void onFound(Beacon beacon) {
            device=new BleDevice(beacon.getBluetoothDevice().getAddress(),beacon.getRSSI(),beacon.getDistance().toString());
            device.setName(beacon.getName());
            deleteDevice(beacon);
            devices.add(device);
            Log.e("found",cal.getTimeInMillis()+"");
            starttime=cal.getTimeInMillis();
            updateBeaconList();
        }

        @Override
        public void onLost(Beacon beacon) {
            device=new BleDevice(beacon.getBluetoothDevice().getAddress(),beacon.getRSSI(),beacon.getDistance().toString());
            deleteDevice(beacon);
            devices.add(device);
            if(starttime>0) {
                timeused = cal.getTimeInMillis() - starttime;
                savetime();
                starttime = -1;
            }
            updateBeaconList();
            }

        @Override
        public void onChange(Beacon beacon, Distance distance) {

        }

        @Override
        public void onScanningFail(int i) {

        }


        public void deleteDevice(Beacon beacon)
        {
            String macId=beacon.getBluetoothDevice().getAddress();
            for(int i=0;i<devices.size();i++)
            {
                if(macId.equals(devices.get(i).getMacId()))
                {
                    devices.remove(i);
                    return ;
                }
            }
        }
    }


    public void savetime() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("projects");
        myRef.child(projectkey).child("timespend").setValue(timeused+project.getTime());

    }

    public void openProjectEditor(View view) {
     editMyProjectDialog.show();
    }

    public void saveEditedProject(View view) {
        String title=editName.getText().toString();
        String about=editAbout.getText().toString();

        if(!title.equals(""))
        {
            project.setName(title);
        }
        if(!about.equals(""))
        {
            project.setAbout(about);
        }
        if(imagefile!=null)
        {                uploadFile(imagefile,projectkey);
        }
        else
        {
            saveProjectData();
        }





    }

    public void uploadFile(File f,final String filename) {
        loadingDialog.show();
        message.setText("Uploading Data");

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(f);
        StorageReference riversRef = storageRef.child("images/"+projectkey+".jpg");

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        project.setImageurl(downloadUrl.toString());
                        Toast.makeText(getBaseContext(),"Successfully uploaded data",Toast.LENGTH_LONG).show();
                        message.setText("Successfully uploaded image");
                        saveProjectData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        message.setText("Failed To upload image");
                        Toast.makeText(getBaseContext(),"Upload Failed",Toast.LENGTH_LONG).show();


                        // ...
                    }
                });

    }

    public void saveProjectData() {
        message.setText("uploading content");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("projects");
        myRef.child(projectkey).setValue(project);
        editMyProjectDialog.dismiss();
        isValueSet=false;
        setData(project);
        message.setText("upload Successfule");
        loadingDialog.dismiss();
    }

    public void addEditPicSelector(View view) {
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this,"arrived"+resultCode+"  "+requestCode+" "+RESULT_OK,Toast.LENGTH_LONG).show();
        if (resultCode == RESULT_OK) {

            if (requestCode == PICK_IMAGE_REQUEST) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                Toast.makeText(this,"recieved "+data.getDataString(),Toast.LENGTH_LONG).show();
                Log.e("image menu",data.getDataString());
                Log.e("after that",selectedImageUri.toString());
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedImageUri);
                    Log.e("image", "Image Path : " + path);
                    // Set the image in ImageView
                    File imgfile=new File(path);
                    //this is for testing purpose
                    Bitmap img= BitmapFactory.decodeFile(imgfile.getAbsolutePath());
                    if(img!=null) {
                        editImagePic.setImageBitmap(img);
                        imagefile=imgfile;
                    }

                }
            }
        }

    }

    public String getPathFromURI(Uri uri) {
        String res = null;

        String[] projection = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();


        int columnIndex = cursor.getColumnIndex(projection[0]);
        String picturePath = cursor.getString(columnIndex); // returns null
        cursor.close();

        return picturePath;
    }

}
