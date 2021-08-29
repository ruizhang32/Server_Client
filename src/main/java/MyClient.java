import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

public class MyClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        new MyClient("127.0.0.1",8001);
    }

    private Socket socket;
    private InputStream ips;
    private BufferedReader br;
    private OutputStream ops;
    private DataOutputStream dos;
    String firstSend = "hello.html";
    String secondSend = "HelloWorld.action";

    public MyClient(String IPAddress, int port) throws IOException {
        socket = new Socket(InetAddress.getByName(IPAddress), port);
        ips = socket.getInputStream();
        br = new BufferedReader(new InputStreamReader(ips));
        ops = socket.getOutputStream();
        dos = new DataOutputStream(ops);

        //client send hello.html
        System.out.println("I am sending " + firstSend);
        dos.writeBytes(firstSend + System.getProperty("line.separator"));

        //client send HelloWorld.action
        System.out.println("I am sending " +secondSend);
        dos.writeBytes(secondSend + System.getProperty("line.separator"));

        //client saves server response as 'hello.html'
        String line;
        int i = 0;
        while((line = br.readLine())!=null) {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("c:/temp/hello" + i++ +".html")));
            System.out.println("Server said: " + line);
            bw.write(line);
            bw.newLine();
            bw.close();
        }

        dos.close();
        br.close();
        ips.close();
        ops.close();
        socket.close();

    }
}