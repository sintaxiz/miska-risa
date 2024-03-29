/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package osm.archive.stat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import osm.archive.stat.node.Node;
import osm.archive.stat.node.NodeReader;
import osm.archive.stat.node.XmlNodeReader;
import osm.archive.stat.statistics.OsmStatistics;
import osm.archive.stat.statistics.StatiscticsWriter;
import osm.archive.stat.statistics.Statistics;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

public class App {

    private static final Logger log
            = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        CliParser parser = new CliParser();
        AppConfiguration config = parser.parse(args);
        if (config == null) {
            parser.printHelp();
            return;
        }

        InputStream inputStream = InputStreamSource.getStream(config);

        try {
            NodeReader nodeReader = new XmlNodeReader(inputStream);
            Statistics<Node> osmStatistic = new OsmStatistics();
            while (nodeReader.hasNext()) {
                osmStatistic.add(nodeReader.read());
            }
            StatiscticsWriter statiscticsWriter = stat -> {
                log.info(String.format("Osm statistic for %s:\n%s", config.path(), stat));
            };
            statiscticsWriter.write(osmStatistic);
        } catch (XMLStreamException e) {
            log.error("Can not parse XML", e);
        }

    }
}
