package knzn.db;

import java.sql.SQLException;

/****
 * Use for transactions!
 *
 */
public interface Transaction{
  void doInTransaction(TransactionDatabase database) throws SQLException;
}