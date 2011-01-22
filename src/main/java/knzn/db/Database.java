package knzn.db;

import java.util.List;

public interface Database {

  public <T> List<T> query(final String sql, final Object[] params,
      final ResultSetHandler<T> resultSetHandler);

  public <T> List<T> query(final String sql,
      final ResultSetHandler<T> resultSetHandler);

  public void update(final String sql, final Object[] params);

}