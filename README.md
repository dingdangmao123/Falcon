### Falcon - 一个简单的Android异步任务执行框架





#### 主要特性

- Falcon通过链式调用完成提交执行

- 调用链中可提交任意数量的任务，按先后顺序依次执行，前一返回值作为后一输入参数
​
- 调用链中随意切换任务的执行线程(主要是UI线程和后台线程池)

​

#### 主要函数

- newTak()开启一个新任务，同一个调用链可多次调用

- exec() 提交执行任务，可连续任意数量的调用

- flip() 设置后序任务的执行线程

- finish() 成功执行任务后最后调用

- error() 任务执行出错后执行，后取消后续任务

- start() 开启任务



#### 备注

- 主要受到RxJava链式调用和线程切换启发，因此山寨了这么一个简单的框架，功能并不完善，细节有些粗糙，有时间再更新。


- 原理很简单，就是线程池和handler的使用。


#### DEMO

````
      Falcon.newTask().flip(Shoulder.getBack()).exec(new Action(){
            @Override
            public Object call(Object obj) {
                Thread thd=Thread.currentThread();
                Log.i("Thread",thd.getName());
                String str=Curl.getText("https://www.baidu.com");
                Log.i("call",str);
                return str;

            }
        }).flip(Shoulder.getMain()).exec(new Action(){
            @Override
            public Object call(Object obj) {

                Log.i("call",obj.toString());
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
