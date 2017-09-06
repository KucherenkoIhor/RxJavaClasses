package com.yalantis.rxjavaclasses;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        final Button button = (Button) findViewById(R.id.button);

        Observable.fromPublisher(new Publisher<Integer>() {

            int count = 0;

            @Override
            public void subscribe(final Subscriber<? super Integer> s) {

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        count ++;
                        s.onNext(count);
                    }
                });

            }
        })
                .debounce(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                button.setText(String.valueOf(integer));
            }
        });

        //////////////////////////

        final PublishSubject<Integer> publishSubject = PublishSubject.create();

        final int[] count = {0};

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count[0]++;
                publishSubject.onNext(count[0]);
            }
        });

        publishSubject

                .debounce(1000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                button.setText(String.valueOf(integer));
            }
        });

        //////////////////

        final BehaviorSubject<Integer> behaviorSubject = BehaviorSubject.create();

        final int[] count1 = {0};

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count1[0]++;
                behaviorSubject.onNext(count1[0]);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                behaviorSubject
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                button.setText(String.valueOf(integer));
                            }
                        });

            }
        }, 3000);


        ///////////////////////



        ConnectableObservable<Integer> connectableObservable = Observable.range(0, 1_000_000)
                .delay(1, TimeUnit.SECONDS)
                .publish();

        connectableObservable.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TAGGGG", String.valueOf(integer));
            }
        });

        connectableObservable.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("TAGGGG", String.valueOf(integer));
            }
        });

        connectableObservable.connect();

        connectableObservable.autoConnect();

        connectableObservable.autoConnect(4);









    }
}
