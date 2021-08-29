import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;

public class DruidFactory {
	private static DruidDataSource dataSource = null;

	public static void init() throws Exception {
		
		dataSource = new DruidDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver"); 
		dataSource.setUsername("root");
		dataSource.setPassword("123456");
		dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/mysql");
		dataSource.setInitialSize(5);
		dataSource.setMinIdle(1); 
		dataSource.setMaxActive(10); 
		// 启用监控统计功能 dataSource.setFilters("stat");// 
	}
	
	public static Connection getConnection() throws Exception {
		if(null == dataSource)
		{
			init();
			System.out.println("连接池构建成功!");
		}
        return dataSource.getConnection();
    }
}
