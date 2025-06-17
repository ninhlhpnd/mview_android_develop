//package com.mtsc.mview.model;
//
//import android.os.Handler;
//import android.os.Looper;
//
//import com.mtsc.mview.my_interface.DataListener;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.CopyOnWriteArraySet;
//
//public class BleDataManager {
//    private static class DataTask {
//        final String deviceName;
//        final String sensorName;
//        final float value;
//
//        DataTask(String deviceName, String sensorName, float value) {
//            this.deviceName = deviceName;
//            this.sensorName = sensorName;
//            this.value = value;
//        }
//    }
//    private static final int MAX_QUEUE_SIZE = 1000;
//    private final ConcurrentLinkedQueue<Runnable> dataQueue = new ConcurrentLinkedQueue<>();
//    private final Handler mainHandler;
//    private final Set<DataListener> listeners = new CopyOnWriteArraySet<>();
//    private volatile boolean isRunning = false;
//    private Thread processingThread;
//
//    public BleDataManager() {
//        mainHandler = new Handler(Looper.getMainLooper());
//    }
//
//    public void addListener(DataListener listener) {
//        listeners.add(listener);
//    }
//
//    public void removeListener(DataListener listener) {
//        listeners.remove(listener);
//    }
//
//    public void start() {
//        if (isRunning) return;
//        isRunning = true;
//
//        processingThread = new Thread(() -> {
//            List<DataTask> batch = new ArrayList<>();
//
//            while (isRunning) {
//                // Collect data into batch
//                DataTask task = (DataTask) dataQueue.poll();
//                if (task != null) {
//                    batch.add(task);
//                }
//
//                // Process batch when it reaches size or after timeout
//                if (!batch.isEmpty() && (batch.size() >= 100 || dataQueue.isEmpty())) {
//                    final List<DataTask> currentBatch = new ArrayList<>(batch);
//                    mainHandler.post(() -> {
//                        for (DataTask t : currentBatch) {
//                            for (DataListener listener : listeners) {
//                                listener.onDataReceived(t.deviceName, t.sensorName, t.value);
//                            }
//                        }
//                    });
//                    batch.clear();
//                }
//
//                // Small sleep to prevent CPU overload
//                if (dataQueue.isEmpty()) {
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                        break;
//                    }
//                }
//            }
//        });
//        processingThread.start();
//    }
//
//    public void stop() {
//        isRunning = false;
//        if (processingThread != null) {
//            processingThread.interrupt();
//            processingThread = null;
//        }
//        dataQueue.clear();
//    }
//
//    public void addData(String deviceName, String sensorname,List<Float> value) {
//        if (!isRunning) return;
//
//        if (dataQueue.size() < MAX_QUEUE_SIZE) {
//            dataQueue.offer(() -> {
//                for (DataListener listener : listeners) {
//                    listener.onDataReceived(deviceName,sensorname, value);
//                }
//            });
//        }
//    }
//
//}
//
