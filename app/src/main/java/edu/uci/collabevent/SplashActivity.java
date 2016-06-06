package edu.uci.collabevent;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    public static CookieManager cmrCookieMan;
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        context = getApplicationContext();
        new PrefetchData().execute();
    }

    private class PrefetchData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            StringBuffer response = new StringBuffer();
            cmrCookieMan = new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cmrCookieMan);
            Log.d("DEBUG", "In doInBackground");
            URL url = null;
            try {
                List<HttpCookie> cookies = cmrCookieMan.getCookieStore().get(new URI(context.getString(R.string.server_ip)));
                if (cookies.size() > 0) {
                    try {
                        url = new URL(context.getString(R.string.server_ip) + context.getString(R.string.test_cookie_url));
                        HttpURLConnection connection = null;
                        try {
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setReadTimeout(5000);
                            connection.setConnectTimeout(3000);
                            connection.setRequestMethod("GET");
                            connection.setDoInput(true);
                            connection.setDoOutput(true);
                            int responseCode = connection.getResponseCode();
                            Log.d("DEBUG", "\nSending 'GET' request to URL : " + url);
                            Log.d("DEBUG", "Response Code : " + responseCode);
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(connection.getInputStream()));
                            String inputLine;
                            response = new StringBuffer();
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();

                            //print result
                            Log.d("DEBUG", response.toString());

                            return response.toString();

                        } catch (IOException e) {
                            Log.d("DEBUG", "In catch11 : doInBackground");
                            e.printStackTrace();
                        }
                    } catch (MalformedURLException e) {
                        Log.d("DEBUG", "In catch2 : doInBackground");
                        e.printStackTrace();
                    }

                }
            } catch (URISyntaxException e) {
                Log.d("DEBUG", "In catch3 : doInBackground");
                e.printStackTrace();
            }

            Log.d("DEBUG", "Returning in doInBackground");
            return response.toString();
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d("DEBUG", "In postExecute");
            Log.d("DEBUG", "In postExecute, Response: " + response);
            if (!response.isEmpty() && response.equals("True")) {
                Intent I = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(I);
            } else {
                Intent I = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(I);
            }
            finish();
        }
    }
}


