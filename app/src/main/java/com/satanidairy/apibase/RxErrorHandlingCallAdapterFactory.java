package com.satanidairy.apibase;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.UnknownHostException;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RxErrorHandlingCallAdapterFactory extends CallAdapter.Factory {
    public Context context;
    private RxJava2CallAdapterFactory original;

    public RxErrorHandlingCallAdapterFactory(Context context) {
        this.context = context;
        original = RxJava2CallAdapterFactory.create();
    }

    public RxErrorHandlingCallAdapterFactory() {
        original = RxJava2CallAdapterFactory.create();
    }

    public static CallAdapter.Factory create(Context context) {
        return new RxErrorHandlingCallAdapterFactory();
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        return new RxCallAdapterWrapper(retrofit, original.get(returnType, annotations, retrofit), context);
    }

    private static class RxCallAdapterWrapper<R> implements CallAdapter<R, Object> {
        private final Retrofit retrofit;
        private final CallAdapter<R, Object> wrapped;
        private Context context;

        public RxCallAdapterWrapper(Retrofit retrofit, CallAdapter<R, Object> wrapped, Context context) {
            this.retrofit = retrofit;
            this.wrapped = wrapped;
            this.context = context;
        }

        @Override
        public Type responseType() {
            return wrapped.responseType();
        }

        @Override
        public Object adapt(Call<R> call) {
            Object result = wrapped.adapt(call);
            if (result instanceof Single) {
                return ((Single) result).onErrorResumeNext(new Function<Throwable, SingleSource>() {
                    @Override
                    public SingleSource apply(@NonNull Throwable throwable) {
                        return Single.error(asRetrofitException(throwable, context));
                    }
                });
            }
            if (result instanceof Observable) {
                return ((Observable) result).onErrorResumeNext(new Function<Throwable, ObservableSource>() {
                    @Override
                    public ObservableSource apply(@NonNull Throwable throwable) {
                        return Observable.error(asRetrofitException(throwable, context));
                    }
                });
            }

            if (result instanceof Completable) {
                return ((Completable) result).onErrorResumeNext(new Function<Throwable, CompletableSource>() {
                    @Override
                    public CompletableSource apply(@NonNull Throwable throwable) {
                        return Completable.error(asRetrofitException(throwable, context));
                    }
                });
            }

            return result;
        }

        private RetrofitException asRetrofitException(Throwable throwable, final Context context) {

            if (throwable instanceof UnknownHostException) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"Please check your internet connection",Toast.LENGTH_LONG).show();
                    }
                });
                return RetrofitException.Companion.networkError((IOException) throwable);
            }

            // We had non-200 http error
            if (throwable instanceof retrofit2.HttpException) {
                retrofit2.HttpException httpException = (retrofit2.HttpException) throwable;
                Response response = httpException.response();
                return RetrofitException.Companion.httpError(response.raw().request().url().toString(), response, retrofit);
            }

            // We don't know what happened. We need to simply convert to an unknown error

            return RetrofitException.Companion.unexpectedError(throwable);
        }
    }
}