package knzn.db;

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

	public void testInsert() {
		
	}
	public void testUpdate() {
		
	}
	
	public void testDelete() {
		
	}
	
	
	public void testCreate() {
		System.out.println("hello world");

		Database db = new DatabaseImpl(DataSourceFactory.createDataSource());

		db.update("create table foo( bar varchar(12))", null);

		// ResultSetHandler<String> result = new ResultSetHandler<String>() {
		//
		// public String handle(ResultSet resultSet) throws SQLException {
		// return resultSet.getString("color");
		// }
		// };

		// List<String> list = db.query("SELECT color FROM colors", result);

	}
}
