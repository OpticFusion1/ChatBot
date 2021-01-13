package optic_fusion1.chatbot.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.chatbot.ChatBot;

public final class FileUtils {

  private FileUtils() {
  }

  public static boolean copy(final InputStream source, final String destination) {
    boolean succeess = true;
    try {
      Files.copy(source, Paths.get(destination, new String[0]), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ex) {
      succeess = false;
    }
    return succeess;
  }

  public static void saveResourceIfNonExistant(final String filePath, final String resource) {
    final File file = new File(filePath);
    if (!file.exists()) {
      try {
        file.mkdirs();
        file.createNewFile();
      } catch (IOException ex) {
        Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
      }
      final InputStream input = getResource(resource);
      copy(input, filePath);
    }
  }

  public static void saveResource(final File file, String resourcePath, final boolean replace) {
    if (resourcePath == null || resourcePath.isEmpty()) {
      throw new IllegalArgumentException("ResourcePath cannot be null or empty");
    }
    resourcePath = resourcePath.replace('\\', '/');
    final InputStream in = getResource(resourcePath);
    if (in == null) {
      throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
    }
    final File outFile = new File(file, resourcePath);
    final int lastIndex = resourcePath.lastIndexOf(47);
    final File outDir = new File(file, resourcePath.substring(0, (lastIndex >= 0) ? lastIndex : 0));
    if (!outDir.exists()) {
      outDir.mkdirs();
    }
    try {
      if (!outFile.exists() || replace) {
        final OutputStream out = new FileOutputStream(outFile);
        try {
          final byte[] buf = new byte[1024];
          int len;
          while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
          }
          out.close();
        } catch (IOException t) {
          try {
            out.close();
          } catch (IOException t2) {
            t.addSuppressed(t2);
          }
          throw t;
        }
        in.close();
      }
    } catch (IOException ex) {
    }
  }

  public static URL getResourceAsURL(final String filename) {
    if (filename == null) {
      throw new IllegalArgumentException("Filename cannot be null");
    }
    final URL url = ChatBot.class.getClassLoader().getResource(filename);
    if (url == null) {
      return null;
    }
    return url;
  }

  public static InputStream getResource(final String filename) {
    if (filename == null) {
      throw new IllegalArgumentException("Filename cannot be null");
    }
    try {
      final URL url = ChatBot.class.getClassLoader().getResource(filename);
      if (url == null) {
        return null;
      }
      final URLConnection connection = url.openConnection();
      connection.setUseCaches(false);
      return connection.getInputStream();
    } catch (IOException ex) {
      return null;
    }
  }

}
