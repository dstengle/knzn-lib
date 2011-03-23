package knzn.db;

import java.util.List;

public interface Database {

  <T> List<T> query(final String sql, final Object[] params,
      final ResultSetHandler<T> resultSetHandler);

  <T> List<T> query(final String sql,
      final ResultSetHandler<T> resultSetHandler);

  int update(final String sql, final Object[] params);

}