package knzn.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LdapTemplate{

  private static final Logger LOGGER = Logger.getLogger(
          LdapTemplate.class.getSimpleName());
  private final Hashtable<String, String> env;

  public LdapTemplate(final Hashtable<String, String> env) {
    this.env = env;
  }

  public static <E> List<E> search(final LdapTemplate ldapTemplate, final String name,
          final String filter, final SearchResultHandler<E> srMapper) {
    
	final SearchControls searchControls = new SearchControls();
    
	searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    
	return ldapTemplate.search(name, filter, srMapper, searchControls);
  }

  public <E> List<E> search(final String name, final String filter,
          final SearchResultHandler<E> srMapper, final SearchControls constraints) {
    DirContext ctx = null;
    final List<E> results = new ArrayList<E>();
    try {
      ctx = new InitialDirContext(env);

      LOGGER.info(" - search: " + name);
      LOGGER.info(" - filter: " + filter);
      
      final NamingEnumeration<SearchResult> answer = ctx.search(
              name,
              filter,
              constraints);


      int count = 0;
      while (answer.hasMoreElements()) {
        results.add(srMapper.handle(
                new SearchResultWrapper(answer.nextElement())));
        count++;
      }
      LOGGER.info(" - count: " + count);

    } catch (final NamingException e) {
      LOGGER.log(Level.SEVERE, "Could not search ldap", e);
    }finally{
      try {
        ctx.close();
      } catch (final NamingException e) {
        LOGGER.log(Level.SEVERE, "Could not close context", e);
      }
    }

  return results;
  }
}