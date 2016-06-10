package edu.uci.collabevent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateTaskActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog = null;

    private CreateActivityTask mCreate = null;

    private EditText mTaskTitle;
    private EditText mDescription;
    private EditText mDate;
    private EditText mTime;
    private int PICK_IMAGE_REQUEST = 1;
    private List<String> userList;
    private List<String> emailList;
    private EditText openDialog;
    private int isSelected;
    private Button bCreate;
    private AlertDialog alert;
    private String taskTitle, taskTime, taskDueDate, taskDesc;
    private int eventId;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Context context;
    private Event event;
    private String assignedTo;
    StringBuilder assignedToString;
    CharSequence[] dialogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        userList = new ArrayList<>();
        emailList = new ArrayList<>();
        assignedTo = new String();
        event = EventDetailActivity.event;
        isSelected = -1;

        mTaskTitle = (EditText) findViewById(R.id.event_name);
        mDescription = (EditText) findViewById(R.id.event_desc);
        mDate = (EditText) findViewById(R.id.in_date);
        mTime = (EditText) findViewById(R.id.in_time);
        openDialog = (EditText) findViewById(R.id.openDialog);
        eventId = event.getEventId();
        assignedToString = new StringBuilder();
        context = getApplicationContext();

        bCreate = (Button) findViewById(R.id.btn_create);

        mDate.setOnClickListener(this);
        mTime.setOnClickListener(this);

        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptCreate();
            }
        });

        MemberListTask memberListTask = new MemberListTask();
        memberListTask.execute();

        openDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createMemberListDialog();
                alert.show();
            }
        });
    }

    public void attemptCreate() {
        if (mCreate != null) {
            return;
        }

        mTaskTitle.setError(null);

        taskTitle = mTaskTitle.getText().toString();
        taskTime = mTime.getText().toString();
        taskDueDate = mDate.getText().toString();
        taskDesc = mDescription.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(taskTitle)) {
            mTaskTitle.setError(getString(R.string.error_field_required));
            focusView = mTaskTitle;
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

    public String postCreateTaskData() throws IOException, JSONException {
        URL url = new URL(context.getString(R.string.server_ip) + context.getString(R.string.create_task_url));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        JSONObject eventData = new JSONObject();
        eventData.put("event_id", eventId);
        eventData.put("task_title", taskTitle);
        eventData.put("task_time", taskDueDate + " " + taskTime + ":00");
        eventData.put("task_desc", taskDesc);
        eventData.put("assigned_to", assignedTo);
        if (isSelected == -1) {
            eventData.put("task_status", "UA");
        } else {
            eventData.put("task_status", "A");
        }

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

    public void createMemberListDialog() {

        dialogList = userList.toArray(new CharSequence[userList.size()]);

        int isSelectedLocal = isSelected;
        // Intialize  readable sequence of char values

        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(this);
        builderDialog.setTitle("Select Member");

        // Creating single selection by using setSingleChoiceItem method
        builderDialog.setSingleChoiceItems(dialogList, isSelectedLocal, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert = builderDialog.create();
        alert.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("DEBUG_CREATE", "OK Clicked!");
                assignedToString = new StringBuilder();
                assignedTo = new String();
                ListView list = ((AlertDialog) dialog).getListView();
                // make selected item in the comma seperated string
                isSelected = list.getCheckedItemPosition();
                Log.d("CREATE_TASK", "Assigned user id " + isSelected);

                assignedToString.append(list.getItemAtPosition(isSelected));

                assignedTo = emailList.get(isSelected);

                if (assignedToString.toString().trim().equals("")) {

                    ((TextView) findViewById(R.id.openDialog)).setText("Click here to open Dialog");
                    assignedToString.setLength(0);

                } else {

                    ((TextView) findViewById(R.id.openDialog)).setText(assignedToString);
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
                    list.setSelection(isSelected);
                }

                Log.d("DEBUG_CREATE_TASK", "Cancel Clicked!");
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

    protected class MemberListTask extends AsyncTask<Void, Void, String> {
        Context mContext;

        MemberListTask() {
            this.mContext = getApplicationContext();
        }

        @Override
        protected String doInBackground(Void... voids) {
            StringBuffer response = new StringBuffer("");
            try {
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("event_id", Integer.toString(eventId));
                String query = builder.build().getEncodedQuery();
                URL eventsListURL = new URL(mContext.getString(R.string.server_ip) + mContext.getString(R.string.members_list) + "?" + query);
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
            createMembersFromJSON(s);
        }

        public void createMembersFromJSON(String friendList) {
            Log.d("DEBUG_LIST", "In createMembersFromJSON()");

            try {
                JSONArray reader = new JSONArray(friendList);
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
                response = postCreateTaskData();
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
