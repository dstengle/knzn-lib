package knzn.db;


public interface Transaction{
  void doInTransaction(TransactionDatabase database);
}