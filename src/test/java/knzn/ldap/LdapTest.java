package knzn.ldap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.directory.server.unit.AbstractServerTest;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;

public class LdapTest extends AbstractServerTest {
	
	@Override
	protected void setUp() throws Exception {
		
		setupPartition();
		
		super.setUp();
		
		InputStream io = new FileInputStream(new File("src/test/resources/demo.ldif"));
		importLdif(io);
	}
	
    public void testImport() throws NamingException
    {
        Set<String> result = searchDNs( "(ObjectClass=*)", "", 
                                          SearchControls.ONELEVEL_SCOPE );

        assertTrue( result.contains( "ou=groups,o=sevenSeas" ) );
        assertTrue( result.contains( "ou=people,o=sevenSeas" ) );
    }
	
	public void testPartition() throws NamingException{		
		
		InitialContext ic = ContextFactory.create();

        DirContext dir = (DirContext) ic.lookup("");
        
        assertNotNull(dir);

        Attributes attributes = dir.getAttributes("");
        
        assertNotNull(attributes);
        assertEquals("sevenseas", attributes.get("o").get());

        Attribute attribute = attributes.get("objectClass");
        
        assertNotNull(attribute);
        assertTrue(attribute.contains("top"));
        assertTrue(attribute.contains("organization"));
		
	}
	
	public void testSearch(){
		Hashtable<String, String> env =
			InitialDirectoryContextFactory.createBaseEnv("uid=admin, ou=system", "secret", "o=sevenSeas", "simple");
		
		LdapTemplate temp = new LdapTemplate(env);
		
		SearchResultHandler<Object> result = new SearchResultHandler<Object>() {
			
			public Object handle(SearchResultWrapper searchResult)
					throws NamingException {
				return searchResult.getAttribute("ou");
			}
		};
		SearchControls search = new SearchControls();
		search.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		
		List<Object> list =  temp.search("", "(ou=*)", result, search);
	
		assertTrue(list.contains("people"));
		assertTrue(list.contains("groups"));
	}
	
    private Set<String> searchDNs( String filter, String base, int scope ) 
    throws NamingException
 {
     DirContext appRoot = ContextFactory.createDir();

     SearchControls controls = new SearchControls();
     controls.setSearchScope( scope );
     NamingEnumeration result = appRoot.search( base, filter, controls );

     // collect all results
     HashSet<String> entries = new HashSet<String>();

     while ( result.hasMore() )
     {
         SearchResult entry = ( SearchResult ) result.next();
         entries.add( entry.getName() );
     }

     return entries;
 }
	
	private void setupPartition() throws NamingException{

        MutablePartitionConfiguration pcfg = new MutablePartitionConfiguration();
        pcfg.setName( "sevenSeas" );
        pcfg.setSuffix( "o=sevenseas" );

        Set<String> indexedAttrs = new HashSet<String>();
        indexedAttrs.add( "objectClass" );
        indexedAttrs.add( "o" );
        pcfg.setIndexedAttributes( indexedAttrs );

        Attributes attrs = new BasicAttributes( true );

        Attribute attr = new BasicAttribute( "objectClass" );
        attr.add( "top" );
        attr.add( "organization" );
        attrs.put( attr );

        attr = new BasicAttribute( "o" );
        attr.add( "sevenseas" );
        attrs.put( attr );

        pcfg.setContextEntry( attrs );

        Set<MutablePartitionConfiguration> pcfgs = new HashSet<MutablePartitionConfiguration>();
        pcfgs.add( pcfg );

        configuration.setContextPartitionConfigurations( pcfgs );

        File workingDirectory = new File( "server-work" );
        configuration.setWorkingDirectory( workingDirectory );

	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
