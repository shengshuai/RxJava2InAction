package com.example.rxjava2inaction.rxjava2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rxjava2inaction.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("CheckResult")
public class ControllerActivity extends AppCompatActivity {

    public static final String TAG = ControllerActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle("RxJava2操作符");
        }

        findViewById(R.id.btn_create).setOnClickListener(v -> {
            Create();
        });

        findViewById(R.id.btn_map).setOnClickListener(v -> {
            Map();
        });

        findViewById(R.id.btn_zip).setOnClickListener(v -> {
            Zip();
        });

        findViewById(R.id.btn_concat).setOnClickListener(v -> {
            Concat();
        });

        findViewById(R.id.btn_faltmap).setOnClickListener(v -> {
            FlatMap();
        });

        findViewById(R.id.btn_concatmap).setOnClickListener(v -> {
            ConcatMap();
        });

        findViewById(R.id.btn_distinct).setOnClickListener(v -> {
            distinct();
        });

        findViewById(R.id.btn_filter).setOnClickListener(v -> {
            Filter();
        });

        findViewById(R.id.btn_buffer).setOnClickListener(v -> {
            buffer();
        });

        findViewById(R.id.btn_timer).setOnClickListener(v -> {
            timer();
        });

        findViewById(R.id.btn_interval).setOnClickListener(v -> {
            interval();
        });

        findViewById(R.id.btn_doOnNext).setOnClickListener(v -> {
            doOnNext();
        });

        findViewById(R.id.btn_skip).setOnClickListener(v -> {
            skip();
        });

        findViewById(R.id.btn_take).setOnClickListener(v -> {
            take();
        });

        findViewById(R.id.btn_just).setOnClickListener(v -> {
            just();
        });

        findViewById(R.id.btn_signle).setOnClickListener(v -> {
            Single();
        });

        findViewById(R.id.btn_debounce).setOnClickListener(v -> {
            debounce();
        });

        findViewById(R.id.btn_defer).setOnClickListener(v -> {
            defer();
        });

        findViewById(R.id.btn_last).setOnClickListener(v -> {
            last();
        });

        findViewById(R.id.btn_merge).setOnClickListener(v -> {
            merge();
        });

        findViewById(R.id.btn_reduce).setOnClickListener(v -> {
            reduce();
        });

        findViewById(R.id.btn_scan).setOnClickListener(v -> {
            scan();
        });

        findViewById(R.id.btn_window).setOnClickListener(v -> {
            window();
        });
    }

    //主要用于产生一个 Obserable 被观察者对象
    void Create() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Thread.sleep(100); //每间隔100ms发射一次，当作取帧
                emitter.onNext("送帧0" + Thread.currentThread().getName());
                Thread.sleep(100); //每间隔100ms发射一次，当作取帧
                emitter.onNext("送帧1" + Thread.currentThread().getName());
                Thread.sleep(100); //每间隔100ms发射一次，当作取帧
                emitter.onNext("送帧2" + Thread.currentThread().getName());
                Thread.sleep(100); //每间隔100ms发射一次，当作取帧
                emitter.onNext("送帧3" + Thread.currentThread().getName());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    private Disposable mDisposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                        Log.e(TAG, "onSubscribe : " + d.isDisposed() + "\n" );
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "onNext : value : " + s + "\n" );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError : value : " + e.getMessage() + "\n" );
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete" + "\n" );
                    }
                });
    }

    //作用是对发射时间发送的每一个事件应用一个函数
    //map 基本作用就是将一个 Observable 通过某种函数关系，转换为另一种 Observable，上面例子中就是把我们的 Integer 数据变成了 String 类型
    void Map() {

        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return "This is result " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.e(TAG, "accept : " + s +"\n" );
            }
        });

    }

    //zip 专用于合并事件，该合并不是连接（连接操作符后面会说），而是两两配对，也就意味着，最终配对出的 Observable 发射事件数目只和少的那个相同
    //zip 组合事件的过程就是分别从发射器 A 和发射器 B 各取出一个事件来组合，并且一个事件只能被使用一次，组合的顺序是严格按照事件发送的顺序来进行的，
    // 所以上面截图中，可以看到，1 永远是和 A 结合的，2 永远是和 B 结合的
    void Zip() {
        Observable.zip(getStringObservable(), getIntegerObservable(), new BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer integer) throws Exception {
                return s + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "zip : accept : " + s + "\n");
            }
        });
    }

    private Observable<String> getStringObservable() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                if (!e.isDisposed()) {
                    e.onNext("A");
                    Log.e(TAG, "String emit : A \n");
                    e.onNext("B");
                    Log.e(TAG, "String emit : B \n");
                    e.onNext("C");
                    Log.e(TAG, "String emit : C \n");
                }
            }
        });
    }

    private Observable<Integer> getIntegerObservable() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                if (!e.isDisposed()) {
                    e.onNext(1);
                    Log.e(TAG, "Integer emit : 1 \n");
                    e.onNext(2);
                    Log.e(TAG, "Integer emit : 2 \n");
                    e.onNext(3);
                    Log.e(TAG, "Integer emit : 3 \n");
                    e.onNext(4);
                    Log.e(TAG, "Integer emit : 4 \n");
                    e.onNext(5);
                    Log.e(TAG, "Integer emit : 5 \n");
                }
            }
        });
    }

    //对于单一的把两个发射器连接成一个发射器，虽然 zip 不能完成，但我们还是可以自力更生，官方提供的 concat 让我们的问题得到了完美解决。
    void Concat() {
        Observable.concat(Observable.just(1, 2, 3), Observable.just(4, 5, 6))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "concat : "+ integer + "\n" );
                    }
                });
    }

    //FlatMap 是一个很有趣的东西，我坚信你在实际开发中会经常用到。它可以把一个发射器 Observable 通过某种方法转换为多个 Observables，
    // 然后再把这些分散的 Observables装进一个单一的发射器 Observable。但有个需要注意的是，flatMap 并不能保证事件的顺序，如果需要保证，
    // 需要用到我们下面要讲的 ConcatMap
    void FlatMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    list.add("I am value " + integer);
                }
                int delayTime = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(list).delay(delayTime, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "flatMap : accept : " + s + "\n");
                    }
                });
    }

    //上面其实就说了，concatMap 与 FlatMap 的唯一区别就是 concatMap 保证了顺序，所以，我们就直接把 flatMap 替换为 concatMap
    void ConcatMap() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    list.add("I am value " + integer);
                }
                int delayTime = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(list).delay(delayTime, TimeUnit.MILLISECONDS);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.e(TAG, "concatMap : accept : " + s + "\n");
                    }
                });
    }

    //简单的去重嘛
    void distinct() {
        Observable.just(1, 1, 1, 2, 2, 3, 4, 5, 6)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "distinct : " + integer + "\n");
                    }
                });
    }

    //过滤器嘛。可以接受一个参数，让其过滤掉不符合我们条件的值
    void Filter() {
        Observable.just(1, 20, 65, -5, 7, 19)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer >= 10;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "filter : " + integer + "\n");
            }
        });
    }

    //buffer 操作符接受两个参数，buffer(count,skip)，作用是将 Observable 中的数据按 skip (步长) 分成最大不超过 count 的 buffer ，然后生成一个 Observable
    void buffer() {
        Observable.just(1, 2, 3, 4, 5)
                .buffer(3, 2)
                .subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> integers) throws Exception {
                        Log.e(TAG, "buffer size : " + integers.size() + "\n");
                        Log.e(TAG, "buffer value : " );
                        for (Integer i : integers) {
                            Log.e(TAG, i + "");
                        }
                        Log.e(TAG, "\n");
                    }
                });
    }

    //timer 很有意思，相当于一个定时任务。在 1.x 中它还可以执行间隔逻辑，但在 2.x 中此功能被交给了 interval，下一个会介绍。但需要注意的是，timer 和 interval 均默认在新线程
    void timer() {
        Log.e(TAG, "timer start : " + System.currentTimeMillis() + "\n");
        Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.e(TAG, "timer :" + aLong + " at " + System.currentTimeMillis() + "\n");
                    }
                });
    }

    //用于间隔时间执行某个操作，其接受三个参数，分别是第一次发送延迟，间隔时间，时间单位
    Disposable intervalisposable = null;
    void interval() {
        if (intervalisposable == null) {
            Log.e(TAG, "timer interval : " + System.currentTimeMillis() + "\n");
            intervalisposable = Observable.interval(1, 2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            Log.e(TAG, "interval :" + aLong + " at " + System.currentTimeMillis() + "\n");
                        }
                    });
        } else {
            intervalisposable.dispose();
            intervalisposable = null;
        }
    }

    //其实觉得 doOnNext 应该不算一个操作符，但考虑到其常用性，我们还是咬咬牙将它放在了这里。它的作用是让订阅者在接收到数据之前干点有意思的事情。
    // 假如我们在获取到数据之前想先保存一下它，无疑我们可以这样实现
    void doOnNext() {
        Observable.just(1, 2, 3, 4)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "doOnNext 保存 " + integer + "成功" + "\n");
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "doOnNext :" + integer + "\n");
            }
        });
    }

    //作用就和字面意思一样，接受一个 long 型参数 count ，代表跳过 count 个数目开始接收
    void skip() {
        Observable.just(1, 2, 3, 4, 5)
                .skip(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "doOnNext :" + integer + "\n");
                    }
                });
    }
    //接受一个 long 型参数 count ，代表至多接收 count 个数据
    void take() {
        Observable.just(1, 2, 3, 4, 5)
                .take(2)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "doOnNext :" + integer + "\n");
                    }
                });
    }

    //没什么好说的，其实在前面各种例子都说明了，就是一个简单的发射器依次调用 onNext() 方法。
    void just() {
        Observable.just(1, 2, 3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG,"accept : onNext : " + integer + "\n" );
                    }
                });
    }

    //顾名思义，Single 只会接收一个参数，而 SingleObserver 只会调用 onError() 或者 onSuccess()
    void Single(){
        Single.just(new Random().nextInt())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.e(TAG, "single : onSuccess : "+integer+"\n" );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "single : onError : "+e.getMessage()+"\n");
                    }
                });
    };

    //去除发送频率过快的项
    void debounce(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1); // skip
                Thread.sleep(400);
                emitter.onNext(2); // deliver
                Thread.sleep(505);
                emitter.onNext(3); // skip
                Thread.sleep(100);
                emitter.onNext(4); // deliver
                Thread.sleep(605);
                emitter.onNext(5); // deliver
                Thread.sleep(510);
                emitter.onComplete();
            }
        }).debounce(500, TimeUnit.MILLISECONDS) //去除发送间隔时间小于 500 毫秒的发射事件
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG,"debounce :" + integer + "\n");
            }
        });
    };

    //简单地时候就是每次订阅都会创建一个新的 Observable，并且如果没有被订阅，就不会产生新的 Observable。
    void defer(){
        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(1, 2, 3);
            }
        });

        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "defer : " + integer + "\n");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "defer : onError : " + e.getMessage() + "\n");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "defer : onComplete\n");
            }
        });

    };

    //取出可观察到的最后一个值，或者是满足某些条件的最后一项。
    void last(){
        Observable.just(1, 2, 3)
                .last(4)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "last : " + integer + "\n");
                    }
                });
    };

    //merge 的作用是把多个 Observable 结合起来，接受可变参数，也支持迭代器集合。注意它和 concat 的区别在于，不用等到 发射器 A 发送完所有的事件再进行发射器 B 的发送。
    void merge(){
        Observable.merge(Observable.just(1, 2), Observable.just(3, 4, 5))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "accept: merge :" + integer + "\n" );
                    }
                });
    };
    //reduce 操作符每次用一个方法处理一个值，可以有一个 seed 作为初始值。
    void reduce(){
        Observable.just(1, 2, 3)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        return integer + integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "accept: reduce : " + integer + "\n");
            }
        });
    };
    //scan 操作符作用和上面的 reduce 一致，唯一区别是 reduce 是个只追求结果的坏人，而 scan 会始终如一地把每一个步骤都输出
    void scan(){
        Observable.just(1, 2, 3)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        Log.e(TAG, "apply: scan : " + integer + "\n");
                        return integer + integer2;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "accept: scan : " + integer + "\n");
            }
        });
    };

    //按照实际划分窗口，将数据发送给不同的 Observable
    void window(){
        Observable.interval(1, TimeUnit.SECONDS) //1s一次
                .take(15) //最多15个
                .window(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Observable<Long>>() {
                    @Override
                    public void accept(Observable<Long> longObservable) throws Exception {
                        Log.e(TAG, "Sub Divide begin...\n");
                        longObservable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        Log.e(TAG, "Next:" + aLong + "\n");
                                    }
                                });
                    }
                });
    };

}