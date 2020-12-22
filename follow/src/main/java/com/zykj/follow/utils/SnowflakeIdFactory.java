package com.zykj.follow.utils;

import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Set;

/**
 * 主键生成
 *
 * @author : J.Tang
 * @version : v1.0
 * @email : seven_tjb@163.com
 * @create : 2018-03-23 10:35
 **/
@ToString
public class SnowflakeIdFactory {

    private final long twepoch = 1288834974657L;
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static final Logger log = LoggerFactory.getLogger(SnowflakeIdFactory.class);


    public SnowflakeIdFactory(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            //服务器时钟被调整了,ID生成器停止服务.
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }


    @SuppressWarnings("unused")
    public static int getWorkerId() {

        //默认
        int workerId = 9;

        //通过mBeanServer获得端口号
        MBeanServer mBeanServer = null;
        ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
        if (mBeanServers.size() > 0) {
            for (MBeanServer _mBeanServer : mBeanServers) {
                mBeanServer = _mBeanServer;
                break;
            }
        }
        if (mBeanServer == null) {
            log.info("没有发现JVM中关联的MBeanServer.");
            return workerId;
        }
        Set<ObjectName> objectNames = null;
        try {

            objectNames = mBeanServer.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (objectNames == null || objectNames.size() <= 0) {
//            throw new IllegalStateException("没有发现JVM中关联的MBeanServer : "
//                    + mBeanServer.getDefaultDomain() + " 中的对象名称.");
            log.info("没有发现JVM中关联的MBeanServer : "+ mBeanServer.getDefaultDomain() + " 中的对象名称.");
            return workerId;
        }
        try {
            for (ObjectName objectName : objectNames) {
                String protocol = (String) mBeanServer.getAttribute(objectName, "protocol");
                if (protocol.equals("HTTP/1.1")) {
                    int port = (Integer) mBeanServer.getAttribute(objectName, "port");
                    workerId = port%10+1;
                }

            }
        } catch (Exception e) {
            log.error("获取workerId失败");
            return workerId;
        }

        return workerId;
    }


}