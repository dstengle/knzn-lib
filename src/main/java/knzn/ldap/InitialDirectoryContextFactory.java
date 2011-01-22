package knzn.ldap;

import java.util.Hashtable;

import javax.naming.Context;

public class InitialDirectoryContextFactory {

  public static Hashtable<String, String> createBaseEnv(final String principle,
          final String credentials, final String host) {
    Hashtable<String, String> env = new Hashtable<String, String>();

    // basic settings
    env
            .put(Context.INITIAL_CONTEXT_FACTORY,
                    "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.SECURITY_PRINCIPAL, principle);
    env.put(Context.SECURITY_CREDENTIALS, credentials);
    env.put(Context.PROVIDER_URL, host);

    return env;
  }

  public static Hashtable<String, String> createPooledEnv(
          final String principle, final String credentials, final String host,
          final int connectTimeout, final int poolSize, final int poolTimeout) {
    Hashtable<String, String> env = createBaseEnv(principle, credentials, host);
    env.put("com.sun.jndi.ldap.connect.pool", "true");
    env
            .put("com.sun.jndi.ldap.connect.timeout", String
                    .valueOf(connectTimeout));
    env.put("com.sun.jndi.ldap.connect.pool.maxsize", String.valueOf(poolSize));
    env.put("com.sun.jndi.ldap.connect.pool.timeout", String
            .valueOf(poolTimeout));
    return env;
  }
}