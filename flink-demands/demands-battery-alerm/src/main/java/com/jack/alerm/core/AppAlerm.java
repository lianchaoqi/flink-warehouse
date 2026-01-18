package com.jack.alerm.core;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: flink-warehouse
 * @BelongsPackage: com.jack.alerm.core
 * @Author: lianchaoqi
 * @CreateTime: 2026-01-18  22:30
 * @Description: ~~~~
 * @Version: jdk1.8
 */
public class AppAlerm {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
//        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        DataStream<String> socketStream = env.socketTextStream("localhost", 9999);

        socketStream.print();
//        DataStream<BatteryEvent> events = socketStream.map(line -> {
//            String[] p = line.split(",");
////            return new BatteryEvent(p[0], Long.parseLong(p[1]), Double.parseDouble(p[2]), ...);
//        });

        env.execute();

    }
}
