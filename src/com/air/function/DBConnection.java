package com.air.function;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

	public DBConnection(){
		
	}
	
	public Connection getConnection(){
		
		Connection con = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Properties property=new Properties();
			property.setProperty("user", "root");
			property.setProperty("password", "abcd@123");
			property.setProperty("characterEncoding", "utf-8");
			con=DriverManager.getConnection("jdbc:mysql://localhost/metal", property);
			System.out.println("数据库连接完成！");
			
		}  catch (SQLException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		
		return con;
		
		
	}
	
}
