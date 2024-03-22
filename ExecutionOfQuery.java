package ExecutionOfQuery;

import java.math.BigInteger;
import java.sql.*;

public class ExecutionOfQuery {

    public static BigInteger getAndUpdateCounterByValue(String host,String port, String database,String user, String pass, String name) {
        BigInteger tempCounter = new BigInteger("0");

        Connection con = null;
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+"?useTimezone=true&serverTimezone=GMT&useSSL=false",user,pass);
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("xyz");
            while(resultSet.next()) {
                tempCounter = new BigInteger(resultSet.getString(1));
            }
            con.close();
        }
        catch(Exception e) {
            System.out.println(e);
        }
        finally {
            if (con != null)
                try {
                    con.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }

        return tempCounter;
    }

    public static BigInteger getAndUpdateCounterByValue(String host,String port, String database,String user, String pass, String name,Connection con) {
        BigInteger tempCounter = new BigInteger("0");

        //  Connection con = null;
        try {

            //  Class.forName("com.mysql.cj.jdbc.Driver");
            //   con = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+"?useTimezone=true&serverTimezone=GMT&useSSL=false",user,pass);
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("xyz");
            while(resultSet.next()) {
                tempCounter = new BigInteger(resultSet.getString(1));
            }
            // con.close();
        }
        catch(Exception e) {
            System.out.println(e);
        }

        return tempCounter;
    }

}
