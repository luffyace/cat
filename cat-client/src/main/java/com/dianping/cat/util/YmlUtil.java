package com.dianping.cat.util;

import com.dianping.cat.Cat;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.plexus.logging.Logger;
import org.yaml.snakeyaml.Yaml;

/**
 * @author luffy
 * @description: yml读取
 * @date 2019-05-24 11:48
 */
public class YmlUtil {

  /** 文件后缀 */
  private static final String file_sufffix = ".yml";

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
        Map<Object, Object> ymls = new HashMap<>();
        ymls.putAll(new Yaml().loadAs(in, LinkedHashMap.class));
        appName = (String) getValue(key, ymls, m_logger);
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

  private static Object getValue(String key, Map<Object, Object> ymls, Logger m_logger) {
    // 首先将key进行拆分
    String[] keys = key.split("[.]");

    // 将配置文件进行复制
    Map ymlInfo = ymls;
    for (int i = 0; i < keys.length; i++) {
      Object value = ymlInfo.get(keys[i]);
      if (i < keys.length - 1) {
        ymlInfo = (Map) value;
      } else if (value == null) {
        m_logger.info(String.format("Can't find app.properties in %s", key));
      } else {
        return value;
      }
    }
    m_logger.info(String.format("Can't find app.properties in %s", key));
    return null;
  }
}
