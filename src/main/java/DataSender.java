import java.sql.*;
import java.util.concurrent.LinkedBlockingQueue;

public class DataSender {

    public void sendData(LinkedBlockingQueue<Record> recordList, Connection conn) throws Exception {

        //PreparedStatement
        String sql = "insert into t_client_access(id, ip_address, access_time, parameters) values(?,?,?,?)";

        //create database PreparedStatement
        PreparedStatement pstmt = conn.prepareStatement(sql);
        System.out.println(" Create PreparedStatement successfully£¡");

        for(Record record: recordList){
            int id = record.getId();
            String ip_address = record.getIp_address();
            String access_time = record.getAccess_time();
            String parameters = record.getParameters();

            pstmt.setInt(1, id);
            pstmt.setString(2, ip_address);
            pstmt.setString(3, access_time);
            pstmt.setString(4, parameters);
            pstmt.addBatch();
//            pstmt.executeUpdate();

            recordList.remove(record);
        }

        pstmt.executeBatch();
        pstmt.close();

        System.out.println("Execute successfully!");

        try
        {
            if(null != conn)
            {
                conn.close();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}