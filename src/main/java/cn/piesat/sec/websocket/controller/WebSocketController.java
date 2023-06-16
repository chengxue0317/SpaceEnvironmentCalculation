package cn.piesat.sec.websocket.controller;

import cn.piesat.sec.websocket.util.TailfLogThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@ServerEndpoint("/sec/log/{logPath}")
@RestController
@Component
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private Process process;
    private InputStream inputStream;


    private static String model;
    @Value("${python.data.model}")
    public void setModel(String value){
        model=value;
    }


    /**
     * 新的WebSocket请求开启
     */
    @OnOpen
    public void onOpen(@PathParam("logPath") String logPath, Session session) {
        try {
            System.out.println(model);
            String command = "tail -f ".concat(model).concat("log").concat(File.separator).concat(logPath).concat(".log");
            log.info("查看日志命令执行：{}",command);
            process = Runtime.getRuntime().exec(command);
            inputStream = process.getInputStream();
            TailfLogThread thread = new TailfLogThread(inputStream, session);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * WebSocket请求关闭
     */
    @OnClose
    public void onClose() {
        try {
            if(inputStream != null){
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(process != null){
            process.destroy();
        }
    }

    @OnError
    public void onError(Throwable thr) {
        thr.printStackTrace();
    }
}
