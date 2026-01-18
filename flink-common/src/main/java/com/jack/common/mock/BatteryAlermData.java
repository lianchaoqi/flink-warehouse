package com.jack.common.mock;

/**
 * @BelongsProject: flink-warehouse
 * @BelongsPackage: com.jack.common.mock
 * @Author: lianchaoqi
 * @CreateTime: 2026-01-18  22:32
 * @Description: ~~~~
 * @Version: jdk1.8
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
public class BatteryAlermData {
    // 配置项
    private static final String FILE_PATH = "battery_data.csv";
    private static final int PORT = 9999;
    private static final int SLEEP_MS = 100000000; // 每条数据间隔时间

    public static void main(String[] args) throws IOException, InterruptedException {
        // 你可以手动切换模式: true 为写入文件, false 为启动 Socket 端口
        boolean writeToFile = false;

        if (writeToFile) {
            generateToCSV(100000000); // 模拟生成1000条数据
        } else {
            generateToSocket();
        }
    }

    /**
     * 核心逻辑：生成模拟数据字符串（CSV格式）
     */
    private static String produceData(String batteryId, double lastTemp, double lastSoc) {
        Random r = new Random();
        long timestamp = System.currentTimeMillis();

        // 模拟物理逻辑
        double current = -30.0 + r.nextDouble() * 60.0; // -30A 到 30A
        double temp = lastTemp + (Math.abs(current) * 0.05) + r.nextDouble(); // 电流大温升快
        double soc = Math.max(0, Math.min(100, lastSoc + (current * 0.01)));
        double voltage = 60.0 + (soc / 100 * 22) + r.nextGaussian();
        double lon = 121.47 + (r.nextDouble() * 0.02);
        double lat = 31.23 + (r.nextDouble() * 0.02);
        int status = (temp > 55.0) ? 1 : 0; // 超过55度告警

        // 格式: 电池ID,时间戳,温度,电压,电流,电量,经度,纬度,状态
        return String.format("%s,%d,%.2f,%.2f,%.2f,%.2f,%.6f,%.6f,%d",
                batteryId, timestamp, temp, voltage, current, soc, lon, lat, status);
    }

    /**
     * 模式1：追加写入 CSV 文件
     */
    public static void generateToCSV(int count) throws IOException {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(FILE_PATH, true)))) {
            double temp = 25.0;
            double soc = 80.0;
            for (int i = 0; i < count; i++) {
                String line = produceData("BATT-001", temp, soc);
                pw.println(line);
                // 更新状态以便下一条数据连续
                temp = Double.parseDouble(line.split(",")[2]);
                soc = Double.parseDouble(line.split(",")[5]);
                if (i % 100 == 0) System.out.println("已写入 " + i + " 条");
            }
        }
        System.out.println("数据生成完毕，路径: " + FILE_PATH);
    }

    /**
     * 模式2：模拟 Socket 服务端
     */
    public static void generateToSocket() throws IOException, InterruptedException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Socket 服务端已启动，端口: " + PORT);
        System.out.println("请在 Flink 中连接此端口...");

        try (Socket client = server.accept();
             PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true)) {

            System.out.println("Flink 已连接！开始发送实时数据...");
            double temp = 30.0;
            double soc = 70.0;

            while (true) {
                String line = produceData("BATT-REALTIME", temp, soc);
                out.println(line);

                // 更新迭代值
                String[] parts = line.split(",");
                temp = Double.parseDouble(parts[2]);
                soc = Double.parseDouble(parts[5]);

                // 如果温度太高，模拟冷却降温
                if (temp > 70) temp = 35.0;

                Thread.sleep(SLEEP_MS);
            }
        }
    }
    }