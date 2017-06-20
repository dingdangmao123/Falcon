package com.dingdangmao.app.Falcon;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by suxiaohui on 2017/6/18.
 */

public class Falcon {
    public static final int MSG_SUCCESS=0;
    public static final int MSG_ERROR=-1;
    private static volatile Falcon app;
    private ConcurrentHashMap<Integer ,LinkedList<Runnable>> Task;
    private ConcurrentHashMap<Integer ,Runnable> Finish;
    private ConcurrentHashMap<Integer ,Runnable> Error;
    private HashSet<Integer> Flag;
    private MessageThread mThd;
    private Shoulders currentShoulders;
    private Shoulders pool;
    private volatile  Handler hld;
    private int index;

    private Falcon(){
        Task=new ConcurrentHashMap<Integer,LinkedList<Runnable>>();
        Flag=new HashSet<Integer>();
        Finish=new ConcurrentHashMap<Integer ,Runnable>();
        Error=new ConcurrentHashMap<Integer ,Runnable>();
        currentShoulders=pool=Shoulder.getBack();
        mThd=new MessageThread();
        hld=new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                handle(msg);
            }
        };
        mThd.start();
    }
    private void handle(Message msg){
        switch(msg.what) {
            case MSG_SUCCESS:
                int id=msg.arg1;
                RunTask tmp=(RunTask)Task.get(id).pollFirst();
                if(tmp==null){
                    Task.remove(id);
                    RunTask0 tmp0=(RunTask0)Finish.remove(id);
                    if(tmp0!=null){
                        tmp0.obj = msg.obj;
                        tmp0.sld.dispatch(tmp0);
                    }
                    break;
                }
                tmp.obj=msg.obj;
                tmp.sld.dispatch(tmp);
                break;
            case MSG_ERROR:
                int id2=msg.arg1;
                Cache.store(Task.remove(id2));
                Finish.remove(id2);
                RunTask0 tmp0=(RunTask0)Error.remove(id2);
                if(tmp0!=null) {
                    tmp0.obj = msg.obj;
                    tmp0.sld.dispatch(tmp0);
                }
                break;
            default:
                break;
        }

    }
    private static Falcon getInstance(){
        if(app==null){
            synchronized (Falcon.class){
                if(app==null) {
                    app=new Falcon();
                }
                return app;
            }
        }
        return app;
    }
    public static Falcon newTask(){
        Falcon app=getInstance();
        LinkedList<Runnable> queue=new LinkedList<Runnable>();
        ++app.index;
        app.Task.put(app.index,queue);
        app.Flag.add(app.index);
        return app;
    }
    public Falcon exec(Action task){
        if(task==null)
            throw new RuntimeException("empty task");

        RunTask tmptask=(RunTask)Cache.get();
        tmptask.init(task,currentShoulders,hld,index);
        Task.get(index).offerLast(tmptask);
        return app;
    }
    public Falcon finish(Action0 task){
        if(task==null)
            throw new RuntimeException("finish task is null");
        RunTask0 tmptask=new RunTask0(task,currentShoulders);
        Finish.put(index,tmptask);
        return app;
    }
    public Falcon error(Action0 task){
        if(task==null)
            throw new RuntimeException("error task is null");
        RunTask0 tmptask=new RunTask0(task,currentShoulders);
        Error.put(index,tmptask);
        return app;
    }
    public Falcon flip(Shoulders sld){
        if(sld==null)
            throw new RuntimeException("Shoulders is null");
        currentShoulders=sld;
        return app;
    }
    public Falcon start(){
        Iterator<Integer> it=Flag.iterator();
        while(it.hasNext()){
            int index=it.next();
            RunTask tmp=(RunTask)Task.get(index).pollFirst();
            tmp.sld.dispatch(tmp);
        }
        Flag.clear();
        return app;
    }
    class MessageThread extends Thread{
        private volatile Handler myHandler;
        public MessageThread(){
            super();
        }
        public void run(){
            Looper.prepare();
            myHandler=new Handler() {
                public void handleMessage(Message msg) {
                    handle(msg);
                }
            };
            hld=myHandler;
            Looper.loop();
        }
    }
}
