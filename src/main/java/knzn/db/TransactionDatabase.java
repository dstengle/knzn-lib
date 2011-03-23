package knzn.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TransactionDatabase implements Database{


  private Connection connection;
  private final DatabaseImpl database;
  private boolean autoCommit;

  public TransactionDatabase(final DatabaseImpl database) throws SQLException {
    this.database = database;
  }

  public <T> List<T> query(final String sql, final Object[] params,
          final ResultSetHandler<T> resultSetHandler) {
    return database.query(connection, sql, params, resultSetHandler);
  }

  public <T> List<T> query(final String sql, final ResultSetHandler<T> resultSetHandler) {
    return database.query(connection, sql, null, resultSetHandler);
  }

  public int update(final String sql, final Object[] params) {
    return database.update(connection, sql, params);
  }

  protected void startTransaction() throws SQLException {
    connection = database.getConnection();
    autoCommit = connection.getAutoCommit();
    connection.setAutoCommit(false);
  }

  protected void commitTransaction() throws SQLException {
    connection.commit();
  }

  protected void rollbackTransaction() throws SQLException  {
    connection.rollback();
  }

  protected void close() throws SQLException {
    connection.setAutoCommit(autoCommit);
    database.close(null, null, connection);
  }
}