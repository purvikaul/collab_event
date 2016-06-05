package edu.uci.collabevent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateEvent extends AppCompatActivity implements View.OnClickListener {

    private EditText mEventName;
    private EditText mVenue;
    private EditText mDescription;
    private EditText mDate;
    private EditText mTime;
    private ImageView imageView;
    private Button bImage;
    private int PICK_IMAGE_REQUEST = 1;
    private List<CharSequence> userList;
    private View openDialog;
    private int mYear, mMonth, mDay, mHour, mMinute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        userList = new ArrayList<CharSequence>();

        mEventName = (EditText) findViewById(R.id.event_name);
        mVenue = (EditText) findViewById(R.id.event_venue);
        mDescription = (EditText) findViewById(R.id.event_desc);
        mDate = (EditText) findViewById(R.id.in_date);
        mTime = (EditText) findViewById(R.id.in_time);
        imageView = (ImageView) findViewById(R.id.event_img);
        openDialog = (View) findViewById(R.id.openDialog);

        bImage = (Button) findViewById(R.id.img_upload);

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
        inviteList();
    }

    public void inviteList() {
        Log.d("DEBUG_CLICK", "In inviteList()");
        for (int i = 0; i < 20; i++) {

            userList.add("test " + i);  // Add the item in the userList
        }

        openDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d("DEBUG_CLICK", "In inviteList()");
                // Intialize  readable sequence of char values
                final CharSequence[] dialogList = userList.toArray(new CharSequence[userList.size()]);
                final AlertDialog.Builder builderDialog = new AlertDialog.Builder(CreateEvent.this);
                builderDialog.setTitle("Select Item");
                int count = dialogList.length;
                boolean[] is_checked = new boolean[count]; // set is_checked boolean false;

                // Creating multiple selection by using setMutliChoiceItem method
                builderDialog.setMultiChoiceItems(dialogList, is_checked,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton, boolean isChecked) {
                            }
                        });

                builderDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ListView list = ((AlertDialog) dialog).getListView();
                                // make selected item in the comma seprated string
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < list.getCount(); i++) {
                                    boolean checked = list.isItemChecked(i);

                                    if (checked) {
                                        if (stringBuilder.length() > 0) stringBuilder.append(",");
                                        stringBuilder.append(list.getItemAtPosition(i));

                                    }
                                }

                        /*Check string builder is empty or not. If string builder is not empty.
                          It will display on the screen.
                         */
                                if (stringBuilder.toString().trim().equals("")) {

                                    ((TextView) findViewById(R.id.text)).setText("Click here to open Dialog");
                                    stringBuilder.setLength(0);

                                } else {

                                    ((TextView) findViewById(R.id.text)).setText(stringBuilder);
                                }
                            }
                        });

                builderDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((TextView) findViewById(R.id.text)).setText("Click here to open Dialog");
                            }
                        });
                AlertDialog alert = builderDialog.create();
                alert.show();
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

                            mDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

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

                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
