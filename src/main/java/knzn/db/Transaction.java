package knzn.db;

/****
 * Use for transactions!
 *
 */
public interface Transaction{
  void doInTransaction(TransactionDatabase database);
}