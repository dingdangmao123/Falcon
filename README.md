### Falcon - 一个简单的Android异步任务执行框架

#### 主要特性

- Falcon通过链式调用完成提交执
- 调用链中可提交任意数量的任务，按先后顺序依次执行，前一返回值作为后一输入参
- 中随意切换任务的执行线程

#### 主要函数

- newTak()开启一个新任务


- exec() 提交执行的任务

- flip() 设置后序任务的执行线程

- finish() 成功执行任务后最后调用

- error() 任务执行出错后执行，后取消后续任务

-  start() 开启任务
#### demo

````
    Falcon.newTask().flip(Shoulder.getBack()).exec(new Action(){
           @Override
           public Object call(Object obj) {
               Thread thd=Thread.currentThread();
               Log.i("Thread",thd.getName());
               String str=Curl.getText("http://www.baidu.com");
               Log.i("call",str);
               //tv2.setText("hello world");
               return str;
           }
       }).flip(Shoulder.getMain()).exec(new Action(){
           @Override
           public Object call(Object obj) {
               Log.i("call2",obj.toString());
               Thread thd=Thread.currentThread();
               Log.i("Thread",thd.getName());
               tv.setText(obj.toString());

               return "down";
           }
       }).finish(new Action0() {
           @Override
           public void call(Object obj) {
               Log.i("finish",obj.toString());
           }
       }).error(new Action0() {
           @Override
           public void call(Object obj) {
               Log.i("error",obj.toString());
           }
       }).start();
      ````

