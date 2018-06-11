package com.example.rahulstudy.blubeekon_admin;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.rahulstudy.blubeekon_admin.Data.ProjectData;
import com.example.rahulstudy.blubeekon_admin.Data.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    public static UserData myuser;
    public static String userkey;
    private Button login,signin;
    private EditText username,password;
    private TextView error;
    private Dialog loadingDialog;
    ValueEventListener userValue;



    Bundle instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref=getSharedPreferences("blubeekonadmin",this.MODE_PRIVATE);
        savedvalue=pref.edit();

        Intent i=new Intent(HomeActivity.this,project.class);
        Intent myprojectpage=new Intent(HomeActivity.this,AdminPageActivity.class);
        if(pref.getBoolean("login",false))
        {
            UserData user=new UserData(pref.getString("username",""),pref.getString("password",""));
            user.setProjectkey(pref.getString("projectkey",""));
            myuser=user;

            if(pref.getBoolean("projectState",false))
            {startActivity(myprojectpage);}
            else {
                startActivity(i);
            }
        }
        setContentView(R.layout.activity_home);
        initialize();

    }

    SharedPreferences.Editor savedvalue;
    SharedPreferences pref;

    public void uiAnimation()
    {   LinearLayout linearLayout = (LinearLayout) findViewById(R.id.loginlayout);

        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();

        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(2500);

        animationDrawable.start();
    }
    public void initialize()
    {
        uiAnimation();
        login=(Button)findViewById(R.id.login);
        signin=(Button)findViewById(R.id.signup);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        error=(TextView)findViewById(R.id.error);
        username.setText(pref.getString("username",""));
        password.setText(pref.getString("password",""));
        loadingDialog=new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);

    }

    public Boolean flag;
    public void newUser(View v)
    {
        flag=true;
       final String name=username.getText().toString();
       final String pass=password.getText().toString();
        loadingDialog.show();
        userValue=null;
        userValue=FirebaseDatabase.getInstance().getReference().child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserData user = snapshot.getValue(UserData.class);
                            if(user.getEmail().equals(name))
                            {
                               loadingDialog.dismiss();
                               flag=false;
                               return;
                                }
                            FirebaseDatabase.getInstance().getReference().child("users").removeEventListener(userValue);
                            }

                        }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });



        if(flag) {
            UserData user = new UserData(name, pass);
            loadingDialog.show();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users");
            ProjectData myproject=new ProjectData();

            DatabaseReference project = database.getReference("projects");
            String projectkey = project.push().getKey();
            project.child(projectkey).setValue(myproject);
            user.setProjectkey(projectkey);
            String mykey = myRef.push().getKey();
            myRef.child(mykey).setValue(user);
            error.setText("New Account Created");
            loadingDialog.dismiss();
        }
        else {error.setText("Email Already Exists"); }
    }



    public void loginUser(View v)
    {
        final Intent i = new Intent(HomeActivity.this, project.class);
        final Intent mainpage=new Intent(HomeActivity.this,AdminPageActivity.class);
        final String username=this.username.getText().toString();
        final String password=this.password.getText().toString();
        loadingDialog.show();
        if(pref.getString("username","nonwenw").equals(username) && pref.getString("password","dafaf12sda").equals(password)) {
            UserData user=new UserData(pref.getString("username",""),pref.getString("password",""));
            user.setProjectkey(pref.getString("projectkey",""));
            myuser=user;
            if(pref.getBoolean("projectState",false))
                startActivity(mainpage);
            else{loadingDialog.dismiss();
                startActivity(i);}
        }




            flag=true;
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserData user = snapshot.getValue(UserData.class);
                            Log.e("user id",snapshot.getKey());
                            Log.e("user data",user.getEmail());
                            if(user.getEmail().equals(username))
                            {
                                flag=false;
                                Log.e("user pswd",user.getPassword());
                                if(user.getPassword().equals(password)) {
                                    userkey = snapshot.getKey();
                                    savedvalue.putString("username",user.getEmail());
                                    savedvalue.putString("projectkey",user.getProjectkey());
                                    savedvalue.putString("password",user.getPassword());
                                    savedvalue.putString("userkey",snapshot.getKey());
                                    savedvalue.putBoolean("login",true);
                                    savedvalue.putBoolean("projectState",user.getProjectState());
                                    savedvalue.apply();
                                    myuser = user;
                                    Log.e("pswd test","passwrod valid");

                                    if(pref.getBoolean("projectState",false))
                                        startActivity(mainpage);
                                    else {
                                        loadingDialog.dismiss();
                                        startActivity(i);
                                    }

                                }
                                else
                                {
                                    error.setText("Invalid password");

                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        if(!flag)
          error.setText("Invalid username");
    }


}
