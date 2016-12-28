import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl {
    private final ArrayList<Thread> threads;
    private final ArrayDeque<MyTask> tasks;
    private boolean shutdowned;

    public ThreadPoolImpl(int n){
        tasks = new ArrayDeque<>();
        shutdowned = false;
        threads = new ArrayList<>();
        for (int i = 0; i < n; i++){
            threads.add(new Thread(new MyRunnable()));
        }
        threads.forEach(Thread::start);
    }

    private boolean isShutdowned() {
        return shutdowned;
    }

    private void setShutdowned() {
        shutdowned = true;
    }

    public synchronized void shutdown(){
        if (isShutdowned()){
            return;
        }
        tasks.forEach(MyTask::setThrewException);
        tasks.clear();
        threads.forEach(Thread::interrupt);
        setShutdowned();
    }


    private class MyRunnable implements Runnable{
        @Override
        public void run(){
            while (!Thread.currentThread().isInterrupted()){
                MyTask task;
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


    public <T> LightFuture<T> submit(Supplier<T> supplier){
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
        private final Supplier<T> supplier;
        private T taskResult;
        private final ArrayList<MyTask> waitingTasks;
        private Exception thrownException;
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
            if (isThrewException()){
                throw new LightExecutionException(thrownException);
            }
            if (isShutdowned()){
                throw new InterruptedException();
            }
            return taskResult;
        }

        @Override
        public <Y> LightFuture<Y> thenApply(Function<? super T, Y> function) {
            Supplier<Y> supplier = () -> {
                try {
                    return function.apply(get());
                } catch (LightExecutionException | InterruptedException e) {
                    e.initCause(e.getCause());
                    return null;
                }
            };

            LightFuture<Y> newTask;
            synchronized (this) {
                if (isReady()) {
                    newTask = submit(supplier);
                } else {
                    newTask = new MyTask<>(supplier);
                    waitingTasks.add((MyTask) newTask);
                }
            }
            return newTask;
        }

        private void execute(){
            try {
                taskResult = supplier.get();
            } catch (Exception e) {
                setThrewException();
                thrownException = e;
            }
            synchronized (this){
                synchronized (tasks){
                    for (MyTask waitingTask : waitingTasks) {
                        tasks.add(waitingTask);
                        tasks.notify();
                    }
                }
                setReady();
                notifyAll();
            }
        }

    }

}
