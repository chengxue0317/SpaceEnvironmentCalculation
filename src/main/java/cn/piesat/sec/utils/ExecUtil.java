package cn.piesat.sec.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ExecUtil {

    /**
     * 执行不需要返回结果的命令
     * @param command
     */
    public static void execCmdWithoutResult(String command){

        try {
            //注意：第一个空格之后的所有参数都为参数
            CommandLine commandLine = new CommandLine(command);
            DefaultExecutor defaultExecutor = new DefaultExecutor();
            defaultExecutor.setExitValue(1);
            //设置60秒超时时，执行超时60秒后会直接终止
            //不设置超时
            ExecuteWatchdog executeWatchdog = new ExecuteWatchdog(-1);
            defaultExecutor.setWatchdog(executeWatchdog);
            DefaultExecuteResultHandler defaultExecuteResultHandler = new DefaultExecuteResultHandler();
            defaultExecutor.execute(commandLine,defaultExecuteResultHandler);
            //命令执行返回前一直阻塞
            defaultExecuteResultHandler.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 带返回结果的命令执行
     * @param command
     * @return
     */
    public static String execCmdWithResult(String command){

        try {
            //接收正常结果流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //接收异常结果流
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            CommandLine commandLine = CommandLine.parse(command);
            DefaultExecutor defaultExecutor = new DefaultExecutor();
            defaultExecutor.setExitValues(null);
            //不设置超时
            ExecuteWatchdog executeWatchdog = new ExecuteWatchdog(-1);
            defaultExecutor.setWatchdog(executeWatchdog);
            PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(outputStream, errorStream);
            defaultExecutor.setStreamHandler(pumpStreamHandler);
            defaultExecutor.execute(commandLine);
            //不同操作系统注意编码，否则结果乱码
            String out = outputStream.toString("GBK");
            String error = errorStream.toString("GBK");
            return out+error;
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    /**
     * 带返回结果的命令执行
     * @param command
     * @return
     */
    public static void execAsync(String command) {

        try {
            //接收正常结果流
            ByteArrayOutputStream susStream = new ByteArrayOutputStream();
            //接收异常结果流
            ByteArrayOutputStream errStream = new ByteArrayOutputStream();
            CommandLine commandLine = CommandLine.parse(command);
            DefaultExecutor exec = new DefaultExecutor();

            PumpStreamHandler streamHandler = new PumpStreamHandler(susStream, errStream);
            exec.setStreamHandler(streamHandler);
            ExecuteResultHandler erh = new ExecuteResultHandler() {
                @Override
                public void onProcessComplete(int exitValue) {
                    try {
                        String suc = susStream.toString("GBK");
                        System.out.println(suc);
                        System.out.println("3. 异步执行完成");
                    } catch (UnsupportedEncodingException uee) {
                        uee.printStackTrace();
                    }
                }
                @Override
                public void onProcessFailed(ExecuteException e) {
                    try {
                        String err = errStream.toString("GBK");
                        System.out.println(err);
                        System.out.println("3. 异步执行出错");
                    } catch (UnsupportedEncodingException uee) {
                        uee.printStackTrace();
                    }
                }
            };
            System.out.println("1. 开始执行");
            exec.execute(commandLine, erh);
            System.out.println("2. 做其他操作");
            // 避免主线程退出导致程序退出
            //Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
