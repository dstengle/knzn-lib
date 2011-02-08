package knzn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;


//XXX Make sure tabs are spaces and 2char
public class CrudTest extends TestCase {
	private Database db = null;
	
	@Override
	protected void setUp() throws Exception {
		
		if(db == null) {
			db = new DatabaseImpl(DataSourceFactory.createDataSource());
		}
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreate() {
		
		db.update("DROP Table foo IF EXISTS", null);
		db.update("create table foo (color varchar(12))", null);
		
	}
	
	
	public void testInsert() {
		String sql = "INSERT INTO foo (color) Values (?)";
		
		List<String> colors = new ArrayList<String>();
		
		colors.add("Red");
		colors.add("Blue");
		colors.add("Yellow");
		colors.add("Green");
		
		int i = 0;
		for(String item : colors){
			
			Object[] params = {item};
			db.update(sql, params);
			i++;
		}
		
		System.out.println("\nTable Populated");
	}	
	
	public void testSelect(String contains, String notContains){

		ResultSetHandler<String> color = new ResultSetHandler<String>() {
			
			public String handle(ResultSet resultSet) throws SQLException {
				return resultSet.getString("color");
			}
		};
		
		List<String> lstColor = db.query("SELECT color FROM foo", color);
		
		
		if(contains != null) assertTrue(lstColor.contains(contains));
		
		if(notContains != null) assertFalse(lstColor.contains(notContains));
	}
	
	public void testUpdate(){
		String[] params = {"Orange", "Red"};
		db.update("UPDATE foo SET color = ? WHERE color = ?", params);
		
		testSelect("Orange", "Red");
	}
	
	public void testDelete(){
		String[] params = {"Yellow"};
		
		db.update("DELETE FROM foo WHERE color = ?", params);		
		testSelect(null, "Yellow");
	}
	
	public void testTimestamp(){
		db.update("DROP TABLE ts_test IF EXISTS", null);
		db.update("CREATE TABLE ts_test (ts timestamp)", null);
		List<Timestamp> lstTs = new ArrayList<Timestamp>();
		
		for(int i = 0; i < 4; i++){
			Timestamp ts = createTimestamp();
			Object[] params = {ts};
			lstTs.add(ts);
			
			db.update("INSERT INTO ts_test (ts) VALUES (?)", params);
		}
		
		ResultSetHandler<Timestamp> resultset = new ResultSetHandler<Timestamp>() {
			
			public Timestamp handle(ResultSet resultSet) throws SQLException {
				return resultSet.getTimestamp("ts");
			}
		};
		
		List<Timestamp> result = db.query("SELECT ts FROM ts_test", resultset);
		
		for(Timestamp row : result){
			assertTrue(lstTs.contains(row));
		}
		
	}
	
	@SuppressWarnings("deprecation")
	private static Timestamp createTimestamp(){
		Date date = new Date();
		Timestamp ts = new Timestamp(date.getYear(), date.getMonth(), date.getDate(),
				date.getHours(), date.getMinutes(), date.getSeconds(), 0);
		return ts;
	}
}
