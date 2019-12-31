
package com.tmser.video;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * <pre>
 *
 * </pre>
 *
 * @author tmser
 * @version $Id: VideoSpider.java, v 1.0 2019年11月7日 下午3:08:30 tmser Exp $
 */
public class VideoM3u8Process {
  private static String baseFolder = "E:\\qres\\videom\\";
  private static String oldFolder = "E:\\qres\\video\\";

  public static void main(String[] args) throws IOException {
    parseUrl(new File(oldFolder));
  }

  private static void parseUrl(File fileFolder) throws IOException {
    for (File f : fileFolder.listFiles()) {
      if (f.isDirectory()) {
        parseUrl(f);
      }
      if (f.isFile() && f.getName().endsWith(".m3u8")) {
        StringBuilder content = new StringBuilder();
        for (String tsLine : FileUtils.readLines(f)) {
          if (tsLine.trim().startsWith("/")) {
            content.append(tsLine.substring(1));
          } else {
            content.append(tsLine);
          }
          content.append("\r\n");
        }
        String relative = f.getAbsolutePath().replace(oldFolder, "");
        File newFile = new File(baseFolder, relative);
        if (!newFile.exists()) {
          FileUtils.forceMkdir(newFile.getParentFile());
          FileUtils.write(newFile, content);
        }
      }
    }

  }

}
