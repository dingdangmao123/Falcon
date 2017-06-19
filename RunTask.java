package com.dingdangmao.app.Falcon;


import android.os.Handler;
import android.os.Message;

/**
 * Created by suxiaohui on 2017/6/18.
 */

public class RunTask implements Runnable{
    public Shoulders sld;
    private Action task;
    public Object obj;
    private Handler hld;
    public int id;
    public void init(Action task, Shoulders sld, android.os.Handler hld,int id){
        this.task=task;
        this.sld=sld;
        this.hld=hld;
        this.id=id;
    }
    public void run(){
        try {
            Object res = task.call(this.obj);
            Message msg=Message.obtain();
            msg.obj=res;
            msg.what=Falcon.MSG_SUCCESS;
            msg.arg1=id;
            hld.sendMessage(msg);

        }catch(Exception e){
            Message msg=Message.obtain();
            msg.obj=e;
            msg.what=Falcon.MSG_ERROR;
            msg.arg1=id;
            hld.sendMessage(msg);
        }finally {
            Cache.store(this);
        }

    }
}
