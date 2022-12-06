import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class sqlConn {
    private String url;
    private String name;
    private String password;
    private Connection conn = null;
    private Statement st = null;
    public sqlConn(String url , String name ,String password){
        this.url = url;
        this.name = name;
        this.password = password == "" ? "" : password;
    }
    public void Conn(){
        try {
            this.conn = DriverManager.getConnection(url, name ,password);
            this.st = this.conn.createStatement();
            System.out.println("sql connected..");
        } catch (Exception e){
            System.out.println("SQL connection aldaa garlaa..");
        }
    }
    public Statement getSt() {
        return st;
    }

    public void Close() {
        try {
            st.close();
            conn = null;
//            System.out.println("SQLconn close");
        } catch (Exception e){
            System.out.println("SQL connection haahad aldaa garlaa..");
        }
    }
}

