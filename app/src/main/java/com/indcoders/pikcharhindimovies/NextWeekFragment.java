package com.indcoders.pikcharhindimovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NextWeekFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NextWeekFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NextWeekFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    StaggeredGridView gridView;
    ArrayList<String> titles, posters, descs;
    ProgressDialog pd;

    private OnFragmentInteractionListener mListener;

    public NextWeekFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NextWeekFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NextWeekFragment newInstance(String param1, String param2) {
        NextWeekFragment fragment = new NextWeekFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        titles = new ArrayList<>();
        posters = new ArrayList<>();
        descs = new ArrayList<>();

        if (savedInstanceState == null) {
            new LoadMovies().execute(null, null, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_next_week, container, false);

        gridView = (StaggeredGridView) v.findViewById(R.id.grid_view);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Snackbar.make(view, descs.get(i), Snackbar.LENGTH_LONG)
                        .show(); // Don’t forget to show!
            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void updateUI() {
        gridView.setAdapter(new CustomAdapter());
        gridView.invalidateViews();
    }

    public class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_card, viewGroup, false);

            }

            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setText(titles.get(i));

            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

            Picasso.with(getActivity()).load(posters.get(i)).into(imageView);

            return view;
        }
    }

    public class LoadMovies extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(getActivity());
            pd.setIndeterminate(false);
            pd.setMax(100);
            pd.setMessage("Loading Movies...");
            pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            String url = "http://api.cinemalytics.com/v1/movie/nextchange?auth_token=" + getActivity().getResources().getString(R.string.api_key);
            String jsonStr = null;

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = null;

            try {
                response = client.newCall(request).execute();
                jsonStr = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Response", "Error: " + e);
            }

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        titles.add(jsonObject.getString("Title") + "\n" + jsonObject.getString("Rating"));
                        posters.add(jsonObject.getString("PosterPath"));
                        descs.add(jsonObject.getString("Description"));
                    }


                } catch (JSONException e) {
                    Log.e("error", e.toString());

                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pd.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateUI();
            pd.dismiss();
            super.onPostExecute(aVoid);
        }
    }
}
