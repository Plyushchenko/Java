import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl {
    private final ArrayList<MyThread> threads;
    private final ArrayDeque<MyTask> tasks;
    private boolean shutdowned;

    public ThreadPoolImpl(int n){
        tasks = new ArrayDeque<>();
        shutdowned = false;
        threads = new ArrayList<>();
        for (int i = 0; i < n; i++){
            threads.add(new MyThread());
        }
        threads.forEach(MyThread::start);
    }

    private boolean isShutdowned() {
        return shutdowned;
    }

    private void setShutdowned() {
        shutdowned = true;
    }

    public void shutdown(){
        if (isShutdowned()){
            return;
        }
        tasks.forEach(MyTask::setThrewException);
        tasks.clear();
        threads.forEach(MyThread::interrupt);
        setShutdowned();
    }


    private class MyThread extends Thread{
        @Override
        public void run(){
            while (!isInterrupted()){
                MyTask task = null;
                synchronized (tasks){
                    if (tasks.isEmpty()){
                        try{
                            tasks.wait();
                        } catch (InterruptedException e) {
                            //just ignore
                        }
                        continue;
                    }
                    task = tasks.poll();
                }
                if (task == null){
                    continue;
                }
                task.execute();
            }
        }
    }


    public <T> MyTask<T> submit(Supplier<T> supplier){
        MyTask<T> task = new MyTask<>(supplier);
        synchronized (tasks){
            tasks.add(task);
            tasks.notify();
        }
        return task;
    }

    private class MyTask<T> implements LightFuture<T>{
        private boolean ready = false;
        private boolean threwException = false;
        private Supplier<T> supplier;
        private T taskResult;
        private ArrayList<MyTask> waitingTasks;
        private MyTask(Supplier<T> supplier){
            this.supplier = supplier;
            waitingTasks = new ArrayList<>();
        }

        synchronized boolean isThrewException() {
            return threwException;
        }

        synchronized void setThrewException() {
            threwException = true;
        }

        @Override
        public synchronized boolean isReady() {
            return ready;
        }

        private synchronized void setReady() {
            ready = true;
        }

        @Override
        public T get() throws LightExecutionException, InterruptedException {
            synchronized (this) {
                while (!isReady() && !isThrewException() && !isShutdowned()) {
                    wait();
                }
            }
            if (isThrewException() || isShutdowned()){
                throw new LightExecutionException();
            }
            return taskResult;
        }

        @Override
        public <Y> LightFuture<Y> thenApply(Function<T, Y> function) {
            Supplier<Y> supplier = () -> {
                try {
                    return function.apply(get());
                } catch (LightExecutionException | InterruptedException e) {
                    setThrewException();
                    return null;
                }
            };

            MyTask<Y> newTask;
            synchronized (this) {
                if (isReady()) {
                    newTask = submit(supplier);
                } else {
                    newTask = new MyTask<>(supplier);
                    waitingTasks.add(newTask);
                }
            }
            return newTask;
        }

        private void execute(){
            try {
                taskResult = supplier.get();
            } catch (Exception e) {
                threwException = true;
            }
            synchronized (this){
                for (MyTask waitingTask : waitingTasks) {
                    tasks.add(waitingTask);
                    tasks.notify();
                }
                setReady();
                notifyAll();
            }
        }

    }

}
