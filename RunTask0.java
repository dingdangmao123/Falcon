package com.dingdangmao.app.Falcon;

/**
 * Created by suxiaohui on 2017/6/19.
 */

public class RunTask0 implements Runnable{
    public Shoulders sld;
    private Action0 task;
    public Object obj;
    public RunTask0(Action0 task, Shoulders sld){
        this.task=task;
        this.sld=sld;
    }
    public void run(){
        try{
           task.call(this.obj);
        }catch(Exception e){

        }
    }
}
