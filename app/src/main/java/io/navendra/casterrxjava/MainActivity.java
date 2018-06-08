package io.navendra.casterrxjava;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Subscription subscription = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subscription = getDataObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(dataSubscriber);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(subscription!=null && !subscription.isUnsubscribed()){ //To prevent memory leak
            subscription.unsubscribe();
        }
    }

    @Nullable
    protected InternetData getData(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://www.vogella.com/index.html")
                .build();

        InternetData data = null;
        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){

                data = new InternetData();
                data.content = response.body().string();
            }
        }catch (IOException e){

        }

        return data;

    }

    public rx.Observable<InternetData> getDataObservable(){

        return rx.Observable.defer(new Func0<rx.Observable<InternetData>>() {
            @Override
            public rx.Observable<InternetData> call() {
                return  rx.Observable.just(getData());
            }
        });
    }

    Subscriber<InternetData> dataSubscriber = new Subscriber<InternetData>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Log.d("Error",e.toString());
        }

        @Override
        public void onNext(InternetData internetData) {
            TextView textView = findViewById(R.id.textView);
            textView.setText(internetData.content);
        }
    };
}
