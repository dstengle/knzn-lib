package knzn.ldap;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

import org.apache.directory.server.unit.AbstractServerTest;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;

public class LdapTest extends AbstractServerTest {
	
	@Override
	protected void setUp() throws Exception {
		
		MutablePartitionConfiguration mpc = new MutablePartitionConfiguration();
		mpc.setName("Test");
		mpc.setSuffix("o=test");
		
		Set<String> index = new HashSet<String>();
		index.add("objectClass");
		index.add("o");
		index.add("cn");
		index.add("uid");
		
		mpc.setIndexedAttributes(index);
		
		Attributes atts = new BasicAttributes(true);
		
		Attribute att = new BasicAttribute("objectClass");
		att.add("top");
		att.add("organization");
		atts.put(att);
		
		att = new BasicAttribute("o");
		att.add("test");
		atts.put(att);
		
		att = new BasicAttribute("cn");
		att.add("color");
		att.add("people");
		att.add("shape");
		atts.put(att);
		
		att = new BasicAttribute("uid");
		att.add("dbathgate");
		atts.put(att);
		
		mpc.setContextEntry(atts);
		
		Set<MutablePartitionConfiguration> smpc = new HashSet<MutablePartitionConfiguration>();
		smpc.add(mpc);
		
		configuration.setContextPartitionConfigurations(smpc);
		
		File file = new File("/target/ldap/server-work");
		
		configuration.setWorkingDirectory(file);
		
		super.setUp();
	}
	
	public void testPartition() throws NamingException{		
		
		Hashtable<String, String> env = 
			InitialDirectoryContextFactory.createBaseEnv("uid=admin, ou=system", "secret", "o=Test", "simple");
		
        InitialContext ic = new InitialContext(env);

        DirContext dir = (DirContext) ic.lookup("");
        
        assertNotNull(dir);

        Attributes attributes = dir.getAttributes("");
        
        assertNotNull(attributes);
        assertEquals("test", attributes.get("o").get());

        Attribute attribute = attributes.get("objectClass");
        
        assertNotNull(attribute);
        assertTrue(attribute.contains("top"));
        assertTrue(attribute.contains("organization"));
		
	}
	
	public void testSearch(){
		Hashtable<String, String> env = 
			InitialDirectoryContextFactory.createBaseEnv("uid=admin, ou=system", "secret", "o=Test", "simple");
		
		LdapTemplate temp = new LdapTemplate(env);
		
		SearchResultHandler<Object> result = new SearchResultHandler<Object>() {
			
			public Object handle(SearchResultWrapper searchResult)
					throws NamingException {
				return searchResult.getAttribute("cn");
			}
		};
		SearchControls search = new SearchControls();
		search.setSearchScope(SearchControls.OBJECT_SCOPE);
		
		List<Object> list =  temp.search("", "(uid=*)", result, search);
	
		assertNotNull(list);
	}
	
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
