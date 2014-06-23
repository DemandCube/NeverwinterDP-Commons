package com.neverwinterdp.hadoop.storm.wordcount;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

/**
 * Keeps stats on word count, calculates and logs top words every X second to
 * stdout and top list every Y seconds,
 * 
 * @author davidk
 */
public class WordCounterBolt extends BaseRichBolt {
  private static final Logger logger = LoggerFactory.getLogger(WordCounterBolt.class);

  private Map<String, Long>   counter;

  public WordCounterBolt() {
  }

  @Override
  public void prepare(Map map, TopologyContext topologyContext, OutputCollector collector) {
    counter = new HashMap<String, Long>();
  }

  @Override
  public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
  }

  @Override
  public void execute(Tuple input) {
    String word = (String) input.getValueByField("word");
    Long count = counter.get(word);
    count = count == null ? 1L : count + 1;
    counter.put(word, count);

    // logger.info(new
    // StringBuilder(word).append('>').append(count).toString());

    System.out.println("Word count: " + counter.size());
  }
}
