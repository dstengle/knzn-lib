package knzn.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class XpathDocTest {

  private static final Logger LOGGER = Logger.getLogger(XpathDocTest.class.getSimpleName());
  
  private static final String TEST_XML = "/knzn/xml/xpath_test.xml";

  private  static String readXmlFromFile() throws IOException {
    final InputStream inputStream = XpathDocTest.class
      .getResourceAsStream(TEST_XML);
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    IOUtils.copy(inputStream, bout);
    
    return new String(bout.toByteArray());
  }
  
  @Test
  public void testNewXpathDocFromFile() throws XPathExpressionException, FileNotFoundException {
    final File file = FileUtils.toFile(
            XpathDocTest.class.getResource(TEST_XML));
    final String xmlFile = file.getAbsolutePath();
    LOGGER.info("xmlFile: " + xmlFile);
    
    assertXpathDoc(
            XpathDoc.newXpathDocFromFile(xmlFile));
    
    assertXpathDoc(
            XpathDoc.newXpathDocFromFile(file));
  }

  
  @Test
  public void testNewXpathDocFromFileMissing() throws XPathExpressionException {
    try {
      XpathDoc.newXpathDocFromFile("missing_file.xml");
      fail("Missing file exception not thrown");
    } catch (FileNotFoundException e) {
      assertEquals("Exception message not correct", 
              "java.io.FileNotFoundException: Not found: missing_file.xml",
              e.toString());
    }
  }

  @Test
  public void testNewXpathDocFromString() throws XPathExpressionException, IOException {
    final String xml = readXmlFromFile();
    assertXpathDoc(XpathDoc.newXpathDocFromString(xml));
  }
  
  private void assertXpathDoc(final XpathDoc xpathDoc) throws XPathExpressionException {
    assertEquals("Expected name not found", "Tuck", 
            xpathDoc.evaluate("//wonder_pets/wonder_pet[1]/name"));
    assertEquals("Expected type not found", "turtle", 
            xpathDoc.evaluate("//wonder_pets/wonder_pet[1]/@type"));
  }
}
