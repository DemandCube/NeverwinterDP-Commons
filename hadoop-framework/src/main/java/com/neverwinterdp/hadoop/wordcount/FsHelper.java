package com.neverwinterdp.hadoop.wordcount;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.Validate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class FsHelper {

  private final FileSystem    fs;
  private final Configuration conf;

  public FsHelper(Configuration conf) throws IOException {
    Validate.notNull(conf);
    this.conf = conf;
    this.fs = FileSystem.get(conf);
  }

  public byte[] readBytes(Path path) {
    FSDataInputStream in = null;
    try {
      in = fs.open(path);
      long bytesLen = fs.getFileStatus(path).getLen();
      byte[] buffer = new byte[(int) bytesLen];
      IOUtils.readFully(in, buffer, 0, buffer.length);
      return buffer;
    } catch (IOException e) {
      throw new RuntimeException("Failed to read from [" + path + "]", e);
    } finally {
      IOUtils.closeStream(in);
    }
  }

  public void writeStringToFile(Path path, String string) {
    InputStream in = new BufferedInputStream(new ByteArrayInputStream(string.getBytes()));
    FSDataOutputStream out = null;
    try {
      out = fs.create(path);
      IOUtils.copyBytes(in, out, conf);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write string to [" + path + "]", e);
    } finally {
      IOUtils.closeStream(out);
    }
  }

  public String readStringFroomFile(Path path) {
    return new String(readBytes(path));
  }
}
