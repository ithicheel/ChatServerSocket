import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;
    private sqlConn conn = null;
    private Statement sqlSt = null;
    public Server(){
        connections = new ArrayList<>();
        this.done = false;
    }
    @Override
    public void run() {
        try {
            System.out.println("111");
            server = new ServerSocket(8081, 1, InetAddress.getByName("127.0.0.1"));
            System.out.println("Server started...");
            System.out.println("Waiting for client...");
            conn = new sqlConn("jdbc:mysql://localhost:3306/javalibrary", "root", "");
            conn.Conn();
            sqlSt = conn.getSt();
            pool = Executors.newCachedThreadPool();
            while(!done){
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }
        } catch (IOException e) {
            // TODO: handle
            shutdown();
        }
    }
    public void broadcast(String from_id, String to_id){
        for (ConnectionHandler ch : connections){
            if(ch != null){
                if(to_id.startsWith("fr")){
                    if(ch.getFrom_id().equals(from_id)){
//                    ch.sendMessage(from_id + "=" + ch.getFrom_id() + "=" + message);
                        System.out.println("?");
                        ch.sendMessage();
                    }
                    if(ch.getFrom_id().equals(to_id)){
//                    ch.sendMessage(from_id + "=" + ch.getFrom_id() + "=" + message);
                        ch.sendMessage();
                    }
                }else if (to_id.startsWith("gp")) {
                    if(ch.getFrom_id().equals(from_id)){
//                    ch.sendMessage(from_id + "=" + ch.getFrom_id() + "=" + message);
                        System.out.println("?");
                        ch.sendMessageGroup();
                    }
                    if(ch.getFrom_id().equals(to_id)){
//                    ch.sendMessage(from_id + "=" + ch.getFrom_id() + "=" + message);
                        ch.sendMessageGroup();
                    }
                }
            }
        }
    }
    public void shutdown(){
        try {
            this.done = true;
            pool.shutdown();
            if (!server.isClosed()){
                server.close();
            }
            for (ConnectionHandler ch : connections){
                ch.shutdown();
            }
            conn.Close();
            sqlSt = null;
        } catch (Exception e){
            // ignore
        }
    }
    class ConnectionHandler implements Runnable {
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String from_id;
        private String to_id;
        public ConnectionHandler(Socket client){
            this.client = client;
        }
        @Override
        public void run() {
            try{
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Please enter a id: ");
                String[] ids = in.readLine().split("=");
                System.out.println(ids);
                this.from_id = ids[0];
                this.to_id = ids[1];
                System.out.println(this.from_id + " connected!");
                if(this.getTo_id().startsWith("fr")){
                    this.sendMessage();
                }else if(this.getTo_id().startsWith("gp")) {
                    this.sendMessageGroup();
                }
                String message;
                while ((message = in.readLine()) != null){
                    if(message.startsWith("/changeToId")){
                        String[] messageSplit = message.split("=", 2);
                        if(messageSplit.length == 2){
                            this.setTo_id(messageSplit[1]);
                            System.out.println("succesfull changed toId to: " + this.getTo_id());
                            if(this.getTo_id().startsWith("fr")){
                                this.sendMessage();
                            }else if(this.getTo_id().startsWith("gp")) {
                                this.sendMessageGroup();
                            }
//                            out.println("Successfully changed toId to : " + this.getTo_id());
                        }else {
                            System.out.println("no to_id provided!");
//                            out.println("No to_id provided!");
                        }
                    }else if (message.startsWith("/quit")){
//                        out.println("left the chat!");
                        System.out.println("left the chat!");
                        shutdown();
                    } else {
                        System.out.println(message);
                        String[] messageSplit = message.split("=");
                        System.out.println("messageSplit: " + messageSplit.length);
                        if(messageSplit[5].startsWith("fr")){
                            insertDB(messageSplit);
                        }else if (messageSplit[5].startsWith("gp")){
                            insertDBGroup(messageSplit);
                        }
                        broadcast(messageSplit[4], messageSplit[5]);
                    }
                }
            } catch (Exception e){
                // TODO: handle
                shutdown();
            }
        }
        public void insertDB(String[] messageSplit){
            String[] to_id = messageSplit[5].split("#");
            String sql = "call javalibrary.sendMessage('" +
                    messageSplit[0] +
                    "','" +
                    messageSplit[1] +
                    "','" +
                    messageSplit[2] +
                    "','" +
                    messageSplit[3] +
                    "','" +
                    messageSplit[4] +
                    "','" +
                    to_id[1] +
                    "')";
            System.out.println(sql);
            try {
                ResultSet resultSet = sqlSt.executeQuery(sql);
                System.out.println("Succesful");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        public void insertDBGroup(String[] messageSplit){
            String[] to_id = messageSplit[5].split("#");
            String sql = "call javalibrary.sendMessageGroup('" +
                    messageSplit[0] +
                    "','" +
                    messageSplit[1] +
                    "','" +
                    messageSplit[2] +
                    "','" +
                    messageSplit[3] +
                    "','" +
                    messageSplit[4] +
                    "','" +
                    to_id[1] +
                    "')";
            System.out.println(sql);
            try {
                ResultSet resultSet = sqlSt.executeQuery(sql);
                System.out.println("Succesful");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        public void sendMessage() {
            String[] to_id = this.getTo_id().split("#");
            String sql = "call javalibrary.FromToMessage('" + this.getFrom_id() + "','" + to_id[1]  + "')";
            ArrayList<String> lists = new ArrayList<>();
            try {
                ResultSet resultSet = sqlSt.executeQuery(sql);
                while (resultSet.next()){
                    ArrayList<String > list = new ArrayList<>();
                    list.add(resultSet.getString("message"));
                    list.add(resultSet.getString("content"));
                    list.add(resultSet.getString("seenDate"));
                    list.add(resultSet.getString("sendDate"));
                    list.add(resultSet.getString("from_user_id"));
                    list.add(resultSet.getString("to_user_id"));
                    String resultList  = "";
                    for (int i = 0; i<list.size(); i++){
                        if(i == list.size()-1){
                            resultList += list.get(i);
                        }else {
                            resultList += list.get(i) + "#";
                        }
                    }
                    lists.add(resultList);
                }
                String resultList = "";
                for (int i = 0; i<lists.size(); i++){
                    if(i == lists.size()-1){
                        resultList += lists.get(i);
                    }else {
                        resultList += lists.get(i) + "=";
                    }
                }
                out.println(resultList);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        public void sendMessageGroup() {
            String[] to_id = this.getTo_id().split("#");
            String sql = "select * from  javalibrary.groupMessage where to_group_id = '"
                    + to_id[1] + "'";
            ArrayList<String> lists = new ArrayList<>();
            try {
                ResultSet resultSet = sqlSt.executeQuery(sql);
                while (resultSet.next()){
                    ArrayList<String > list = new ArrayList<>();
                    list.add(resultSet.getString("message"));
                    list.add(resultSet.getString("content"));
                    list.add(resultSet.getString("seenDate"));
                    list.add(resultSet.getString("sendDate"));
                    list.add(resultSet.getString("from_user_id"));
                    list.add(resultSet.getString("to_group_id"));
                    String resultList  = "";
                    for (int i = 0; i<list.size(); i++){
                        if(i == list.size()-1){
                            resultList += list.get(i);
                        }else {
                            resultList += list.get(i) + "#";
                        }
                    }
                    lists.add(resultList);
                }
                String resultList = "";
                for (int i = 0; i<lists.size(); i++){
                    if(i == lists.size()-1){
                        resultList += lists.get(i);
                    }else {
                        resultList += lists.get(i) + "=";
                    }
                }
                out.println(resultList);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        // getter
        public String getFrom_id() {
            return from_id;
        }
        public String getTo_id() {
            return to_id;
        }
        // setter
        public void setFrom_id(String from_id) {
            this.from_id = from_id;
        }
        public void setTo_id(String to_id) {
            this.to_id = to_id;
        }
        // shutdown
        public void shutdown(){
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
    }

    public static void main(String[] args) {
        System.out.println("dsds");
        Server server = new Server();
        server.run();
    }
}