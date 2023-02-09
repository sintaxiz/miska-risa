package osm.archive.stat.statistics;


import osm.archive.stat.node.Node;

import java.util.Comparator;
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
        StringBuilder changesPerUserSorted = new StringBuilder();
        changesPerUser.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(k -> changesPerUserSorted.append
                        (k.getKey()).append(": ").append(k.getValue()).append("\n"));

        StringBuilder tagCountPerKeyStr = new StringBuilder();
        tagCountPerKey.forEach((key, value) -> tagCountPerKeyStr.append
                (key).append(": ").append(value).append("\n"));

        return "OsmStatistic\n" +
                "changes per user\n" + changesPerUserSorted +
                "\n\ntag count per key\n" + tagCountPerKeyStr;
    }
}
