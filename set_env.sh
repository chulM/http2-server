#!/usr/bin/env bash
HOST_NAME=$(curl --connect-timeout 1 --max-time 1 http://169.254.169.254/latest/meta-data/instance-id)
HOST_IP=$(curl --connect-timeout 1 --max-time 1 http://169.254.169.254/latest/meta-data/local-ipv4)
APP_LOG_PATH=/prod/logs/bootapp
export JAVA_OPTS="$JAVA_OPTS \
    "${JVM_MEMORY}" \
    -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:G1HeapRegionSize=8m -XX:+ParallelRefProcEnabled -XX:-ResizePLAB \
    -verbose:gc -Xloggc:${APP_LOG_PATH}/gc/gc.`date '+%Y%m%d%H%M'`.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps \
    -XX:+PrintClassHistogramAfterFullGC -XX:+PrintClassHistogramBeforeFullGC \
    -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${APP_LOG_PATH}/gc/heapdump_`date '+%Y%m%d%H%M'`.hprof"