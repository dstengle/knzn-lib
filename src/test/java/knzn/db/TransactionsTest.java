package knzn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;

public class TransactionsTest extends TestCase {
	private TransactionDatabase tdb = null;
	
	@Override
	protected void setUp() throws Exception {
		if(tdb == null) {
			DatabaseImpl db = new DatabaseImpl(DataSourceFactory.createDataSource());
			tdb = new TransactionDatabase(db);
		}
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreate(){
		tdb.update("DROP Table trans IF EXISTS", null);
		tdb.update("create table trans (color varchar(12))", null);
	}
	
	public void testInsert() throws SQLException{
		String sql = "INSERT INto trans (color) VALUES (?)";
		String[] name = {"Blue", "Red", "Yellow", "Green"};
		
		for(String row : name){
			String[] params = {row};
			tdb.update(sql, params);
		}
		tdb.commitTransaction();
	}
	
	public void testSelect(String contains){
		ResultSetHandler<String> result = new ResultSetHandler<String>() {
			
			public String handle(ResultSet resultSet) throws SQLException {
				return resultSet.getString("color");
			}
		};
		
		List<String> color = tdb.query("SELECT color FROM trans", result);
		
		if(contains != null) assertTrue(color.contains(contains));
	}
	
	public void testDelete() throws SQLException{
		tdb.update("DELETE FROM trans WHERE color = 'Red'", null);
		
		tdb.rollbackTransaction();
		testSelect("Red");
	}
}
