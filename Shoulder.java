package com.dingdangmao.app.Falcon;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by suxiaohui on 2017/6/19.
 */

public class Shoulder {
    private  Shoulders main;
    private  Shoulders back;
    private static Shoulder app;
    private Shoulder(){
        main=new MainThread();
        back=new BackThread();
    }
    public static Shoulders getMain(){
        if(app==null)
            app=new Shoulder();
        return app.main;
    }
    public static Shoulders getBack(){
        if(app==null)
            app=new Shoulder();
        return app.back;
    }

    class MainThread implements Shoulders{
        private Handler hld;
        public MainThread(){
            hld=new Handler(Looper.getMainLooper());
        }

        public void dispatch(Runnable e){
            hld.post(e);
        }
    }
    class BackThread implements Shoulders{
        private ExecutorService exec= Executors.newCachedThreadPool();
        public BackThread(){

        }
        public void dispatch(Runnable e){
            exec.execute(e);
        }
        public void close(){

        }
    }

}
