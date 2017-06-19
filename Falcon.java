package com.dingdangmao.app.Falcon;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by suxiaohui on 2017/6/18.
 */

public class Falcon {
    public static final int MSG_SUCCESS=0;
    public static final int MSG_ERROR=-1;
    private static volatile Falcon app;
    private HashMap<Integer ,LinkedList<Runnable>> Task;
    private HashMap<Integer ,Runnable> Finish;
    private HashMap<Integer ,Runnable> Error;
    private HashSet<Integer> Flag;
    private int index;
    private Shoulders currentShoulders;
    private Handler hld;

    private Falcon(){
        Task=new HashMap<Integer,LinkedList<Runnable>>();
        Flag=new HashSet<Integer>();
        Finish=new HashMap<Integer ,Runnable>();
        Error=new HashMap<Integer ,Runnable>();
        hld=new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
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
                        Task.remove(id2);
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
        };
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

        RunTask tmptask=new RunTask(task,currentShoulders,hld,app.index);
        Task.get(app.index).offerLast(tmptask);
        return app;
    }
    public Falcon finish(Action0 task){
        RunTask0 tmptask=new RunTask0(task,currentShoulders);
        Finish.put(index,tmptask);
        return app;
    }
    public Falcon error(Action0 task){
        RunTask0 tmptask=new RunTask0(task,currentShoulders);
        Error.put(index,tmptask);
        return app;
    }
    public Falcon flip(Shoulders sld){
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
}
