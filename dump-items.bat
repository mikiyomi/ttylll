@echo off
@title Dump
set CLASSPATH=.;dist\Xephyr.jar;dist\mina-core.jar;dist\slf4j-api.jar;dist\slf4j-jdk14.jar;dist\mysql-connector-java-bin.jar
java -server -Dnet.sf.odinms.wzpath=wz tools.wztosql.DumpItems
pause