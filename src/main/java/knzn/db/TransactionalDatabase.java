package knzn.db;

import java.sql.SQLException;

public interface TransactionalDatabase extends Database{
  void runTransaction(final Transaction transaction) throws SQLException;
}