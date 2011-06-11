package knzn.db;


import org.apache.commons.dbcp.BasicDataSource;

public final class DataSourceFactory{

  private DataSourceFactory() {}
  
	public static BasicDataSource createDataSource() {
		final BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		dataSource.setUrl("jdbc:hsqldb:file:target/hsqldb/testdb");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}
}
