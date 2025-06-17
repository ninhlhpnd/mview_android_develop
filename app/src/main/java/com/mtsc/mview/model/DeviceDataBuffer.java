package com.mtsc.mview.model;

import android.os.Looper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.os.Handler;

import com.mtsc.mview.MainActivity;
import com.mtsc.mview.ultis.DataEvent;

import org.greenrobot.eventbus.EventBus;

public class DeviceDataBuffer {
    private final Map<String, RingBuffer> deviceBuffers;
    private final int bufferSize;
    private final Handler mainHandler;
    private final ScheduledExecutorService scheduler;
    private volatile boolean isRunning = true;

    public DeviceDataBuffer(int bufferSize) {
        this.bufferSize = bufferSize;
        this.deviceBuffers = new ConcurrentHashMap<>();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        startProcessing();
    }

    public void addData(String deviceName, float value) {
        RingBuffer buffer = deviceBuffers.computeIfAbsent(deviceName,
                k -> new RingBuffer(bufferSize));
        buffer.add(value);
    }

    private void startProcessing() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!isRunning) return;

            Map<String, List<Float>> batchData = new HashMap<>();

            for (Map.Entry<String, RingBuffer> entry : deviceBuffers.entrySet()) {
                List<Float> values = entry.getValue().drain();
                if (!values.isEmpty()) {
                    batchData.put(entry.getKey(), values);
                }
            }

            if (!batchData.isEmpty()) {
                mainHandler.post(() -> {
                    for (Map.Entry<String, List<Float>> entry : batchData.entrySet()) {
                        String deviceName = entry.getKey();
                        List<Float> values = entry.getValue();

                        // Tìm sensor name từ sodoCambienList
                        String sensorName = "";
                        for (SodoCambien sodo : MainActivity.sodoCambienList) {
                            if (sodo.getBleDevice().getName().equals(deviceName)) {
                                sensorName = sodo.getTencambien();
                                break;
                            }
                        }

                        DulieuCB dulieuCB = new DulieuCB(deviceName, sensorName, values);
                        EventBus.getDefault().post(new DataEvent(dulieuCB));
                    }
                });
            }
        }, 100, 100, TimeUnit.MILLISECONDS); // Xử lý mỗi 100ms
    }

    public void stop() {
        isRunning = false;
        scheduler.shutdown();
        mainHandler.removeCallbacksAndMessages(null);
    }
    public class RingBuffer {
        private final float[] buffer;
        private final int capacity;
        private volatile int writeIndex = 0;
        private volatile int available = 0;

        public RingBuffer(int capacity) {
            this.capacity = capacity;
            this.buffer = new float[capacity];
        }

        public void add(float value) {
            synchronized (this) {
                buffer[writeIndex] = value;
                writeIndex = (writeIndex + 1) % capacity;
                if (available < capacity) {
                    available++;
                }
            }
        }

        public List<Float> drain() {
            synchronized (this) {
                if (available == 0) {
                    return Collections.emptyList();
                }

                List<Float> result = new ArrayList<>(available);
                for (int i = 0; i < available; i++) {
                    int index = (writeIndex - available + i + capacity) % capacity;
                    result.add(buffer[index]);
                }
                available = 0;
                return result;
            }
        }

        public boolean isEmpty() {
            return available == 0;
        }
    }
}
