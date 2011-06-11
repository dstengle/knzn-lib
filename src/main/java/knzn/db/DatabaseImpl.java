package knzn.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.google.common.base.Joiner;


public class DatabaseImpl implements TransactionalDatabase {

  private static class QueryToStringBuilder{
    
    private QueryToStringBuilder() {}
    public static String format(final String query, final Object[] params) {
      final String paramStr = params == null ? "" : Joiner.on(",").useForNull("null").join(params);
      return "Query: " + query + " \n  Params: " + paramStr;
    }
  }
  
  private static DataSource getDataSource(final String dataSourceName) {
    try {
      final InitialContext ctx = new InitialContext();
      final String msgDs = (String) ctx.lookup(dataSourceName);

      LOGGER.fine("Using datasoure: " + msgDs);
      return (DataSource) ctx.lookup (msgDs);

    } catch (final NamingException e) {
      LOGGER.log(Level.SEVERE, "Could not find data source", e);
      throw new IllegalStateException("Cannot find data source",e);
    }
  }

  private static final Logger LOGGER = Logger.getLogger(DatabaseImpl.class.getSimpleName());

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
      LOGGER.log(Level.SEVERE, "Query exception: " + sql, e);
      throw new IllegalStateException(e);
    } finally {
      close(null, null, conn);
    }
  }

  protected <T> List<T> query(final Connection conn, final String sql, final Object[] params,
          final ResultSetHandler<T> resultSetHandler) {

    
    PreparedStatement stmt = null;
    ResultSet resultSet = null;
    
    final List<T> list = new ArrayList<T>();

    final String formattedQuery = QueryToStringBuilder.format(sql, params);
    LOGGER.info(formattedQuery);
    try {

      stmt = conn.prepareStatement(sql);
      setParams(stmt, params);

      resultSet = stmt.executeQuery();
      while (resultSet.next()) {
        list.add(resultSetHandler.handle(resultSet));
      }

    } catch (final SQLException e) {
      LOGGER.log(Level.SEVERE, "Query exception: " + formattedQuery, e);
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
      LOGGER.log(Level.SEVERE, "Update failed: " + 
              QueryToStringBuilder.format(sql, params), e);
      throw new IllegalStateException(e);
    } finally {
      close(null, null, conn);
    }
  }

  public void runTransaction(final Transaction transaction) throws SQLException{

    final TransactionDatabase transDb = new TransactionDatabase(this);
    try {
      transDb.startTransaction();
      transaction.doInTransaction(transDb);
      transDb.commitTransaction();
    } catch (final Exception e) {
      LOGGER.log(Level.SEVERE, "Error during transaction.",e);
      transDb.rollbackTransaction();
    }finally{
      transDb.close();
    }

  }

  protected void update(final Connection conn, final String sql, final Object[] params) {

	    PreparedStatement stmt = null;
	    final ResultSet resultSet = null;

	    final String formattedQuery = QueryToStringBuilder.format(sql, params);
	    LOGGER.info(formattedQuery);
	    
	    try {
	      stmt = conn.prepareStatement(sql);
	      setParams(stmt, params);
	      stmt.execute();

	    } catch (final SQLException e) {
	      LOGGER.severe("Update failed: " + formattedQuery);
	      throw new IllegalStateException(e);
	    } finally {
	    	close(resultSet, stmt, null);
	    }
	  }

  protected void close(final ResultSet resultSet, final Statement stmt,
         final Connection conn) {
    try {
      if (resultSet != null) {
        resultSet.close();
      }
      if (stmt != null) {
        stmt.close();
      }
      if (conn != null) {
        conn.close();
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
