package edu.uci.collabevent;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Prateek on 06/06/16.
 */

class SignOutTask extends AsyncTask<Void, Void, String> {

    private Context context;

    SignOutTask(Context ctx) {
        this.context = ctx;
    }

    private String postSignOut() throws IOException {
        URL url = new URL(context.getString(R.string.server_ip) + context.getString(R.string.signout_url));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);

        int responseCode = connection.getResponseCode();
        Log.d("DEBUG", "\nSending 'GET' request to URL : " + url);
        Log.d("DEBUG", "Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        Log.d("DEBUG", response.toString());
        return response.toString();
    }

    @Override
    protected String doInBackground(Void... params) {
        String response = new String();
        try {
            response = postSignOut();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO: register the new account here.
        return response;

    }

    @Override
    protected void onPostExecute(String s) {
        if (!s.isEmpty() && s.equals("Success")) {
            Intent I = new Intent(context, SplashActivity.class);
            context.startActivity(I);
        } else {
            CharSequence text = "Something went wrong!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}