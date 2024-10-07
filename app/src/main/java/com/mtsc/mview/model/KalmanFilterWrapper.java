package com.mtsc.mview.model;

public class KalmanFilterWrapper {
    private double state;
    private double covariance;
    private double[][] A;
    private double[][] B;
    private double[][] C;
    private double[][] Q;
    private double[][] R;

    public KalmanFilterWrapper() {
        // Khởi tạo các thuộc tính
        state = 0;
        covariance = 1;
        A = new double[][]{{1, 1}};
        B = new double[][]{{0}};
        C = new double[][]{{1}};
        Q = new double[][]{{0.001}};
        R = new double[][]{{0.01}};
    }

    public void update(double measurement) {
        // Dự đoán trạng thái mới
        double predictedState = A[0][0] * state + B[0][0] * measurement;

        // Dự đoán phương sai mới
        double predictedCovariance = A[0][0] * covariance * A[0][0] + Q[0][0];

        // Cập nhật trạng thái dựa trên giá trị đo lường
        double gain = predictedCovariance * C[0][0] / (C[0][0] * predictedCovariance * C[0][0] + R[0][0]);
        state = predictedState + gain * (measurement - C[0][0] * predictedState);

        // Cập nhật phương sai
        covariance = (1 - gain * C[0][0]) * predictedCovariance;
    }

    public Float getFilteredValue() {
        return (float)state;
    }
}
