package knzn.db;

import java.sql.SQLException;
import java.util.List;

public interface Database {

  <T> List<T> query(final String sql, final Object[] params,
      final ResultSetHandler<T> resultSetHandler);

  <T> List<T> query(final String sql,
      final ResultSetHandler<T> resultSetHandler);

  void update(final String sql, final Object[] params);

  void runTransaction(final Transaction transaction) throws SQLException;

}