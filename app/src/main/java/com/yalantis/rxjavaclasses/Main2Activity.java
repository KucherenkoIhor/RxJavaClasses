package com.yalantis.rxjavaclasses;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        Flowable<Long> longFlowable = Flowable.rangeLong(0, 1_000_000);

        longFlowable.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(MainActivity.class.getSimpleName(), String.valueOf(aLong));
            }
        });

        longFlowable.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                Log.d(MainActivity.class.getSimpleName(), String.valueOf(aLong));
            }
        });

        // !!! МЫ МОЖЕМ ПОДПИСЫВАТЬСЯ НА ОДИН И ТОТ ЖЕ Flowable сколь угодно
        // при каждой новой подписки генерация элементов начинается заново

        Flowable<String> stringFlowable = longFlowable
                .map(new Function<Long, String>() {
                    @Override
                    public String apply(@NonNull Long aLong) throws Exception {
                        return String.valueOf(aLong);
                    }
                });

        stringFlowable.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(MainActivity.class.getSimpleName(), s);
            }
        });

        // !!! КАЖДЫЙ ОПЕРАТОР RX ВОЗВРАЩАЕТ НОВЫЙ ИНСТАНС Flowable



    }
}
