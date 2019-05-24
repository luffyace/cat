package com.dianping.cat.util;

import com.dianping.cat.Cat;
import java.io.InputStream;
import java.util.Properties;
import org.codehaus.plexus.logging.Logger;

/**
 * @author luffy
 * @description: properties读取
 * @date 2019-05-24 11:48
 */
public class PropertiesUtil {

  /** 文件后缀 */
  private static final String file_sufffix = ".properties";

  public static String loadProjectName(String fileName, String key, Logger m_logger) {
    if (!fileName.endsWith(file_sufffix)) {
      return null;
    }
    String appName = null;
    InputStream in = null;
    try {
      in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);

      if (in == null) {
        in = Cat.class.getResourceAsStream(fileName);
      }
      if (in != null) {
        Properties prop = new Properties();

        prop.load(in);
        appName = prop.getProperty(key);
        if (appName != null) {
          m_logger.info(String.format("Find domain name %s from %s.", appName, fileName));
        } else {
          m_logger.info(String.format("Can't find app.name from $s.", fileName));
          return null;
        }
      } else {
        m_logger.info(String.format("Can't find app.properties in %s", fileName));
      }
    } catch (Exception e) {
      m_logger.error(e.getMessage(), e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (Exception e) {
        }
      }
    }
    return appName;
  }
}
