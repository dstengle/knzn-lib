package knzn.ldap;

import javax.naming.NamingException;


public interface SearchResultHandler<T>{
   T handle(SearchResultWrapper searchResult) throws NamingException;
}