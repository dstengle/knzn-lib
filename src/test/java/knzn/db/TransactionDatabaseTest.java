package knzn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;

public class TransactionDatabaseTest extends TestCase {
	private DatabaseImpl database = null;

	public void testRunTransactionSuccess() throws SQLException{

	  database.runTransaction(new Transaction() {
      public void doInTransaction(final TransactionDatabase tbd) {
        tbd.update("DROP Table trans IF EXISTS", null);
        tbd.update("create table trans (color varchar(12))", null);
      }
    });
	}


	 public void testRunTransactionFailure() throws SQLException{

	    database.runTransaction(new Transaction() {
	      public void doInTransaction(final TransactionDatabase tbd) {
	        tbd.update("DROP Table trans IF EXISTS", null);
	        tbd.update("create table trans (color varchar(12))", null);

	      }
	    });

	    fail();
	  }

	public void testRollback() throws SQLException{
	  final TransactionDatabase tbd = new TransactionDatabase(database);
	  tbd.startTransaction();
	  populateTable(tbd);
	  tbd.commitTransaction();

	  tbd.update("DELETE FROM trans WHERE color = 'Red'", null);
		tbd.rollbackTransaction();
		assertSelect("Red");

		tbd.close();
	}

	public void testInsert() throws SQLException{
	  final TransactionDatabase tbd = new TransactionDatabase(database);
	  tbd.startTransaction();
	  populateTable(tbd);
		tbd.commitTransaction();

		tbd.close();
	}

  private void populateTable(final TransactionDatabase tbd) {
    final String sql = "INSERT INto trans (color) VALUES (?)";
		final String[] name = {"Blue", "Red", "Yellow", "Green"};

		for(final String row : name){
			final String[] params = {row};
			tbd.update(sql, params);
		}
  }

	private void assertSelect(final String contains) throws SQLException{
	  final TransactionDatabase tbd = new TransactionDatabase(database);
	  tbd.startTransaction();
		final ResultSetHandler<String> result = new ResultSetHandler<String>() {

			public String handle(final ResultSet resultSet) throws SQLException {
				return resultSet.getString("color");
			}
		};

		final List<String> color = tbd.query("SELECT color FROM trans", result);

		if(contains != null) {
      assertTrue(color.contains(contains));
    }
		tbd.close();
	}

	@Override
	protected void setUp() throws Exception {
	  super.setUp();
	  if(database == null) {
			database = new DatabaseImpl(DataSourceFactory.createDataSource());
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
