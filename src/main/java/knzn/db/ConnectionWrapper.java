package knzn.db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ConnectionWrapper implements Connection {

  private static class Trace {

    private final List<String> calls = new ArrayList<String>(20);

    public Trace() {
      split(new Exception());
    }

    public void append(final StringBuffer buff) {

      if (buff == null) {
        return;
      }

      for (int i = 0; i < calls.size(); i++) {
        buff.append("\n\t\t").append(calls.get(i));
      }
    }

    public String getCaller() {
      return calls.get(4);
    }

    @Override
    public String toString() {
      final StringBuffer b = new StringBuffer(512);
      append(b);

      return b.toString();
    }

    private void split(final Throwable t) {
      final StringWriter sw = new StringWriter(1024);
      final PrintWriter out = new PrintWriter(sw, false);

      t.fillInStackTrace();
      t.printStackTrace(out);

      final StringTokenizer tk = new StringTokenizer(sw.toString(), "\n");
      String line = null;

      while (tk.hasMoreTokens()) {
        line = tk.nextToken().trim();

        if (line.startsWith("at ")) {
          calls.add(line);
        }
      }
    }
  }

  private static int COUNT = 0;
  private static synchronized void decrementCounter() {
    COUNT--;
  }
  private static String getTrace(final Throwable t) {
    final StringWriter sw = new StringWriter(1024);
    final PrintWriter out = new PrintWriter(sw, false);

    t.fillInStackTrace();
    t.printStackTrace(out);

    return sw.toString();
  }
  private static synchronized void incrementCounter() {
    COUNT++;
  }

  private Connection conn = null;

  private long creationTime = 0;

  private boolean closeCalled = false;

  private Trace trace = null;

  public ConnectionWrapper(final Connection conn) {
    this.conn = conn;
    creationTime = System.currentTimeMillis();

    incrementCounter();

    trace = new Trace();

    log("Get(): Caller: " + trace.getCaller() + " COUNT: " + COUNT, null);
  }

  public void clearWarnings() throws SQLException {
    conn.clearWarnings();
  }

  public void close() throws SQLException {
    Exception err = null;

    if (!closeCalled) {
      decrementCounter();
      closeCalled = true;
    }

    try {
      conn.close();
    } catch (final Exception e) {
      e.printStackTrace();
      err = e;
    }

    log("close(): Connection released after: "
            + (System.currentTimeMillis() - creationTime) + "ms. Caller: "
            + trace.getCaller() + " COUNT: " + COUNT, err);
  }

  public void commit() throws SQLException {
    conn.commit();
  }

  public Statement createStatement() throws SQLException {
    return conn.createStatement();
  }

  public Statement createStatement(final int resultSetType,
          final int resultSetConcurrency) throws SQLException {
    return conn.createStatement(resultSetType, resultSetConcurrency);
  }

  public Statement createStatement(final int resultSetType,
          final int resultSetConcurrency, final int resultSetHoldability)
          throws SQLException {
    return conn.createStatement(resultSetType, resultSetConcurrency,
            resultSetHoldability);
  }

  public boolean getAutoCommit() throws SQLException {
    return conn.getAutoCommit();
  }

  public String getCatalog() throws SQLException {
    return conn.getCatalog();
  }

  public int getHoldability() throws SQLException {
    return conn.getHoldability();
  }

  public DatabaseMetaData getMetaData() throws SQLException {
    return conn.getMetaData();
  }

  public int getTransactionIsolation() throws SQLException {
    return conn.getTransactionIsolation();
  }

  public Map<String, Class<?>> getTypeMap() throws SQLException {
    return conn.getTypeMap();
  }

  public SQLWarning getWarnings() throws SQLException {
    return conn.getWarnings();
  }

  public boolean isClosed() throws SQLException {
    return conn.isClosed();
  }

  public boolean isReadOnly() throws SQLException {
    return conn.isReadOnly();
  }

  public String nativeSQL(final String sql) throws SQLException {
    return conn.nativeSQL(sql);
  }

  public CallableStatement prepareCall(final String sql) throws SQLException {
    return conn.prepareCall(sql);
  }

  public CallableStatement prepareCall(final String sql,
          final int resultSetType, final int resultSetConcurrency)
          throws SQLException {
    return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
  }

  public CallableStatement prepareCall(final String sql,
          final int resultSetType, final int resultSetConcurrency,
          final int resultSetHoldability) throws SQLException {
    return conn.prepareCall(sql, resultSetType, resultSetConcurrency,
            resultSetHoldability);
  }

  public PreparedStatement prepareStatement(final String sql)
          throws SQLException {
    return conn.prepareStatement(sql);
  }

  public PreparedStatement prepareStatement(final String sql,
          final int autoGeneratedKeys) throws SQLException {
    return conn.prepareStatement(sql, autoGeneratedKeys);
  }

  public PreparedStatement prepareStatement(final String sql,
          final int resultSetType, final int resultSetConcurrency)
          throws SQLException {
    return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
  }

  public PreparedStatement prepareStatement(final String sql,
          final int resultSetType, final int resultSetConcurrency,
          final int resultSetHoldability) throws SQLException {
    return conn.prepareStatement(sql, resultSetType, resultSetConcurrency,
            resultSetHoldability);
  }

  public PreparedStatement prepareStatement(final String sql,
          final int[] columnIndexes) throws SQLException {
    return conn.prepareStatement(sql, columnIndexes);
  }

  public PreparedStatement prepareStatement(final String sql,
          final String[] columnNames) throws SQLException {
    return conn.prepareStatement(sql, columnNames);
  }

  public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
    conn.releaseSavepoint(savepoint);
  }

  public void rollback() throws SQLException {
    conn.rollback();
  }

  public void rollback(final Savepoint savepoint) throws SQLException {
    conn.rollback(savepoint);
  }

  public void setAutoCommit(final boolean autoCommit) throws SQLException {
    conn.setAutoCommit(autoCommit);
  }

  public void setCatalog(final String catalog) throws SQLException {
    conn.setCatalog(catalog);
  }

  public void setHoldability(final int holdability) throws SQLException {
    conn.setHoldability(holdability);
  }

  public void setReadOnly(final boolean readOnly) throws SQLException {
    conn.setReadOnly(readOnly);
  }

  public Savepoint setSavepoint() throws SQLException {
    return conn.setSavepoint();
  }

  public Savepoint setSavepoint(final String name) throws SQLException {
    return conn.setSavepoint(name);
  }

  public void setTransactionIsolation(final int level) throws SQLException {
    conn.setTransactionIsolation(level);
  }

  public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
    conn.setTypeMap(map);
  }

  @Override
  protected void finalize() throws Throwable {

    if ((conn != null) && (!closeCalled || !conn.isClosed())) {

      log("Finalize(): Close() not called. COUNT: " + COUNT + "\nTRACE:\n"
              + trace, null);

      if (!conn.isClosed()) {
        close();
      } else {
        decrementCounter();
      }
    }

    super.finalize();
  }

  private void log(final String message, final Throwable t) {
    final StringBuffer msgbuf = new StringBuffer(256);

    msgbuf.append(" ConnectionWrapper::").append(message);

    if (t != null) {
      msgbuf.append("\nError follows: -----------------------------------\n");
      msgbuf.append(getTrace(t));
    }

    System.out.println(msgbuf.toString());
  }
}