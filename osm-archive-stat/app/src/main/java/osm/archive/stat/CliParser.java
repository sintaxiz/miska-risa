package osm.archive.stat;

import org.apache.commons.cli.*;

public class CliParser {
    private final Options options;

    private static final String FILE_OPTION = "f";
    private static final String IS_ARCHIVE_OPTION = "unpack";

    public CliParser() {
        options = new Options();
        options.addOption(FILE_OPTION, true, "path to the file with osm data");
        options.addOption(IS_ARCHIVE_OPTION, false, "option to archives");
    }

    public AppConfiguration parse(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        AppConfiguration config = new AppConfiguration();
        if (!cmd.hasOption(FILE_OPTION)) {
            return null;
        }
        config.setPath(cmd.getOptionValue(FILE_OPTION));
        config.setArchive(cmd.hasOption(IS_ARCHIVE_OPTION));
        return config;
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("osm-archive-stat", options);
    }
}
