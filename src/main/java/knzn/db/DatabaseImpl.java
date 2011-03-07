package knzn.db;

import com.google.common.base.Function;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;


public class DatabaseImpl implements TransactionalDatabase {

  private static DataSource getDataSource(final String dataSourceName) {
    try {
      final InitialContext ctx = new InitialContext();
      final String msgDs = (String) ctx.lookup(dataSourceName);

      System.out.println("Using datasoure: " + msgDs);
      return (DataSource) ctx.lookup (msgDs);

    } catch (final NamingException e) {
      e.printStackTrace();
      throw new IllegalStateException("Cannot find data source",e);
    }
  }

  private final Logger logger = Logger.getLogger(DatabaseImpl.class.getSimpleName());

  private final DataSource dataSource;

  public DatabaseImpl(final String dataSourceName) {
    this(getDataSource(dataSourceName));
  }

  public DatabaseImpl(final DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public <T> List<T> query(final String sql, final Object[] params,
          final ResultSetHandler<T> resultSetHandler) {

    Connection conn = null;
    try {
      conn  = getConnection();
      return query(conn, sql, params, resultSetHandler);

    } catch (final SQLException e) {
      logger.log(Level.SEVERE, "Query exception: " + sql, e);
      throw new IllegalStateException(e);
    } finally {
      close(null, null, conn);
    }
  }

  protected <T> List<T> query(final Connection conn, final String sql, final Object[] params,
          final ResultSetHandler<T> resultSetHandler) {

    PreparedStatement stmt = null;
    ResultSet resultSet = null;
    final String strParams = params == null ? "" : Joiner.on(",").useForNull("null").join(params);
    final List<T> list = new ArrayList<T>();

    logger.info("Running query: " + sql + " with params " +
            strParams);
    try {

      stmt = conn.prepareStatement(sql);
      setParams(stmt, params);

      resultSet = stmt.executeQuery();
      while (resultSet.next()) {
        list.add(resultSetHandler.handle(resultSet));
      }

    } catch (final SQLException e) {
      logger.log(Level.SEVERE, "Query exception: " + sql, e);
      throw new IllegalStateException(e);
    } finally {
    	close(resultSet, stmt, null);
    }
    return list;
  }

  protected Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  public <T> List<T> query(final String sql, final ResultSetHandler<T> resultSetHandler) {
    return query(sql, null, resultSetHandler);
  }


  public void update(final String sql, final Object[] params) {
    Connection conn = null;
    try {
      conn  = getConnection();
      update(conn, sql, params);

    } catch (final SQLException e) {

      final String join = params == null ? "" : Joiner.on(",").useForNull("null").join(params);
	logger.log(Level.SEVERE, "Update failed: " + sql + " with params " +
              join, e);
      throw new IllegalStateException(e);
    } finally {
      close(null, null, conn);
    }
  }

  public void runTransaction(final Transaction transaction) throws SQLException{

    final TransactionDatabase transactionDatabase = new TransactionDatabase(this);
    try {
      transactionDatabase.startTransaction();
      transaction.doInTransaction(transactionDatabase);
      transactionDatabase.commitTransaction();
    } catch (final Exception e) {
      logger.log(Level.SEVERE, "Error during transaction.",e);
      transactionDatabase.rollbackTransaction();
    }finally{
      transactionDatabase.close();
    }

  }

  protected void update(final Connection conn, final String sql, final Object[] params) {

	    PreparedStatement stmt = null;
	    final ResultSet resultSet = null;

	    final String paramLog = params == null ? "" : Joiner.on(",").useForNull("null").join(params);
	    logger.info("Running query: " + sql + " with params " + paramLog);
	    try {
	      stmt = conn.prepareStatement(sql);
	      setParams(stmt, params);
	      stmt.execute();

	    } catch (final SQLException e) {
	      logger.severe("Update failed: " + sql + " with params " +
	              paramLog);
	      throw new IllegalStateException(e);
	    } finally {
	    	close(resultSet, stmt, null);
	    }
	  }

  protected void close(final ResultSet resultSet, final Statement stmt,
          Connection conn) {
    try {
      if (resultSet != null) {
        resultSet.close();
      }
      if (stmt != null) {
        stmt.close();
      }
      if (conn != null) {
        conn.close();
        conn = null;
      }
    } catch (final SQLException e) {
      throw new IllegalStateException(e);
    }
  }

  private void setParams(final PreparedStatement stmt, final Object[] params)
          throws SQLException {
    if (params != null) {
      int count = 1;
      for (final Object object : params) {
    	  if (object instanceof java.sql.Date) {
    		  stmt.setDate(count++, (java.sql.Date)object);
    	  } else {
    		  stmt.setObject(count++, object);
    	  }
      }
    }
  }
}
