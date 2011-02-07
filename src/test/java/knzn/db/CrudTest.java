package knzn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;
import knzn.db.Database;
import knzn.db.DatabaseImpl;

//XXX Make sure tabs are spaces and 2char
public class CrudTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreate() {

		Database db = new DatabaseImpl(DataSourceFactory.createDataSource());

		db.update("create table  ( bar varchar(12))", null);

		// ResultSetHandler<String> result = new ResultSetHandler<String>() {
		//
		// public String handle(ResultSet resultSet) throws SQLException {
		// return resultSet.getString("color");
		// }
		// };

		// List<String> list = db.query("SELECT color FROM colors", result);

	}
	
	public void testSelect(){
		Database db = new DatabaseImpl(DataSourceFactory.createDataSource());

		ResultSetHandler<String> result = new ResultSetHandler<String>() {
			
			public String handle(ResultSet resultSet) throws SQLException {
				return resultSet.getString("bar");
			}
		};
		
		List<String> list = db.query("SELECT bar FROM foo", result);
		
		for(String row : list){
			System.out.println(row);
		}
	}
	
	public void testInsert() {
		Database db = new DatabaseImpl(DataSourceFactory.createDataSource());
		
		db.update("INSERT INTO foo (bar) Values ('Red')", null);	
		db.update("INSERT INTO foo (bar) Values ('Green')", null);	
		db.update("INSERT INTO foo (bar) Values ('Blue')", null);	
		db.update("INSERT INTO foo (bar) Values ('Yellow')", null);	
	}
}
