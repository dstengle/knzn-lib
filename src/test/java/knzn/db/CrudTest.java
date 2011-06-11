package knzn.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CrudTest {

  private static final Logger LOGGER = Logger.getLogger(CrudTest.class
          .getSimpleName());

  private static Timestamp createTimestamp() {
    final Calendar cal = Calendar.getInstance();
    return new Timestamp(cal.getTimeInMillis());
  }

  private DatabaseImpl database = null;

  @Test
  public void testCreate() {
    try {
      database.update("DROP Table foo IF EXISTS", null);
      database.update("create table foo (color varchar(12), test varchar(20))",
              null);
    } catch (Exception e) {
      e.printStackTrace();
      fail("testCreate failed.");
    }

  }

  @Test
  public void testDelete() {
    final String colorParam = "Yellow";

    final String[] params = {colorParam};

    database.update("DELETE FROM foo WHERE color = ?", params);
    assertSelect(null, colorParam);
  }

  @Test
  public void testInsert() {
    final String sql = "INSERT INTO foo (color, test) Values (?, ?)";

    final List<String> colors = new ArrayList<String>();

    colors.add("Red");
    colors.add("Blue");
    colors.add("Yellow");
    colors.add("Green");

    for (final String item : colors) {
      final Object[] params = {item, null};
      database.update(sql, params);
    }

    assertSelect("Red", null);
    LOGGER.info("\nTable Populated");
  }

  @Test
  public void testTimestamp() {
    database.update("DROP TABLE ts_test IF EXISTS", null);
    database.update("CREATE TABLE ts_test (ts timestamp)", null);

    final List<Timestamp> lstTs = new ArrayList<Timestamp>();

    for (int i = 0; i < 4; i++) {
      final Timestamp timestamp = createTimestamp();
      final Object[] params = {timestamp};
      lstTs.add(timestamp);

      database.update("INSERT INTO ts_test (ts) VALUES (?)", params);
    }

    final ResultSetHandler<Timestamp> resultset = new ResultSetHandler<Timestamp>() {

      public Timestamp handle(final ResultSet resultSet) throws SQLException {
        return resultSet.getTimestamp("ts");
      }
    };

    final List<Timestamp> result = database.query("SELECT ts FROM ts_test",
            resultset);

    for (final Timestamp row : result) {
      assertTrue("row failed:" + row, lstTs.contains(row));
    }

  }

  @Test
  public void testRollback() throws SQLException {
    database.runTransaction(new Transaction() {

      public void doInTransaction(final TransactionDatabase database)
              throws SQLException {
        database.startTransaction();
        database.update("DELETE FROM foo WHERE color = ?",
                new String[]{"Green"});

        throw new SQLException("Rollback Test Exception");
      }
    });
    assertSelect("Green", null);
  }

  @Test
  public void testUpdate() {
    final String[] params = {"Orange", "Red"};
    database.update("UPDATE foo SET color = ? WHERE color = ?", params);

    final String[] newParams = {"Color", "Not a Color"};
    database.update("UPDATE foo SET color = ? WHERE color = ?", newParams);
    assertSelect("Orange", "Red");
  }

  @Before
  public void setUp() throws Exception {

    if (database == null) {
      database = new DatabaseImpl(DataSourceFactory.createDataSource());
    }
  }

  private void assertSelect(final String contains, final String notContains) {

    final ResultSetHandler<String> color = new ResultSetHandler<String>() {

      public String handle(final ResultSet resultSet) throws SQLException {
        return resultSet.getString("color");
      }
    };

    final List<String> lstColor = database
            .query("SELECT color FROM foo", color);

    if (contains != null) {
      assertTrue("color missing: " + contains, lstColor.contains(contains));
    }

    if (notContains != null) {
      assertFalse("color should be missing: " + notContains, 
              lstColor.contains(notContains));
    }
  }

}
