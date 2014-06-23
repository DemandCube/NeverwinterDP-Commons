package com.neverwinterdp.hadoop.storm.wordcount;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

/**
 * Topology class that sets up the Storm topology for this sample. Please note
 * that Twitter credentials have to be provided as VM args, otherwise you'll get
 * an Unauthorized error.
 * 
 * @link http://twitter4j.org/en/configuration.html#systempropertyconfiguration
 */
public class Topology {

  static final String TOPOLOGY_NAME = "WordCount";

  public static void main(String[] args) {
    Config config = new Config();
    config.setMessageTimeoutSecs(120);

    TopologyBuilder b = new TopologyBuilder();
    b.setSpout("TextSpout", new TextSpout());
    b.setBolt("WordSplitterBolt", new WordSplitterBolt()).shuffleGrouping("TextSpout");
    b.setBolt("WordCounterBolt", new WordCounterBolt()).shuffleGrouping("WordSplitterBolt");
    final LocalCluster cluster = new LocalCluster();
    cluster.submitTopology(TOPOLOGY_NAME, config, b.createTopology());

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        cluster.killTopology(TOPOLOGY_NAME);
        cluster.shutdown();
      }
    });
  }
}
