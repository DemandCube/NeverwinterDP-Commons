package com.neverwinterdp.hadoop.wordcount;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.junit.Test;

public class StartsWithCountMapperReducerTest {

  @Test
  public void testWithMRUnit() throws IOException {
    MapReduceDriver<LongWritable, Text, Text, IntWritable, Text, IntWritable> driver =
        new MapReduceDriver<LongWritable, Text, Text, IntWritable, Text, IntWritable>();

    driver.setMapper(new StartsWithCountMapper());
    driver.setReducer(new StartsWithCountReducer());

    LongWritable k = new LongWritable();
    driver.withInput(k, new Text("This is a line number one"));
    driver.withInput(k, new Text("This is another line"));

    driver.withOutput(new Text("T"), new IntWritable(2));
    driver.withOutput(new Text("a"), new IntWritable(2));
    driver.withOutput(new Text("i"), new IntWritable(2));
    driver.withOutput(new Text("l"), new IntWritable(2));
    driver.withOutput(new Text("n"), new IntWritable(1));
    driver.withOutput(new Text("o"), new IntWritable(1));
    driver.runTest();
  }
}
