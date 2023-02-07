package osm.archive.stat.statistics;


import osm.archive.stat.node.Node;

import java.util.HashMap;
import java.util.Map;

public class OsmStatistics implements Statistics<Node> {
    private final Map<String, Integer> changesPerUser;
    private final Map<String, Integer> tagCountPerKey;

    public OsmStatistics() {
        changesPerUser = new HashMap<>();
        tagCountPerKey = new HashMap<>();
    }

    private <T> Integer incrementStatistic(T key, Integer value) {
        if (value == null) {
            return 1;
        }
        return value + 1;
    }

    @Override
    public void add(Node node) {
        changesPerUser.compute(node.getUser(), this::incrementStatistic);
        for (var tag : node.getTags()) {
            tagCountPerKey.compute(tag.name(), this::incrementStatistic);
        }
    }

    @Override
    public String toString() {
        return "OsmStatistic{" +
                "changesPerUser=" + changesPerUser +
                ", tagCountPerKey=" + tagCountPerKey +
                '}';
    }
}
