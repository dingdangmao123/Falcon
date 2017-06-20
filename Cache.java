package Falcon;

import java.util.LinkedList;

/**
 * Created by dingdangmao on 2017/6/19.
 */

public class Cache {
    private final static  int max=10;
    private static LinkedList<Runnable> list=new  LinkedList<Runnable>();

    public  synchronized static Runnable get(){
        if (list.size() > 0)
                return list.removeFirst();
        return new RunTask();
    }
    public synchronized static boolean store(Runnable e){
        if(list.size()>=max){
            return false;
        }
        list.addLast(e);
        return true;
    }
    public synchronized static boolean store(LinkedList<Runnable> e){
        if(list.size()>=max){
            return false;
        }
        int i=max-list.size();
        for(int j=0;j<i;j++)
            list.addLast(e.getFirst());
        return true;
    }
}
