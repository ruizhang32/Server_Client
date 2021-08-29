import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class MyServer {

    public static void main(String[] args) throws Exception {
        MyServer myServer = new MyServer(8001);
        myServer.endServer();
    }

    private ThreadPoolExecutor executor;
    private ServerSocket serverSocket;
    private LinkedBlockingQueue<Record> recordList;
    private Socket socket;
    private HashMap<String, String> myMap;
    private Connection conn;

    private MyServer(int port) throws Exception {

        serverSocket =new ServerSocket(port);
        recordList = new LinkedBlockingQueue<>();
        myMap = new MapReader().readMap();

        //create thread pool executor
        executor=(ThreadPoolExecutor) Executors.newCachedThreadPool();

        //connect to DruidFactory
        try {
            conn = DruidFactory.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DruidFactory connected!");

        //server records clients visiting data using thread
        Thread threadClientRecord = new Thread(new clientRecord());
        threadClientRecord.setDaemon(true);
        threadClientRecord.start();

        while(true){
            try {
                socket = serverSocket.accept();
                System.out.println("New Communication socket for " + socket.getLocalAddress().getHostName() +" started");
                Task client = new Task(socket);//create new task
                submitTask(client);  //sumbit tasks to thread pool executor
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class Task implements Runnable{
        private InputStream ips;
        private BufferedReader br;
        private OutputStream ops;
        private DataOutputStream dos;
        private Socket socket;
        private String response;
        private int id = 0;

        Task(Socket socket) throws IOException {
            this.socket = socket;
            ips = socket.getInputStream();    //open input stream
            br = new BufferedReader(new InputStreamReader(ips));//wrap input stream reader as buffer reader to make it more efficient
            ops = socket.getOutputStream();  //open output stream
            dos = new DataOutputStream(ops);//wrap output stream to output any type data
        }

            @Override
            public void run() {
                while(true){
                    try {
                        id += 1;
                        String obj = br.readLine();
                        System.out.println("Received message from client " + ips + " from " + socket.getLocalAddress().getHostName() + " at " + LocalDateTime.now());//接受到客户信息
                        recordList.add(new Record(id, socket.getLocalAddress().toString(),LocalDateTime.now().toString(),obj));
                        response = myMap.get(obj);

                        //if server's response ends with 'html', read this file and return it to client
                        if(response.endsWith("html")){
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("hello-server.html")));
                            String line;
                            while ((line = bufferedReader.readLine()) != null){
                                System.out.println("server said:" + line);
                                dos.writeBytes(line + System.getProperty("line.separator"));
                            }
//                            dos.writeBytes(obj + "---->" + response + System.getProperty("line.separator"));
                        }

                        //if server's response ends with 'class', execute 'HelloWorld.class' using javExec()
                        else if (response.endsWith("class")){
                            String className = response.split("\\.")[0];
                            String javaExecResult = javaExec(className);
                            System.out.println("Server said:" + javaExecResult);
                            dos.writeBytes(javaExecResult + System.getProperty("line.separator"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    //submit task to thread pool
    private void submitTask(Task task){
        System.out.printf("Server: A new task has arrived\n");
        executor.execute(task); //execute

        System.out.printf("Server: Pool Size: %d\n",executor.getPoolSize());
        System.out.printf("Server: Active Count: %d\n",executor.getActiveCount());
        System.out.printf("Server: Completed Tasks: %d\n",executor.getCompletedTaskCount());
    }

    private void endServer() {
        executor.shutdown();
    }

    private String javaExec(String className) {
        Process p;
        String result = null;
        String[] cmds = new String[2];
        cmds[0] = "java";
        cmds[1] = className;

        try {
            // 执行命令
            p = Runtime.getRuntime().exec(cmds, null, new File("c:/temp"));

            InputStream fis = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            result = br.readLine();

            System.out.println("");
            int exitVal = p.waitFor(); //获取进程最后返回状态
            System.out.println("Process exitValue: " + exitVal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private class clientRecord implements Runnable{
        @Override
        public void run() {

            DataSender dataSender = new DataSender();

            while(true){
                try {
                    if(recordList.size() > 0){
                        dataSender.sendData(recordList,conn);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
