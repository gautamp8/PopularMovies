package brainbreaker.popularmovies.Activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import brainbreaker.popularmovies.Adapters.CustomGridAdapter;
import brainbreaker.popularmovies.Extras.Connectivity;
import brainbreaker.popularmovies.Listeners.MovieListLoadedListener;
import brainbreaker.popularmovies.Models.FavouriteMovies;
import brainbreaker.popularmovies.Models.MovieClass;
import brainbreaker.popularmovies.MyApplication;
import brainbreaker.popularmovies.R;
import brainbreaker.popularmovies.Utilities.FetchMovieList;
import brainbreaker.popularmovies.Utilities.NetworkUtils;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements MovieListLoadedListener, MainActivity.OverflowMenuClick{
    RecyclerView movieGridRecyclerView;
    CustomGridAdapter adapter;
    String apikey;
    ProgressDialog progress;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        apikey= getResources().getString(R.string.api_key);

        movieGridRecyclerView=(RecyclerView) rootView.findViewById(R.id.moviesRecyclerView);
        movieGridRecyclerView.setHasFixedSize(true);

        // The number of Columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        movieGridRecyclerView.setLayoutManager(gridLayoutManager);

        showProgressDialog();
        FetchMovieList fetchPopularMovieList = new FetchMovieList(MainActivityFragment.this);
        fetchPopularMovieList.execute("popularity");

        return rootView;
    }

    private void showProgressDialog() {
        // SHOW A PROGRESS DIALOG
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading Movies...");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    public void movieListLoadedListener(final ArrayList<MovieClass> movieList) {
        populateList(movieList);
    }

    private void populateList(ArrayList<MovieClass> movieList) {
        try {
            adapter = new CustomGridAdapter(getActivity(),movieList);
            movieGridRecyclerView.setAdapter(adapter);
            if(progress.isShowing()){
                progress.dismiss();
            }
            //Saving the Movie Array List to SharedPrefs so that it is available for offline access also.
            MyApplication.saveMovieList(getActivity(),movieList);

        }
        catch (NullPointerException e){
            boolean connect = Connectivity.isOnline(getActivity());
            if (!connect){
                Toast.makeText(getActivity(),"Unable to connect to Internet. Please check your connection.",Toast.LENGTH_SHORT).show();
                if (MyApplication.retrieveMovieList(getActivity())!=null){
                    adapter = new CustomGridAdapter(getActivity(),MyApplication.retrieveMovieList(getActivity()));
                    movieGridRecyclerView.setAdapter(adapter);
                    if(progress.isShowing()){
                        progress.dismiss();
                    }
                }
            }
        }
    }

    @Override
    public void onOverflowItemClicked(String item) {
        switch (item) {
            case "popularity": {
                FetchMovieList fetchpopularMovieList = new FetchMovieList(this);
                fetchpopularMovieList.execute("popularity");
                break;
            }
            case "rating": {
                FetchMovieList fetchpopularMovieList = new FetchMovieList(this);
                fetchpopularMovieList.execute("rating");
                break;
            }
            case "favourite":
                ArrayList<FavouriteMovies> FavMovieList = MyApplication.retrieveFavList(getActivity());
                if (FavMovieList != null) {
                    final ArrayList<MovieClass> movieList = new ArrayList<>();
                    for (int i = 0; i < FavMovieList.size(); i++) {
                        movieList.add(FavMovieList.get(i).getMovie());
                    }
                    populateList(movieList);
                } else {
                    Toast.makeText(getActivity(), "You don't have any favourites yet", Toast.LENGTH_SHORT).show();
                    FetchMovieList fetchMovieList = new FetchMovieList(this);
                    fetchMovieList.execute("popularity");
                }
                break;
            default: FetchMovieList fetchMovieList = new FetchMovieList(this);
                fetchMovieList.execute("popularity");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkReceiver, filter);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        Context context = getActivity();
        ((MainActivity)context).listener = this;

    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = NetworkUtils.getConnectivityStatusString(context);
            if (!status.equals("Not connected to Internet")){
                if (getActivity().getActionBar()!=null) {
                    String title = getActivity().getActionBar().getTitle().toString();
                    switch (title) {
                        case "Popular Movies": {
                            FetchMovieList fetchPopularMovieList = new FetchMovieList(MainActivityFragment.this);
                            fetchPopularMovieList.execute("popularity");
                            break;
                        }
                        case "Top Rated": {
                            FetchMovieList fetchPopularMovieList = new FetchMovieList(MainActivityFragment.this);
                            fetchPopularMovieList.execute("rating");
                            break;
                        }
                        default: {
                            ArrayList<FavouriteMovies> FavMovieList = MyApplication.retrieveFavList(getActivity());
                            if (FavMovieList != null) {
                                final ArrayList<MovieClass> movieList = new ArrayList<>();
                                for (int i = 0; i < FavMovieList.size(); i++) {
                                    movieList.add(FavMovieList.get(i).getMovie());
                                }
                                populateList(movieList);
                            }
                            break;
                        }
                    }
                }
                else {
                    if (getActivity().getActionBar()!=null) {
                        getActivity().getActionBar().setTitle("Popular Movies");
                        FetchMovieList fetchPopularMovieList = new FetchMovieList(MainActivityFragment.this);
                        fetchPopularMovieList.execute("popularity");
                    }

                }
            }
        }
    };
}
