package com.saskpolytech.clinicapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "";
    EditText etHealthCard;
    EditText etPassword;
    TextView tvTimer;

    String sPassword = "";
    public static TextView data;

    int attempts = 0;
    boolean isPasswordValid;
    boolean isHealthcardnoValid;
    boolean aptFound;



    String LoginLoginUrl = "http://10.0.2.2:7000/login";

    String SeachAptUrl = "http://10.0.2.2:7000/getAppointmentCount";




    private SharedPreferences prefs;
    ImageButton bLogin;
    JSONObject obj = new JSONObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String ip = Network.readIP(getBaseContext());
        //String ip = "10.0.2.2";
        LoginLoginUrl = "http://" + ip + ":7000/login";
        SeachAptUrl = "http://" + ip + ":7000/getAppointmentCount";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etHealthCard = findViewById(R.id.etHealthCardNo);
        etPassword = findViewById(R.id.etPassword);
        tvTimer = findViewById(R.id.tvTimer);
        bLogin = findViewById(R.id.imgBtnLogin);
        prefs = getSharedPreferences("file", Context.MODE_PRIVATE);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams Countparams = new RequestParams();
                Countparams.put("HealthCard",etHealthCard.getText());

                /*client.post(SeachAptUrl, Countparams, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        aptFound = true;
                        tvTimer.setText("");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        aptFound = false;
                        tvTimer.setText("No appointments found.");
                    }
                });*/
                if(attempts>=4)
                {
                    tvTimer.append("Please wait before trying again.");
                }
                else
                {
                    if(SetValidation()/* && aptFound*/)
                    {
                        RequestParams params = new RequestParams();
                        params.put("username", etHealthCard.getText());
                        params.put("password", etPassword.getText());
                        client.post(LoginLoginUrl, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                tvTimer.setText("");
                                String resp = new String(responseBody);
                                resp = resp.replaceAll("^\"|\"$", "");
                                Intent i = new Intent(getApplicationContext(), HomeScreenActivity.class);
                                i.putExtra("ResponseBody", resp);
                                i.putExtra("healthcard", etHealthCard.getText().toString());
                                etHealthCard.setText("");
                                etPassword.setText("");
                                startActivity(i);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(getApplicationContext(),"Login Failed.",Toast.LENGTH_LONG).show();
                                attempts++;
                                startLockout();
                            }
                        });
                    }
                    else
                    {
                        attempts++;
                        startLockout();
                    }
                }
            }
        });


    }


    public void startLockout()
    {
        if(attempts>=4)
        {
            final CountDownTimer waitTimer;
            waitTimer = new CountDownTimer(60000, 300) {

                public void onTick(long millisUntilFinished) {
                    //called every 300 milliseconds, which could be used to
                    //send messages or some other action
                    tvTimer.setVisibility(View.VISIBLE);
                    tvTimer.setText("User can login after: "+String.valueOf(millisUntilFinished/1000)+"s");
                    bLogin.setEnabled(false);

                }

                public void onFinish() {
                    attempts=0;
                    bLogin.setEnabled(true);
                    tvTimer.setVisibility(View.GONE);
                }
            }.start();
        }
    }
    public boolean SetValidation() {

        boolean validPass = false;
        if (etHealthCard.getText().toString().isEmpty()) {
            etHealthCard.setError(getResources().getString(R.string.etHealthCardNo_error));
            isHealthcardnoValid = false;
        } else if (!Patterns.PHONE.matcher(etHealthCard.getText().toString()).matches()) {
            etHealthCard.setError(getResources().getString(R.string.error_invalid_etHealthCardNo));
            isHealthcardnoValid = false;
        } else {
            isHealthcardnoValid = true;
        }

        // Check for a valid password.
        if (etPassword.getText().toString().isEmpty()) {
            etPassword.setError(getResources().getString(R.string.spassword_error));
            isPasswordValid = false;
        } else if (etPassword.getText().length() < 6) {
            etPassword.setError(getResources().getString(R.string.error_invalid_etpassword));
            isPasswordValid = false;
        } else {
            isPasswordValid = true;
        }

        if (isHealthcardnoValid && isPasswordValid) {
            validPass = true;
        }
        return validPass;
    }

}