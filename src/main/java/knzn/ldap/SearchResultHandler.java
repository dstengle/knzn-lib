package knzn.ldap;

import javax.naming.NamingException;


public interface SearchResultHandler<T>{
  public T handle(SearchResultWrapper searchResult) throws NamingException;
}