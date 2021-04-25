package com.saskpolytech.clinicapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import cz.msebera.android.httpclient.Header;

public class CarouselAdsFragment extends Fragment {

    View view;
    CarouselView carouselView;
    byte[] data;   //store response from backend
    JSONArray jsonArray;    //convert response to json array
    Bitmap[] bmArray;   //store images


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        String ip = Network.readIP(getActivity().getBaseContext());

        //String ip = "10.0.2.2";

        //make request to backend to get images
        String getImagesUrl = "http://" + ip + ":7000/retrieveImage";
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getImagesUrl, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                data = Arrays.copyOf(responseBody, responseBody.length);
                String str = new String(data, StandardCharsets.UTF_8);
                //Toast.makeText(getActivity().getApplicationContext(), data.length, Toast.LENGTH_LONG).show();
                //delete backslashes ( \ ) :
                str = str.replaceAll("[\\\\]{1}[\"]{1}","\"");
                //delete first and last double quotation ( " ) :
                str = str.substring(str.indexOf("["),str.lastIndexOf("]")+1);
                try {
                    jsonArray = new JSONArray(str);
                    populateBitmapArray();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_carousel_ads, container, false);
        carouselView = view.findViewById(R.id.carouselView);
        carouselView.setImageListener(imageListener);
        return view;
    }

    public void populateBitmapArray() {
        bmArray = new Bitmap[jsonArray.length()];
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i=0;i<len;i++){
                //convert base64 string to byte[]
                byte[] data = new byte[0];
                try {
                    data = Base64.getDecoder().decode(jsonArray.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //convert byte[] to a bitmap
                Bitmap decodedByte = BitmapFactory.decodeByteArray(data, 0, data.length);
                bmArray[i] = decodedByte;
            }
        }
        carouselView.setPageCount(bmArray.length);
    }


    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageBitmap(bmArray[position]);
        }
    };
}