package edu.uci.collabevent;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class EditEventActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog = null;

    private EditActivityTask mEdit = null;

    private EditText mEventName;
    private EditText mVenue;
    private EditText mDescription;
    private EditText mDate;
    private EditText mTime;
    private ImageView imageView;
    private Button bImage;
    private int PICK_IMAGE_REQUEST = 1;
    private Button bCreate;
    private String eventName, eventVenue, eventTime, eventDate, eventDesc, eventImage;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Context context;
    private Event event;
    private Bitmap image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Intent intentExtras = getIntent();
        byte[] imageBytes = intentExtras.getByteArrayExtra("image");
        image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        Bundle extrasBundle = intentExtras.getExtras();
        if (!extrasBundle.isEmpty()) {
            event = extrasBundle.getParcelable("event");

        } else {
            Log.d("DEBUG_EV_DETAIL", "Event Detail Activity initialised without id.");
            Log.d("DEBUG_EV_DETAIL", "Going to Splash screen.");
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(intent);
        }



        mEventName = (EditText) findViewById(R.id.event_name);
        mEventName.setText(event.getName());

        mVenue = (EditText) findViewById(R.id.event_venue);
        mVenue.setText(event.getVenue());
        mDescription = (EditText) findViewById(R.id.event_desc);
        mDescription.setText(event.getDescription());
        mDate = (EditText) findViewById(R.id.in_date);
        mDate.setText(Event.onlyDateFormat.format(event.getDate()));
        mTime = (EditText) findViewById(R.id.in_time);
        mTime.setText(Event.onlyTimeFormat.format(event.getDate()));
        imageView = (ImageView) findViewById(R.id.event_img);
        if (image != null) {
            imageView.setImageBitmap(image);
        }

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
                attemptEdit();
            }
        });

    }

    public void attemptEdit() {
        if (mEdit != null) {
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
            mEdit = new EditActivityTask();
            mEdit.execute((Void) null);

        }

    }

    public String postEditEventData() throws IOException, JSONException {
        URL url = new URL(context.getString(R.string.server_ip) + context.getString(R.string.edit_event_url));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        JSONObject eventData = new JSONObject();
        eventData.put("event_id", event.getEventId());
        eventData.put("event_name", eventName);
        eventData.put("event_venue", eventVenue);
        eventData.put("event_time", eventDate + " " + eventTime + ":00");
        eventData.put("event_desc", eventDesc);
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
                            StringBuilder dateText = new StringBuilder();

                            dateText.append(Integer.toString(year) + "-");

                            if (monthOfYear < 9) {
                                dateText.append("0");
                            }
                            dateText.append(Integer.toString(monthOfYear + 1) + "-");

                            if (dayOfMonth < 10) {
                                dateText.append("0");
                            }

                            dateText.append(Integer.toString(dayOfMonth));
                            mDate.setText(dateText.toString());

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

                            StringBuilder timeText = new StringBuilder();

                            if (hourOfDay < 10) {
                                timeText.append("0");
                            }
                            timeText.append(hourOfDay + ":");

                            if (minute < 10) {
                                timeText.append("0");
                            }
                            timeText.append(minute);

                            mTime.setText(timeText.toString());
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
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 300, true);

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

    protected class EditActivityTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            String response = new String();
            try {
                response = postEditEventData();
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
            mEdit = null;
            progressDialog.dismiss();
            String successString = "Success";
            if (!response.isEmpty() && response.startsWith(successString)) {
//                eventName, eventVenue, eventTime, eventDate, eventDesc, eventImage;
                String jsonResponse = response.substring(successString.length());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonResponse);
                    Event e = Event.createEvenFromJSON(jsonObject);
                    EventDetailActivity.event = e;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
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
