/*
 * @author furryinstitute, BurntBread007
 * @repo GexBot for Discord
 * @version 0.6.2a
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.io.IOUtils;

public class Sites {

   public static String get (final String address) {
      String site;
      try {
         URL url = new URL(address);
         URLConnection connect = url.openConnection();
         InputStream inp = connect.getInputStream();
         String encoding = connect.getContentEncoding();
         encoding = (encoding==null) ? "UTF-8" : encoding;
         site = IOUtils.toString(inp, encoding);
      } catch (IOException e) {
         site = "IOException";
      } catch (Exception e) {
         site = "Exception";
      }
      return site;
   }
}
