package com.yalantis.rxjavaclasses;

import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ExampleUnitTest {

    /***
     *
     * RxJava - библиотека, которая позволяет писать программы на Java в реактивном стиле.
     * Что значит в реактивном стиле?
     * В Java мы привыкли писать в императивном стиле:
     * b = 4;
     * c = 3;
     * a = b + c;
     * //a = 7;
     *
     * b = 8;
     * //a = 7;
     * При изменении значения b значение a не изменилось.
     *
     * В реактивном программирование значение переменной a измениться и станет равно 11.
     *
     * Зачем нам реактивное программирование?
     * В Android постоянно происходят события, такие как:
     *  - взаимодействие пользователя с интерфейс
     *  - получение результата длительной асинхронной операции
     *  - интенты
     *  - пуш-уведомления
     *  - и т.д. и т.п.
     *
     *  Когда происходят эти действия (меняется значение переменной b) нам нужно на них
     *  реагировать и что-то делать (значение переменной a должно измениться).
     *
     */
    @Test
    public void simpleSample() throws Exception {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("event");
            }
        }).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("reaction");
            }
        });
    }

    /***
     *
     * В примере выше хорошо видно, что эта штука основана на паттерне Наблюдатель(Observable, Callback, Listener)
     * https://en.wikipedia.org/wiki/Observer_pattern
     *
     */

    @Test
    public void variableAreactsToChangingOfVariableB() throws Exception {

        Observable<Integer> b = Observable.fromPublisher(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(4);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                subscriber.onNext(8);
            }
        });

        b.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer b) throws Exception {
                int c = 3;
                int a = b + c;
                System.out.println(a);
            }
        });

    }


    /***
     *
     * Базовые типы:
     *
     * 1) Observable - non-backpressure
     *
     *
     * Что такое Backpressure?
     * В общем случае, у нас есть сущность, которая производит элементы, например: Observable.
     * И есть тот, кто эти элементы потребляет (Consumer) и что-то с ними делают.
     * Может возникнуть ситуация, в которой потребитель будет просто не успевать потреблять
     * элементы. То есть, backpressure - это когда Observable производит элементы быстрее, чем
     * Consumer их потребляет. В этом случае мы получим MissingBackpressureException
     *
     * Главное отличие RxJava1 и RxJava2 в том, что во второй появился класс Flowable, который
     * изначально сделан так, чтоб избегать проблем с backpressure.
     *
     */
    @Test
    public void sampleObservable() throws Exception {

        Observable.range(0, 1_000_000)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        System.out.println(integer);
                    }
                });

    }

    /***
     *
     *
     * 2) Flowable
     *
     */
    @Test
    public void sampleFlowable() throws Exception {
        Flowable.range(0, 1_000_000)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //onNext
                        System.out.println(integer);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //onError
                        //если возникнет ошибка, то мы сможем обработать ее здесь
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        //onComplete
                        // а этот колбек вызовется после того как Flowable произведет последний элемент
                    }
                });

        /***
         *
         * Метод subscribe перегружен
         *
         * он принимает три колбека onNext, onComplete, onError
         *
         *
         *
         */

        // onComplete и onError это терминальные колбеки. То есть, после них ничего не будет происходить
    }

    /***
     *
     * 3)  Completable. Используется когда нам не нужно получать результат
     *  Мы просто хотим знать, что действие выполнено
     *
     *
     */
    @Test
    public void sampleCompletable() throws Exception {

        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("start");
                Thread.sleep(2000);
                System.out.println("stop");
            }
        }).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                System.out.println("on complete");
            }
        });

        //у Completable есть только onComplete и onError

    }

    /***
     *
     * 4)  Single. Observable и Flowable это потоки. То есть, они могут производит сколь угодно много элементов.
     * Это нам подходит, например в случае отслеживания событий, когда пользователь взаимодействует с UI,
     *  ибо пользователь может много раз кликнуть по кнопке.
     *
     *  В случае же, когда мы отправляем запрос к бд или на сервер, мы получим ответ только один раз
     *  Flowable и Observable не сильео подходят в данной ситуации.
     *
     *  Для этого есть Single.
     *
     *
     */
    @Test
    public void sampleSingle() throws Exception {

        Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 2 + 2;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);
            }
        });

        //у Completable есть только onSuccess и onError

    }


    /***
     *
     * 5) Maybe. Среднее между Completable и Single
     *
     *
     *
     */
    @Test
    public void sampleMaybe() throws Exception {

        Maybe.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return null;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);
            }
        });

        //у него есть onSuccess onError onComplete

    }

    /**
     * Многопоточность
     *
     *  subscribeOn(Schedulers.io())
     *
     *  observeOn(AndroidSchedulers.mainThread())
     *
     *
     */
    @Test
    public void sampleWorkers() throws Exception {

        Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 2 + 2;
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println(integer);
            }
        });

    }


}