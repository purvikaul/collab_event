package edu.uci.collabevent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        View view = inflater.inflate(R.layout.fragment_about__event, container, false);

        mEventName = (TextView) view.findViewById(R.id.event_name);
        mEventVenue = (TextView) view.findViewById(R.id.event_venue);
        mEventTime = (TextView) view.findViewById(R.id.event_time);
        mEventDesc = (TextView) view.findViewById(R.id.event_desc);
        mEventCreator = (TextView) view.findViewById(R.id.event_creator);
        mEventImage = (ImageView) view.findViewById(R.id.event_img);


        setEventDetails();

        return view;
    }

    public void setEventDetails() {
        EventDetailActivity parentActivity = (EventDetailActivity) getActivity();
        Event event = parentActivity.getEvent();

//        Bundle bundle = this.getArguments();
//        String name = bundle.getString("event_name", "Name");
//        String venue = bundle.getString("event_venue", "TBD");
//        String time = bundle.getString("event_time", "TBD");
//        String desc = bundle.getString("event_desc", "Event Description ...");

        mEventName.setText(event.getName());
        mEventVenue.setText(event.getVenue());
        mEventTime.setText(Event.displayDateFormat.format(event.getDate()));
        mEventDesc.setText(event.getDescription());
        mEventCreator.setText(event.getEventCreator());

        String imgUrl = event.getImgURL().toString();
        if (!imgUrl.endsWith("null")) {
            DownloadImageTask downloadImageTask = new DownloadImageTask(mEventImage);
            Log.d("DEBUG-IMG", imgUrl);
            downloadImageTask.execute(imgUrl);
        }

    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
