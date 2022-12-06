import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client1 implements Runnable{
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;
    @Override
    public void run() {
        try {
            client = new Socket("127.0.0.1", 8081);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            InputHandler inputHandler = new InputHandler();
            Thread t = new Thread(inputHandler);
            t.start();
            String inMessage;
            while((inMessage = in.readLine()) != null){
                System.out.println("Server to message: " + inMessage);
            }
        } catch (Exception e){
            // TODO: handle
            shutdown();
        }
    }
    public void shutdown(){
        done = true;
        try {
            in.close();
            out.close();
            if(!client.isClosed()){
                client.close();
            }
        } catch (Exception e){
            // ignore
        }
    }
    class InputHandler implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                String id = "1234zsdlk32333=zxcv1234zxcv1234";
                String info = "=dsdsds=2022-06-23 14:50:30=2022-06-23 14:50:30=1234zsdlk32333=zxcv1234zxcv1234";
                out.println(id);
                while(!done){
                    String message = inReader.readLine();
                    if(message.startsWith("/quit")){
                        out.println(message+ "=" +id);
                        inReader.close();
                        shutdown();
                    }if(message.startsWith("/changeToId")){
                        out.println(message);
                    } else {
                        out.println(message + info);
                    }
                }
            } catch (Exception e){
                shutdown();
            }
        }
    }
    public static void main(String[] args) {
        Client1 client = new Client1();
        client.run();
    }
}