package com.satanidairy.apibase;
import android.content.Context;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FetchServiceBase {

    private Retrofit getRestAdapter(final Context context) {
        Interceptor interceptor = chain -> {

            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("x-access-token","")
                    .method(original.method(), original.body())
                    .build();


            Response response = chain.proceed(request);

            return response;
        };

        //setup cache
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(loggingInterceptor)
                .addInterceptor(interceptor)
                .readTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES)
                .cache(provideHttpCache(context));

        OkHttpClient client = builder.build();

        return new Retrofit.Builder()
                //.baseUrl(BuildConfig.BASE_URL_APP_SERVER)
                .addCallAdapterFactory(new RxErrorHandlingCallAdapterFactory(context))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private Cache provideHttpCache(Context context) {
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(context.getApplicationContext().getCacheDir(), cacheSize);
    }

    public FetchServiceInterface getFetcherService(Context context) {
        return getRestAdapter(context).create(FetchServiceInterface.class);
    }
}
