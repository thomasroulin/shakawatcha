package ch.hearc.android.shakawatcha.fragments.movies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.hearc.android.shakawatcha.R;
import ch.hearc.android.shakawatcha.objects.Movie;

/**
 * Created by thomas.roulin on 12.11.2015.
 */
public class FragmentMovie extends Fragment {

    private ImageView ivBackdrop;
    private ImageView ivPoster;
    private TextView tvTitle;
    private TextView tvReleaseDate;

    private Movie movie;
    private String movieTitle;
    private int movieId;

    public static final String ARG_TITLE = "TITLE";
    public static final String ARG_ID = "ID";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        movieTitle = args.getString(ARG_TITLE);
        movieId = args.getInt(ARG_ID);

        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ivBackdrop = (ImageView) getActivity().findViewById(R.id.movie_backdrop);
        ivPoster = (ImageView) getActivity().findViewById(R.id.movie_poster);
        tvTitle = (TextView) getActivity().findViewById(R.id.movie_title);
        tvReleaseDate = (TextView)getActivity().findViewById(R.id.movie_release_date);

        tvTitle.setText(this.movieTitle);

        queueMovieRequest(this.movieId);
        queueMovieBackdrop(this.movieId);
    }

    public static FragmentMovie newInstance(String movieTitle, int movieId) {
        FragmentMovie fragmentMovie = new FragmentMovie();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, movieTitle);
        args.putInt(ARG_ID, movieId);
        fragmentMovie.setArguments(args);

        return fragmentMovie;
    }

    public void queueMovieRequest(int id) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String query = "http://api.themoviedb.org/3/movie/" + id + "?api_key=" + Movie.API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, query, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    showMovie(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("JEEZ", "Volley Error");
            }
        });

        queue.add(stringRequest);
    }

    public void queueMovieBackdrop(int id){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String query = "http://api.themoviedb.org/3/movie/" + id + "/images?api_key=" + Movie.API_KEY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, query, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    showBackdrop(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("JEEZ", "Volley Error");
            }
        });

        queue.add(stringRequest);
    }

    private void showBackdrop(String response) throws JSONException {
        JSONObject imagesJSON = new JSONObject(response);
        JSONArray backdropsJSON = imagesJSON.getJSONArray("backdrops");
        String backdropPath = backdropsJSON.getJSONObject(0).getString("file_path");

        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w1280" + backdropPath).into(ivBackdrop);
    }

    private void showMovie(String response) throws JSONException {
        Movie movie = new Movie(new JSONObject(response));

        tvReleaseDate.setText(movie.getReleaseDate());


        String posterPath = movie.getPosterPath();
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w780/" + posterPath).into(ivPoster);
    }
}
