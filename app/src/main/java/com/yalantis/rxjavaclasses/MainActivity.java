package com.yalantis.rxjavaclasses;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class MainActivity extends AppCompatActivity {

    //метод subscribe возвращает instance of Disposable
    /*

    public interface Disposable {

        прекратить работу Flowable
        void dispose();

        проверить непрекратился ли еще Flowable
        boolean isDisposed();
    }
    */

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDisposable = Flowable.fromPublisher(new Publisher<Integer>() {

            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                for (int i = 0; i < 1_000_000; i ++) {
                    subscriber.onNext(i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        subscriber.onError(e);
                    }
                }
            }
        })

                .retry()//если произойдет ошибка, то сабскрайбер переподпишется на новый обсервейбл
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //функции doOnNext, doOnError, doOnComplete можно использовать,
                        // чтоб сделать какие-нибудь сайд эффекты. Например, залогировать что-то
                        Log.d(MainActivity.class.getSimpleName(), String.valueOf(integer));
                    }
                })
                .map(new Function<Integer, Integer>() {

                    @Override
                    public Integer apply(@NonNull Integer integer) throws Exception {
                        return integer * 2;
                    }
                })
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //функции doOnNext, doOnError, doOnComplete можно использовать,
                        // чтоб сделать какие-нибудь сайд эффекты. Например, залогировать что-то
                        Log.d(MainActivity.class.getSimpleName(), String.valueOf(integer));
                    }
                })
                .filter(new Predicate<Integer>() {
                    // Предикат это не абы какое название. Оно есть во многих язаках и обозначает функцию,
                    // которая что-то принимает на вход и возвращает булеан
                    @Override
                    public boolean test(@NonNull Integer integer) throws Exception {
                        return false;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //String.valueOf(integer) по производительности лучше, чем integer.toString()
                        ((TextView)findViewById(R.id.tv)).setText(String.valueOf(integer));

                        //теперь сабскрайбер хранит ссылку на текст вью, а текст вью хранит ссылку на активити
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ((TextView)findViewById(R.id.tv)).setText(throwable.getLocalizedMessage());
                        //опять же сабскрайбер хранит ссылку на текст вью, а текст вью хранит ссылку на активити
                    }
                });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
                finish();
                //вот тут мы стартанули новую активити и финишировали эту, но Сабскрайбер хранит ссылку на текствью
                // а текствью на активити и по этому сборщик мусора ее не уничтожит этот инстанс
                // ПРОЧИТАЙ ЭТО Java Memory Model https://habrahabr.ru/company/golovachcourses/blog/221133/
            }
        }, 5_000);

    }

    @Override
    protected void onStop() {

        // Мы это делаем, что избежать утечки памяти. Это еще одна причина, чтоб использовать
        // single если есть возможность

        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        super.onStop();
    }
}
