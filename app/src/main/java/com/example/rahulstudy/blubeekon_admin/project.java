package com.example.rahulstudy.blubeekon_admin;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rahulstudy.blubeekon_admin.Data.ProjectData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;

import static com.example.rahulstudy.blubeekon_admin.HomeActivity.myuser;

public class project extends AppCompatActivity {

    int PICK_IMAGE_REQUEST=21;
    ImageView add,projectimg;
    EditText projectname,projectdetail;
    TextView error;
    File imagefile;
    ProjectData project;
    SharedPreferences pref;
    SharedPreferences.Editor prefeditor;
    String projecturl;
    Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref=getSharedPreferences("blubeekonadmin",this.MODE_PRIVATE);
        setContentView(R.layout.activity_project);
        initialize();

    }

    public void initialize()
    {
        imagefile=null;
        projectname=(EditText)findViewById(R.id.editname);
        projectdetail=(EditText)findViewById(R.id.editcontent);
        projectimg=(ImageView)findViewById(R.id.editpic);
        prefeditor=pref.edit();
        projecturl="";
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        error=(TextView)findViewById(R.id.error);
    }

    public void selectImage(View view)
    {
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
                    Bitmap img=BitmapFactory.decodeFile(imgfile.getAbsolutePath());
                    if(img!=null) {
                        projectimg.setImageBitmap(img);
                        imagefile=imgfile;
                    }

                }
            }
        }

    }

    /* Get the real path from the URI */
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



    public void uploadFile(File f,final String filename,final String name, final String detail) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(f);
        StorageReference riversRef = storageRef.child("images/"+filename+".jpg");

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        projecturl=downloadUrl.toString();
                        Toast.makeText(getBaseContext(),"Successfully uploaded data",Toast.LENGTH_LONG).show();
                        saveProjectData(name,detail,filename);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getBaseContext(),"Upload Failed",Toast.LENGTH_LONG).show();

                        // ...
                    }
                });

    }


    public void saveProject(View view)
    {
     String name=projectname.getText().toString();
     String detail=projectdetail.getText().toString();
        Toast.makeText(this,"processing ",Toast.LENGTH_LONG).show();

        if(imagefile!=null && !name.equals("") && !detail.equals(""))
     {
         Toast.makeText(this,"enterred ",Toast.LENGTH_LONG).show();
         String filename=myuser.getProjectkey();
         uploadFile(imagefile,filename,name,detail);
     }
     else if(imagefile==null)
         {
             error.setText("Please select an image");
         }
         else
        {
            error.setText("Please fill in all the details");
        }

    }

    public void saveProjectData(String name, String detail,String filename)
    {
        loadingDialog.show();
        project=new ProjectData(name,detail);
        project.setImageurl(projecturl);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("projects");
        myRef.child(filename).setValue(project);
        Intent adminpage=new Intent(project.this,AdminPageActivity.class);
        adminpage.putExtra("title",name);
        adminpage.putExtra("content",detail);
        prefeditor.putBoolean("projectState",true);
        prefeditor.apply();
        myRef=database.getReference("users");
        myRef.child(pref.getString("userkey","nothing")).child("projectState").setValue(true);
        startActivity(adminpage);

    }



    public void logout(View view)
    {
        SharedPreferences pref=getSharedPreferences("blufibeekon",MODE_PRIVATE);
        pref.edit().putBoolean("login",false);
        Intent i=new Intent(project.this,HomeActivity.class);
        startActivity(i);
    }
}
