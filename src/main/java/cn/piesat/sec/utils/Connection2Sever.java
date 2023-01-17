package cn.piesat.sec.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class Connection2Sever {

    private static final Logger logger = Logger.getLogger(Connection2Sever.class);

    private static String DEFAULTCHARTSET = "UTF-8";
    private static Connection conn;

    //用户名密码方式  远程登录linux服务器
    public static Boolean login(RemoteConnect remoteConnect) {
        boolean flag = false;
        try {
            conn = new Connection(remoteConnect.getIp(),remoteConnect.getPort());
            conn.connect();// 连接
            flag = conn.authenticateWithPassword(remoteConnect.getUserName(), remoteConnect.getPassword());// 认证
            if (flag) {
                logger.info("认证成功！");
            } else {
                logger.info("认证失败！");
                conn.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    //远程执行shell脚本或者命令,命令执行成功后返回的结果值，如果命令执行失败，返回空字符串，不是null
    public static String executeSuccess(String cmd){
        String result = "";
        //int ret=-1;
        try {
            Session session = conn.openSession();// 打开一个会话
            session.execCommand(cmd);// 执行命令
            result = processStdout(session.getStdout(), DEFAULTCHARTSET);
            //ret = session.getExitStatus();
            conn.close();
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }



    //解析脚本执行的返回结果
    public static String processStdout(InputStream in, String charset){
        InputStream stdout = new StreamGobbler(in);
        StringBuffer buffer = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout, charset));
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line + "\n");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    //通过用户名和密码关联linux服务器
    public static String connectLinux(String ip,int port,String userName,String password,String commandStr) {

        String returnStr = "";
        RemoteConnect remoteConnect = new RemoteConnect();
        remoteConnect.setIp(ip);
        remoteConnect.setPort(port);
        remoteConnect.setUserName(userName);
        remoteConnect.setPassword(password);
        try {
            if (login(remoteConnect)) {
                returnStr = executeSuccess(commandStr);
                System.out.println(returnStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnStr;
    }

}
