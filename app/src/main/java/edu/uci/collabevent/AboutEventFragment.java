package edu.uci.collabevent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class AboutEventFragment extends Fragment {

    private TextView mEventName;
    private TextView mEventVenue;
    private TextView mEventTime;
    private TextView mEventDesc;
    private TextView mEventCreator;
    private ImageView mEventImage;
    private Context mContext;
    private Event event;
    private EventDetailActivity parentActivity;

//    private OnFragmentInteractionListener mListener;

    public AboutEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AboutEventFragment.
     */
//    // TODO: Rename and change types and number of parameters
//    public static AboutEventFragment newInstance(String param1, String param2) {
//        AboutEventFragment fragment = new AboutEventFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_about__event, container, false);

        mContext = getActivity();

        mEventName = (TextView) view.findViewById(R.id.event_name);
        mEventVenue = (TextView) view.findViewById(R.id.event_venue);
        mEventTime = (TextView) view.findViewById(R.id.event_time);
        mEventDesc = (TextView) view.findViewById(R.id.event_desc);
        mEventCreator = (TextView) view.findViewById(R.id.event_creator);
        mEventImage = (ImageView) view.findViewById(R.id.event_img);
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        parentActivity = (EventDetailActivity) getActivity();
        event = parentActivity.getEvent();

        setEventDetails();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BITMAP", "Crossed1");
                Intent intent = new Intent(mContext, EditEventActivity.class);
                Log.d("BITMAP", "Crossed2");
                byte[] b = new byte[0];
                try {
                    Bitmap image = ((BitmapDrawable) mEventImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
                    b = baos.toByteArray();
                    Log.d("BITMAP", "Crossed3");
                } catch (NullPointerException e) {
                }

                Bundle informationBundle = new Bundle();
                informationBundle.putParcelable("event", event);
                intent.putExtra("image", b);
                Log.d("BITMAP", "Crossed4");
                intent.putExtras(informationBundle);
                Log.d("BITMAP", "Crossed5");
                mContext.startActivity(intent);


            }
        });

        return view;
    }

    public void setEventDetails() {

        mEventName.setText(event.getName());
        mEventVenue.setText(event.getVenue());
        mEventTime.setText(Event.displayDateFormat.format(event.getDate()));
        mEventDesc.setText(event.getDescription());
        mEventCreator.setText("Created by: " + event.getEventCreator());

        String imgUrl = event.getImgURL().toString();
        if (!imgUrl.endsWith("null")) {
            DownloadImageTask downloadImageTask = new DownloadImageTask(mEventImage);
            Log.d("DEBUG-IMG", imgUrl);
            downloadImageTask.execute(imgUrl);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        event = parentActivity.getEvent();
        setEventDetails();
    }

}
