package com.example.rxjava2inaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rxjava2inaction.rxjava2.ControllerActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("CheckResult")
public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_controller).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ControllerActivity.class));
            }
        });

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Observable<Object> faceRecongnize = Observable.create(emitter -> {
                    Log.e(TAG, "检测到人脸");
                    emitter.onNext("");
                }).flatMap(new Function<Object, ObservableSource<Object>>() {
                    @Override
                    public ObservableSource<Object> apply(Object o) throws Exception {
                        Log.e(TAG, "flatMap 提取特征");
                        return null;
                    }
                }).observeOn(Schedulers.computation());

                Observable<Object> liveCheck = Observable.create(emitter -> {
                    Log.e(TAG, "检测到人脸，开灯");
                    emitter.onNext("");
                    emitter.onComplete();
                }).doOnNext(o -> Log.e(TAG, "检活成功"))
                        .observeOn(Schedulers.newThread());

                Observable<Object> TemperatureCheck = Observable.create(emitter -> Log.e(TAG, "测温"))
                        .observeOn(Schedulers.newThread());

                Observable.interval(5000, 100, TimeUnit.MILLISECONDS).
                        zip(liveCheck, faceRecongnize, new BiFunction<Object, Object, Object>() {
                            @Override
                            public Object apply(Object o, Object o2) throws Exception {
                                Log.e(TAG, "合并结果");
                                return o;
                            }})
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(Schedulers.io())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                Log.e(TAG, "成功");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "失败");
                            }
                        });
            }
        });
    }
}
