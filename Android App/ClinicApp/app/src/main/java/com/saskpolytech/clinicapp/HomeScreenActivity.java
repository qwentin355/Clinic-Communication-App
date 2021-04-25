package com.saskpolytech.clinicapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class HomeScreenActivity extends AppCompatActivity {
    private String Sendurl;
    private String LogOuturl;
    private AppBarConfiguration mAppBarConfiguration;

    private String POST_PARAMS;
    String hc;
    String resp;
    public String GetAptURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String ip = Network.readIP(getBaseContext());
        //String ip = "10.0.2.2";
        Sendurl = "http://" + ip + ":7000/getMessages";
        GetAptURL="http://" + ip + ":7000/search";
        LogOuturl = "http://" + ip + ":7000/logout";
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle extras = getIntent().getExtras();
/*
        String resp = extras.getString("ResponseBody");
        final String Response = extras.getString("Response Body");
        Toast.makeText(this,"Response Value: "+resp,Toast.LENGTH_SHORT).show();
*/

        //TextView doctorName = (TextView)findViewById(R.id.tvDoctorName);
        //TextView appointment = (TextView)findViewById(R.id.tvAppointment);
        resp = extras.getString("ResponseBody");
        hc = extras.getString("healthcard");
        final String Response = extras.getString("Response Body");
        //Toast.makeText(this,"Response Value: "+hc,Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,"Response Value: "+resp,Toast.LENGTH_SHORT).show();



        AsyncHttpClient client = new AsyncHttpClient();


        RequestParams params = new RequestParams();
        params.put("username", hc.toString());
        params.put("sessionID",resp.toString());
        params.put("HealthCard", hc.toString());
        client.post(GetAptURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                Toast.makeText(getApplicationContext(),""+str,Toast.LENGTH_LONG).show();
               // Toast.makeText(getApplicationContext(),"Response:  "+resp,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
           /*     Toast.makeText(getApplicationContext(),"Fail"+hc,Toast.LENGTH_LONG).show();

                Toast.makeText(getApplicationContext(),"Fail"+ip,Toast.LENGTH_LONG).show();
            Log.e("IP",ip);
            Log.e("SessionID",resp);*/
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, Response, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent i = new Intent(getApplicationContext(),ChatScreen.class);
                i.putExtra("ResponseBody", resp);
                i.putExtra("healthcard", hc);
                startActivity(i);


            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_Logout)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //ads carousel view
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, new CarouselAdsFragment());
        ft.commit();


        navigationView.getMenu().findItem(R.id.nav_Logout).setOnMenuItemClickListener(menuItem -> {             //Logs out of the server
            System.out.print("Logout button");
            logout_server();
            return true;
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    private void logout(){
        this.finish();
    }

    private void logout_server()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(LogOuturl);                                                                 //Create URL
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();                          //Create a Connector
                    conn.setRequestMethod("POST");                                                              //Dictate the Method
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8"); //Some other properties
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());                         //Prepare the connection for output

                    POST_PARAMS = toBodyType("admin", resp, hc);     //Converts post parameters to body type

                    System.out.println(POST_PARAMS);
                    os.writeBytes(POST_PARAMS);                                                           //Write our parameters to the post


                    os.flush();                                                                                 //Do the thing
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));                               //Debug
                    Log.i("MSG", conn.getResponseMessage());                                               //Debug

                    int responseCode = 0;                                                                       //get response code
                    responseCode = conn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {                                            //On success logout of app
                        logout();
                    }
                    conn.disconnect();                                                                          //disconnect after attempting post
                    //update our messages
                } catch (Exception e) {                                                                         //dirty catch all
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private String toBodyType(String uName, String sesID, String cardNum)
    {
        return "username=" + uName + "&sessionID=" + sesID + "&cardNum=" + cardNum;
    }
}