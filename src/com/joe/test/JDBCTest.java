package com.joe.test;

import com.joe.jdbc.JDBCTools;
import org.junit.Test;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author maqiao
 * @create create on 2018-10-10 15:35
 * @desc
 **/
public class JDBCTest {

    @Test
    public void testResultSet() {

        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            conn = JDBCTools.getConnection();
            statement = conn.createStatement();

            String sql = "select id,name,email,birth " +
                    "from customers";

            rs = statement.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt(1);
                String email = rs.getString("email");
                String name = rs.getString("name");
                Date birth = rs.getDate("birth");

                System.out.println(id);
                System.out.println(email);
                System.out.println(name);
                System.out.println(birth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCTools.release(rs, statement, conn);
        }


    }

    /**
     * 执行通用的增删改操作
     * version 1.0
     */
    public void update(String sql) {
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

    @Test
    public void testStatement() throws Exception {
        Connection conn = null;
        Statement statement = null;

        try {
            conn = getConnection2();

            String sql = "INSERT INTO customers(NAME,EMAIL,BIRTH) VALUES ('bbs','atguigu@132.com','2013-2-1')";

            statement = conn.createStatement();
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }


        }


    }

    @Test
    public void testDriverManager() throws Exception {
        String driverClass = null;
        String jdbcUrl = null;
        String user = null;
        String password = null;

        InputStream in = getClass().getClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(in);
        driverClass = properties.getProperty("driver");
        jdbcUrl = properties.getProperty("jdbcUrl");
        user = properties.getProperty("user");
        password = properties.getProperty("password");

        //加载驱动（注册 对应的Driver 实现类中有注册驱动的静态代码块。）
        // DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
        Class.forName(driverClass);
        Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
        System.out.println(connection);

    }

    @Test
    public void testDriver() throws SQLException {
        Driver driver = new com.mysql.jdbc.Driver();

        String url = "jdbc:mysql://127.0.0.1:3306/test";
        Properties info = new Properties();
        info.put("user", "root");
        info.put("password", "root");

        Connection connection = driver.connect(url, info);
        System.out.println(connection);
    }

    @Test
    public void testGetConnection2() throws Exception {
        System.out.println(getConnection2());
    }

    /**
     * 使用driverManager来获取数据库连接
     *
     * @return
     * @throws Exception
     */
    public Connection getConnection2() throws Exception {
        Properties properties = new Properties();
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");

        properties.load(in);

        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        String jdbcUrl = properties.getProperty("jdbcUrl");
        String driver = properties.getProperty("driver");

        Class.forName(driver);

        return DriverManager.getConnection(jdbcUrl, user, password);

    }

    /**
     * 直接用driver（驱动来获取数据库连接）
     *
     * @return
     * @throws Exception
     */
    public Connection getConnection() throws Exception {
        String driverClass = null;
        String jdbcUrl = null;
        String user = null;
        String password = null;

        InputStream in = getClass().getClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(in);
        driverClass = properties.getProperty("driver");
        jdbcUrl = properties.getProperty("jdbcUrl");
        user = properties.getProperty("user");
        password = properties.getProperty("password");

        Driver driver = (Driver) Class.forName(driverClass).newInstance();
        Properties info = new Properties();
        info.put("user", user);
        info.put("password", password);
        Connection connection = driver.connect(jdbcUrl, info);

        return connection;
    }

    @Test
    public void testGetConnection() throws Exception {
        Connection connection = getConnection();
        System.out.println(connection);
    }
}
