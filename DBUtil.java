package DAO;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import MyException.AppException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//获取数据库链接
public class DBUtil {
    private static final String URL="jdbc:mysql://127.0.0.1:3306/image_server?&useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USERNAME="root";
    private static final String PASSWORD="";

    private static volatile DataSource dataSource=null;

    //创建dataSource实例---双重校验锁(线程安全)
    public static DataSource getDataSource(){
         if(dataSource==null) {
             synchronized (DBUtil.class) {
                 if (dataSource==null) {
                     dataSource = new MysqlDataSource();
                     MysqlDataSource tmpDataSource=(MysqlDataSource) dataSource;
                     //将链接属性设置到连接池中
                     tmpDataSource.setUrl(URL);
                     tmpDataSource.setUser(USERNAME);
                     tmpDataSource.setPassword(PASSWORD);

                 }
             }
         }
         return dataSource;
    }

    //获取连接
    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            throw new AppException("DB001","数据库连接出错",e);
        }
    }

    //关闭连接
    public static void close(Connection connection, PreparedStatement statement, ResultSet resultSet)  {
        //注意关闭顺序：先创建的后关闭
        try {
            if (resultSet!=null){
                resultSet.close();
            }
            if (statement!=null){
                statement.close();
            }
            if (connection!=null){
                connection.close();
            }
        } catch (SQLException e) {
            throw new AppException("DB002","数据库资源释放出错",e);
        }
    }

}
