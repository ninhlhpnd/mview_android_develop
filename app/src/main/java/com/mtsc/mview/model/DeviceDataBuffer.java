package com.mtsc.mview.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class DeviceDataBuffer {
    private final String deviceName;
    private final LinkedBlockingQueue<byte[]> dataQueue;
    private volatile boolean isRunning = true;

    public DeviceDataBuffer(String deviceName) {
        this.deviceName = deviceName;
        this.dataQueue = new LinkedBlockingQueue<>(1000); // Buffer size 1000
        startProcessingThread();
    }

    public void addData(byte[] data) {
        if (!dataQueue.offer(data)) {
            // Buffer đầy, bỏ qua dữ liệu cũ nhất
            dataQueue.poll();
            dataQueue.offer(data);
        }
    }

    private void startProcessingThread() {
        new Thread(() -> {
            while (isRunning) {
                try {
                    byte[] data = dataQueue.take(); // Đợi cho đến khi có dữ liệu
                    processData(data);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    private void processData(byte[] data) {
        // Xử lý dữ liệu trong thread riêng
        if (data[0] == 0x02 && data[data.length - 1] == 0x03) {
            List<Float> processedData = new ArrayList<>();
            // ... code xử lý dữ liệu của bạn ...

            // Gửi dữ liệu đã xử lý lên UI với tần suất thấp hơn
//            updateUIWithDelay(processedData);
        }
    }

    public void stop() {
        isRunning = false;
    }
}
