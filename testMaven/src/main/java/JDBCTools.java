import java.io.InputStream;
import java.sql.*;
import java.util.*;

/**
 * @author maqiao
 * @create create on 2018-10-11 16:16
 * @desc 操作JDBC的工具方法 封装了一些工具方法
 * version 1.0
 **/
public class JDBCTools {

    /**
     * 释放数据库connection 、 statement 、 resultset的方法
     *
     * @param statement
     * @param conn
     */
    public static void release(ResultSet rs, Statement statement, Connection conn) {

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 释放数据库connection 和 statement 的方法
     *
     * @param statement
     * @param conn
     */
    public static void release(Statement statement, Connection conn) {

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获取数据库连接的方法
     * 通过配置文件从数据库中获取一个连接
     *
     * @return
     * @throws Exception
     */
    public static Connection getConnection() throws Exception {
        Properties properties = new Properties();
        InputStream in = JDBCTools.class.getClassLoader().getResourceAsStream("jdbc.properties");

        properties.load(in);

        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String jdbcUrl = properties.getProperty("jdbcUrl");
        String driver = properties.getProperty("driver");

        Class.forName(driver);

        return DriverManager.getConnection(jdbcUrl, user, password);

    }

    /**
     * 执行通用的增删改操作
     * version 1.0
     */
    public static void update(String sql) {
        Connection conn = null;
        Statement statement = null;

        try {
            conn = JDBCTools.getConnection();
            statement = conn.createStatement();
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.release(statement, conn);
        }
    }

    /**
     * 通用的查询方法,利用反射返回对应的单个对象
     *
     * @param sql
     * @param clazz
     * @param args
     * @param <T>
     * @return
     */
    public static <T> T get(String sql, Class<T> clazz, Object... args) {

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        T entity = null;

        try {
            conn = getConnection();
            preparedStatement = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setObject(i + 1, args[i]);
            }

            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            Map<String, Object> map = new HashMap<>();

            if (resultSet.next()) {

                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String columnLabel = metaData.getColumnLabel(i + 1);
                    Object object = resultSet.getObject(i + 1);
                    map.put(columnLabel, object);
                }

                if (map.size() > 0) {
                    entity = clazz.newInstance();

                    for (String key : map.keySet()) {
                        ReflectionUtils.setFieldValue(entity, key, map.get(key));
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            release(resultSet, preparedStatement, conn);
        }

        return entity;
    }

    /**
     * 通用的查询方法,利用反射返回对应的单个对象
     *
     * @param sql
     * @param clazz
     * @param args
     * @param <T>
     * @return
     */
    public static <T> List<T> getClasses(String sql, Class<T> clazz, Object... args) {

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<T> entities = null;

        try {
            conn = getConnection();
            preparedStatement = conn.prepareStatement(sql);
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    preparedStatement.setObject(i + 1, args[i]);
                }
            }

            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            Map<String, Object> map = new HashMap<>();
            entities = new ArrayList<>();

            while (resultSet.next()) {

                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String columnLabel = metaData.getColumnLabel(i + 1);
                    Object object = resultSet.getObject(i + 1);
                    map.put(columnLabel, object);
                }

                if (map.size() > 0) {
                    T entity = clazz.newInstance();
                    for (String key : map.keySet()) {
                        ReflectionUtils.setFieldValue(entity, key, map.get(key));
                    }
                    entities.add(entity);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            release(resultSet, preparedStatement, conn);
        }

        return entities;
    }

    public static void main(String[] args) {
        String sql = "select ID id,NAME name,EMAIL email,BIRTH birth from customers where id = ?";
//        Customer customer = get(sql, Customer.class, 1);
//        System.out.println(customer);
        List<Customer> customers = getClasses(sql, Customer.class, 1);
        System.out.println(customers);

    }

}
