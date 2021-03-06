/*
* Copyright (C) 2021 Optic_Fusion1
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

// Copied from spigot so I'm not passing around instances of ChatBot just for utility methods
public final class FileUtils {

  private FileUtils() {
  }

  // Copied from spigot so I'm not passing around instances of ChatBot just for utility methods
  public static boolean copy(final InputStream source, final String destination) {
    boolean succeess = true;
    try {
      Files.copy(source, Paths.get(destination, new String[0]), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ex) {
      succeess = false;
    }
    return succeess;
  }

  // Copied from spigot so I'm not passing around instances of ChatBot just for utility methods
  public static void saveResourceIfNonExistant(final String filePath, final String resource) {
    final File file = new File(filePath);
    if (!file.exists()) {
      file.mkdirs();
      try {
        file.createNewFile();
      } catch (IOException ex) {
        Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
      }
      final InputStream input = getResource(resource);
      copy(input, filePath);
    }
  }

  // Copied from spigot so I'm not passing around instances of ChatBot just for utility methods
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

  // Copied from spigot so I'm not passing around instances of ChatBot just for utility methods
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

  // Copied from spigot so I'm not passing around instances of ChatBot just for utility methods
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
