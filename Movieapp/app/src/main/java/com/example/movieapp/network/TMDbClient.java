package com.example.movieapp.network;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movieapp.models.Movie;
import com.example.movieapp.models.MovieNetworkResponse;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class TMDbClient {
    private static final Object LOCK = new Object();
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static TMDbAPI sTMDbAPI;
    private static TMDbClient sInstance;

    //private constructor for singleton pattern
    private TMDbClient() {

    }

    public static TMDbClient getInstance() {
        if (sInstance == null || sTMDbAPI == null) {
            synchronized (LOCK) {
                //building OkHttp client
                OkHttpClient httpClient = new OkHttpClient.Builder().build();

                //build retrofit instance
                Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL).client(httpClient)
                        .addConverterFactory(GsonConverterFactory.create());

                sInstance = new TMDbClient();
                sTMDbAPI = builder.build().create(TMDbAPI.class);
            }
        }
        return sInstance;
    }

    public LiveData<List<Movie>> getPopularMovies(String apiKey) {
        final MutableLiveData<List<Movie>> mutableLiveData = new MutableLiveData<>();
        sTMDbAPI.getPopularMovies(apiKey).enqueue(new Callback<MovieNetworkResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieNetworkResponse> call, @NonNull Response<MovieNetworkResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mutableLiveData.postValue(response.body().getMovies());
                } else {
                    Log.e("tag", response.message());
                }
            }

            @Override
            public void onFailure(Call<MovieNetworkResponse> call, Throwable t) {
                Timber.i("getPopularMovies() on Failure");
                Timber.e(t);
            }
        });
        return mutableLiveData;
    }

    public LiveData<List<Movie>> getTopRatedMovies(String apiKey) {
        final MutableLiveData<List<Movie>> mutableLiveData = new MutableLiveData<>();
        sTMDbAPI.getTopRatedMovies(apiKey).enqueue(new Callback<MovieNetworkResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieNetworkResponse> call, @NonNull Response<MovieNetworkResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mutableLiveData.postValue(response.body().getMovies());
                } else {
                    Timber.e(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieNetworkResponse> call, @NonNull Throwable t) {
                Timber.e(t);
            }
        });
        return mutableLiveData;
    }


}

