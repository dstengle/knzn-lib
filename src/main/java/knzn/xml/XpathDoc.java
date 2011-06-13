package knzn.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

public final class XpathDoc {

  public static class InputStreamInputSourceHolder implements InputSourceHolder {

    private final InputStream stream;

    public InputStreamInputSourceHolder(final InputStream stream) {
      this.stream = stream;
    }

    public void close() {
      try {
        stream.close();
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, "Could not close stream", e);
      }
    }

    public InputSource getInputSource() {
      return new InputSource(stream);
    }
  }

  
  private static class FileInputSourceHolder implements InputSourceHolder {

    private final File file;
    private InputStreamInputSourceHolder sourceHolder;

    public FileInputSourceHolder(final File file) throws FileNotFoundException {
      this.file = file;
      if (!this.file.exists()) {
        throw new FileNotFoundException("Not found: " + this.file);
      }
    }

    public FileInputSourceHolder(final String xmlFile)
            throws FileNotFoundException {
      this(new File(xmlFile));
    }

    public void close() {
      sourceHolder.close();
    }

    public InputSource getInputSource() {
      try {
        sourceHolder = new InputStreamInputSourceHolder(new FileInputStream(file));
        return sourceHolder.getInputSource();
      } catch (FileNotFoundException e) {
        LOGGER.log(Level.SEVERE, "Could not find file", e);
        throw new IllegalStateException(e);
      }
    }
  }

  private interface InputSourceHolder {
    void close();

    InputSource getInputSource();
  }

  private static class StringInputSourceHolder implements InputSourceHolder {

    private final String text;
    private InputStreamInputSourceHolder sourceHolder;

    public StringInputSourceHolder(final String text) {
      this.text = text;
    }
    
    public void close() {
      sourceHolder.close();
    }

    public InputSource getInputSource() {
      sourceHolder = new InputStreamInputSourceHolder(new ByteArrayInputStream(text.getBytes()));
      return sourceHolder.getInputSource();
    }

  }

  private static final Logger LOGGER = Logger.getLogger(XpathDoc.class
          .getSimpleName());

  
  public static XpathDoc newXpathDocFromFile(final File xmlFile)
          throws FileNotFoundException {
    return new XpathDoc(new FileInputSourceHolder(xmlFile));
  }

  public static XpathDoc newXpathDocFromFile(final String xmlFile)
          throws FileNotFoundException {
    return new XpathDoc(new FileInputSourceHolder(xmlFile));
  }

  public static XpathDoc newXpathDocFromString(final String xml) {
    return new XpathDoc(new StringInputSourceHolder(xml));
  }

  private final XPath xPath;
  private final InputSourceHolder inputSourceHolder;

  private XpathDoc(final InputSourceHolder inputSourceHolder) {
    this.inputSourceHolder = inputSourceHolder;
    xPath = XPathFactory.newInstance().newXPath();
  }

  public String evaluate(final String expression)
          throws XPathExpressionException {
    final InputSource inputSource = inputSourceHolder.getInputSource();
    final String result = xPath.evaluate(expression, inputSource);
    inputSourceHolder.close();
    return result;
  }
}
