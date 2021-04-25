package com.saskpolytech.clinicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import cz.msebera.android.httpclient.entity.StringEntity;

/*
Elemnents on the Chat Screen
*  Constraintlayout
    -toolbar1 -toolbar at top needs to navigate to home screen make this work again
    -reyclerview_message_list - where we display the messages - define a fragment or something with which to display the the messages in here
    - View -  - separator -

 * layout_chatbox
    - edittext_chatbox - user input - just need to be itself
    - button_chatbox_send - to send the user input - this needs to call a function that posts a message to the backend

    Other things
    - function for polling the backend for new message (it will every 10 seconds or so send a post request to the the backend and update our displayed messages) -DONE
    - function for requesting messages -DONE
    - function for sending messages -DONE
    - functions for message validation -DONE
    - function for displaying messages -DONE
    - Somehow get the healthcard from the login screen fragment. -PENDING TEAM B
    - if time allows Censor -PROBABLY NOT IN THE SCOPE
 */

public class ChatScreen extends AppCompatActivity {
    private static final String USER_AGENT = "Mozilla/5.0";
    private String POST_PARAMS = "username=admin&sessionID=1&cardNum=111111111&sentByStaff=0&message=This is a test&time=1615410749";
    String Geturl;
    String Sendurl;
    private String HCard = "";
    private String ID = "";
    private MessageAdapter messageAdapter;
    private EditText mEdit;
    private Button mButton;
    private List<AuthMsg> messages = new ArrayList<AuthMsg>();
    private ListView messagesView;
    private Timer getTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String ip = Network.readIP(getBaseContext());
        Geturl = "http://" + ip + ":7000/getMessages";
        Sendurl = "http://" + ip + ":7000/newMessage";
        getWindow().setBackgroundDrawableResource(R.drawable.app_bg_png6);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);          //General layout
        Toolbar toolbar = findViewById(R.id.toolbar1);          //Toolbar on top
        setSupportActionBar(toolbar);                           //Sets action for toolbar
        toolbar.setTitle("Chat");                               //Should set the toolbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Sets the orientation??
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();                            //Gets the Healthcard passed from the HomeScreen Activity
        HCard = intent.getStringExtra("healthcard");
        ID = intent.getStringExtra("ResponseBody");

       // Toast.makeText(this,"Response Value: " +HCard + " : " + ID,Toast.LENGTH_SHORT).show();
        //System.out.println(HCard + " : " + ID);
        messageAdapter = new MessageAdapter(this);      //creates a message adapter and plugs it into our messegesVeiw
        messagesView = (ListView) findViewById(R.id.message_list);
        messagesView.setAdapter((messageAdapter));

        mButton = (Button)findViewById(R.id.button_chatbox_send);
        mButton.setOnClickListener((v) -> {sendMessage();});     //Adds click listener to send button

        mEdit = (EditText)findViewById(R.id.edittext_chatbox);  //Used to define what message will be sent

        getTimer = new Timer();                                 //Creates a timer to getMessages every 10 secconds
        getTimer.scheduleAtFixedRate(new TimerTask() {       //Every x seconds makes a updates messages
            @Override
            public void run() {
                getMessage();
            }
        }, 0, 10000);//10000 milliseconds=10 second
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        getTimer.cancel();
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    //This function will make a post request to the backend sending the edittext_chatbox value if it is deemed valid by the validate_Message function
    //if its invalid I will use toast to convey the error
    //if valid the function will call the get_messages function to update the displayed messages
    //resource https://www.journaldev.com/7148/java-httpurlconnection-example-java-http-request-get-post
    public void sendMessage() {
        if(!mEdit.getText().toString().trim().matches("")) {                                                  //Cant send empty strings
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(Sendurl);                                                                 //Create URL
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();                          //Create a Connector
                        conn.setRequestMethod("POST");                                                              //Dictate the Method
                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");                  //Some other properties
                        conn.setRequestProperty("Accept", "application/json");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);

                        DataOutputStream os = new DataOutputStream(conn.getOutputStream());                         //Prepare the connection for output

                        POST_PARAMS = toBodyType(HCard, ID, HCard, mEdit.getText().toString().trim());     //Converts post parameters to body type

                        System.out.println(POST_PARAMS);
                        os.writeBytes(POST_PARAMS);                                                           //Write our parameters to the post


                        os.flush();                                                                                 //Do the thing
                        os.close();

                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));                               //Debug
                        Log.i("MSG", conn.getResponseMessage());                                               //Debug

                        int responseCode = 0;                                                                       //get response code
                        responseCode = conn.getResponseCode();

                        if (responseCode == HttpURLConnection.HTTP_OK) {                                            //On success
                                mEdit.setText("");
                        }
                        conn.disconnect();                                                                          //disconnect after attempting post
                        getMessage();                                                                               //update our messages
                    } catch (Exception e) {                                                                         //dirty catch all
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    //This function Makes a HTTPURL Post request then converts and stores the response to a string.
    //It then passes the response to the JSONify function which attempt to convert the string to java objects as defines in
    //the AutMsg class.
    //resource https://www.journaldev.com/7148/java-httpurlconnection-example-java-http-request-get-post
    //resource https://stackoverflow.com/questions/42767249/android-post-request-with-json
    public void getMessage() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(Geturl);                                                                //Create URL
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();                        //Create a Connector
                    conn.setRequestMethod("POST");                                                            //Dictate the Method
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");                //Some other properties
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    //This part causes a 401 error because it is the equivalent of passing parameters but our backend uses body formatting
                    //JSONObject jsonParam = new JSONObject();
                    //jsonParam.put("username", "admin");
                    //jsonParam.put("sessionID", "1");
                    //Log.i("JSON", jsonParam.toString());

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());                         //Prepare the connection for output
                    //os.writeBytes(jsonParam.toString());

                    POST_PARAMS = toBodyType(HCard, ID);                                //Converts post parameters to body type
                    System.out.println(POST_PARAMS);
                    os.writeBytes(POST_PARAMS);                                                           //Write our parameters to the post

                    os.flush();                                                                                 //Do the thing
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));                                //Debug
                    Log.i("MSG" , conn.getResponseMessage());                                               //Debug

                    int responseCode = 0;                                                                       //get response code
                    responseCode = conn.getResponseCode();

                    System.out.println("POST Response Code :: " + responseCode);                                //Debug:  print response code

                    if (responseCode == HttpURLConnection.HTTP_OK) {                                            //On success
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));   //Gotta figure out how to expect a JSON stream and how to parse the responce ot JSON

                        //get, cleanup and store the response from the server.
                        String inputLine;
                        StringBuffer response = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine.replaceAll("\\\\",""));
                        }
                        in.close();
                        String result = response.toString().substring(1,response.toString().length()-1);
                        //Convert the response into java usable objects
                        JSONify(result);                                                                        //This will call the JSONify function and store the json conversion to the global messages array.
                    } else {
                        System.out.println("POST request not worked");                                          //If post fails
                    }

                    conn.disconnect();                                                                          //disconnect after attempting post
                } catch (Exception e) {                                                                         //dirty catch all
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    private String toBodyType(String uName, String sesID, String cardNum, String Message)
    {
        Date date = new Date();
        String session = sesID.replaceAll("\"", "");
            return "username=" + uName + "&sessionID=" + session + "&cardNum=" + uName + "&sentByStaff=false&message=" + Message + "&time=" + date.getTime()/1000 ;
    }

    private String toBodyType(String uName, String sesID)
    {
        String session = sesID.replaceAll("\"", "");
        return "username=" + uName + "&sessionID=" + session + "&cardNum=" + uName ;
    }


    //This function will take in a string formatted as JSON and then convert the JSON objects to
    // Java Objects the results will be stored in the global messages array.
    //resource https://howtodoinjava.com/gson/gson-parse-json-array/
    private void JSONify(String data) throws JSONException {

        List<AuthMsg> LocalMessages = new ArrayList<AuthMsg>();
        //Uses Gson to create a List of AuthMsg objects
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listOfMyClassObject = new TypeToken<ArrayList<AuthMsg>>() {}.getType();
        LocalMessages = gson.fromJson(data, listOfMyClassObject);

        if(LocalMessages.size()>messages.size()) {          //Only updates teh display if new information is received
            messages = new ArrayList<AuthMsg>(LocalMessages);

            //UI changes need to be made on the main thread thus this.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(messages);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
        }
    }

    //This will be a data object to store our json data in java
   private class AuthMsg{
        String cardNum;
        Boolean staff;
        String message;
        long sentTime;

        public String getMessage() { return this.message; }
        public String getDate()
        {
            Date date = new Date(sentTime*1000);
            DateFormat df = new SimpleDateFormat("MMMM d, yyy h:mm aaa");
            return df.format(date);
        }
        public Boolean isStaff() { return this.staff; }
        public long sentTime(){return this.sentTime;}
    }

    // MessageAdapter is the adapter that handles displaying messages on our listview
    //resource: https://guides.codepath.com/android/Using-a-BaseAdapter-with-ListView
    public class MessageAdapter extends BaseAdapter {

        List<AuthMsg> messages = new ArrayList<AuthMsg>();
        Context context;

        public MessageAdapter(Context context) {
            this.context = context;
        }

        public void add(List<AuthMsg> message) {
            this.messages = message;
            this.messages.sort((AuthMsg A, AuthMsg B)->Long.valueOf(A.sentTime).compareTo(Long.valueOf(B.sentTime)));
            System.out.println("from Add");
            for(AuthMsg msg : this.messages)
            {
                System.out.println(msg.getMessage());
                System.out.println(msg.isStaff());
            }
            notifyDataSetChanged(); // to render the list we need to notify
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int i) {
            return messages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            MessageViewHolder holder = new MessageViewHolder();
            LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            AuthMsg message = messages.get(i);

            if (!message.isStaff()) { // this message was sent by us so let's create a basic chat bubble on the right
                convertView = messageInflater.inflate(R.layout.my_message, null);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                holder.date = (TextView) convertView.findViewById(R.id.date_body);
                convertView.setTag(holder);
                holder.messageBody.setText(message.getMessage());
                holder.date.setText(message.getDate());
            } else { // this message was sent by someone else
                convertView = messageInflater.inflate(R.layout.your_message, null);
                holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
                holder.date = (TextView) convertView.findViewById(R.id.date_body);
                convertView.setTag(holder);
                holder.messageBody.setText(message.getMessage());
                holder.date.setText(message.getDate());
            }
            return convertView;
        }
    }

    class MessageViewHolder {
        public TextView date;
        public TextView messageBody;
    }
}