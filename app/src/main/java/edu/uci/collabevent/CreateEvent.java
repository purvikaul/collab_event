package edu.uci.collabevent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateEvent extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog = null;

    private CreateActivityTask mCreate = null;

    private EditText mEventName;
    private EditText mVenue;
    private EditText mDescription;
    private EditText mDate;
    private EditText mTime;
    private ImageView imageView;
    private Button bImage;
    private int PICK_IMAGE_REQUEST = 1;
    private List<String> userList;
    private List<String> emailList;
    private View openDialog;
    private boolean[] isChecked;
    private Button bCreate;
    private AlertDialog alert;
    private String eventName, eventVenue, eventTime, eventDate, eventDesc, eventImage;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Context context;
    private JSONArray invitees;
    StringBuilder inviteListString;
    CharSequence[] dialogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        userList = new ArrayList<>();
        emailList = new ArrayList<>();
        invitees = new JSONArray();

        mEventName = (EditText) findViewById(R.id.event_name);
        mVenue = (EditText) findViewById(R.id.event_venue);
        mDescription = (EditText) findViewById(R.id.event_desc);
        mDate = (EditText) findViewById(R.id.in_date);
        mTime = (EditText) findViewById(R.id.in_time);
        imageView = (ImageView) findViewById(R.id.event_img);
        openDialog = (View) findViewById(R.id.openDialog);
        inviteListString = new StringBuilder();
        context = getApplicationContext();
        eventImage = new String();

        bImage = (Button) findViewById(R.id.img_upload);
        bCreate = (Button) findViewById(R.id.btn_create);

        mDate.setOnClickListener(this);
        mTime.setOnClickListener(this);

        bImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
// Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });

        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptCreate();
            }
        });

        InviteListTask inviteListTask = new InviteListTask();
        inviteListTask.execute();

        openDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createUserListDialog();
                alert.show();
            }
        });
    }

    public void attemptCreate() {
        if (mCreate != null) {
            return;
        }

        mEventName.setError(null);
        mVenue.setError(null);

        eventName = mEventName.getText().toString();
        eventVenue = mVenue.getText().toString();
        eventTime = mTime.getText().toString();
        eventDate = mDate.getText().toString();
        eventDesc = mDescription.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(eventName)) {
            mEventName.setError(getString(R.string.error_field_required));
            focusView = mEventName;
            cancel = true;
        }
        if (TextUtils.isEmpty(eventVenue)) {
            mVenue.setError(getString(R.string.error_field_required));
            focusView = mVenue;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Creating Event....");
            progressDialog.show();
            mCreate = new CreateActivityTask();
            mCreate.execute((Void) null);

        }

    }

    public String postCreateEventData() throws IOException, JSONException {
        URL url = new URL(context.getString(R.string.server_ip) + context.getString(R.string.create_event_url));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        JSONObject eventData = new JSONObject();
        eventData.put("event_name", eventName);
        eventData.put("event_venue", eventVenue);
        eventData.put("event_time", eventDate + " " + eventTime + ":00");
        eventData.put("event_desc", eventDesc);
        eventData.put("event_invitees", invitees);
        eventData.put("event_pic", eventImage);

        OutputStream os = connection.getOutputStream();
        os.write(eventData.toString().getBytes("UTF-8"));
        os.close();

        int responseCode = connection.getResponseCode();
        Log.d("DEBUG", "\nSending 'POST' request to URL : " + url);
        Log.d("DEBUG", "Post parameters : " + eventData.toString());
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

    public void createUserListDialog() {

        dialogList = userList.toArray(new CharSequence[userList.size()]);

        final boolean[] isSelected = new boolean[isChecked.length];
        for (int i = 0; i < isChecked.length; i++) {
            isSelected[i] = Boolean.valueOf(isChecked[i]);
        }

        Log.d("DEBUG_CLICK2", "In inviteList()");
        // Intialize  readable sequence of char values

        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(CreateEvent.this);
        builderDialog.setTitle("Select Item");

        // Creating multiple selection by using setMutliChoiceItem method
        builderDialog.setMultiChoiceItems(dialogList, isSelected,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {
                    }
                });

        alert = builderDialog.create();
        alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("DEBUG_CREATE", "OK Clicked!");
                inviteListString = new StringBuilder();
                invitees = new JSONArray();
                ListView list = ((AlertDialog) dialog).getListView();
                // make selected item in the comma seperated string
                for (int i = 0; i < list.getCount(); i++) {
                    boolean checked = list.isItemChecked(i);
                    isChecked[i] = checked;
                    Log.d("LLIS", "DONE2");

                    if (checked) {
                        if (inviteListString.length() > 0) inviteListString.append(",");
                        inviteListString.append(list.getItemAtPosition(i));
                        invitees.put(emailList.get(i));
                    }
                }

                if (inviteListString.toString().trim().equals("")) {

                    ((TextView) findViewById(R.id.text)).setText("Click here to open Dialog");
                    inviteListString.setLength(0);

                } else {

                    ((TextView) findViewById(R.id.text)).setText(inviteListString);
                }
            }

        });

        alert.setButton(Dialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ListView list = ((AlertDialog) dialog).getListView();
                // make selected item in the comma seperated string
                for (int i = 0; i < list.getCount(); i++) {
                    Log.d("LLIS", "WORk");
                    list.setItemChecked(i, isChecked[i]);
                }

                Log.d("DEBUG_CREATE", "Cancel Clicked!");
            }

        });

    }

    @Override
    public void onClick(View v) {
        if (v == mDate) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            mDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == mTime) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            mTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d("TAG", String.valueOf(bitmap));

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                eventImage = Base64.encodeToString(b, Base64.DEFAULT);

                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected class InviteListTask extends AsyncTask<Void, Void, String> {
        Context mContext;

        InviteListTask() {
            this.mContext = getApplicationContext();
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuffer response = new StringBuffer("");
            try {
                URL eventsListURL = new URL(mContext.getString(R.string.server_ip) + mContext.getString(R.string.friend_list));
                HttpURLConnection connection = (HttpURLConnection) eventsListURL.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                Log.d("DEBUG", "\nSending 'GET' request to URL : " + eventsListURL);
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

            } catch (IOException e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            createFriendsFromJSON(s);
        }

        public void createFriendsFromJSON(String friendList) {
            Log.d("DEBUG_LIST", "In createFriendsFromJSON()");

            try {
                JSONArray reader = new JSONArray(friendList);
                isChecked = new boolean[friendList.length()];
                for (int i = 0; i < friendList.length(); i++) {
                    JSONObject jsonObject = reader.getJSONObject(i);
                    String friendName = jsonObject.getString("name");
                    String friendEmail = jsonObject.getString("email");
                    userList.add(friendName);
                    emailList.add(friendEmail);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    protected class CreateActivityTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            String response = new String();
            try {
                response = postCreateEventData();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // TODO: register the new account here.
            return response;
        }

        @Override
        protected void onPostExecute(final String response) {
            mCreate = null;
            progressDialog.dismiss();
            if (!response.isEmpty() && response.equals("Success")) {
                Intent I = new Intent(context, HomeActivity.class);
                startActivity(I);
            } else {
                CharSequence text = "Something went wrong!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }
}
