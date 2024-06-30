package tools.descartes.teastore.dbgenerator;

import jakarta.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final long DATABASE_OFFLINE_WAIT_MS = 2000;

    public static void main(String[] args) {
        String size = null;
        // print all the arguments
        LOG.info("Arguments: " + args.length);

        for (String arg : args) {
            LOG.info("Argument: " + arg);
        }

        if (args.length > 2) {
            LOG.error("Too many arguments. Only one argument is allowed.");
            System.exit(-1);
        }

        if (args.length == 1) {
            size = args[0];
        }

        if (args.length == 2) {
            size = args[1];
        }

        if (!size.equals("small") && !size.equals("large") && !size.equals("mid")) {
            LOG.error("Invalid argument. Only 'small', 'mid' and 'large' are allowed.");
            System.exit(-1);
        }

        waitForDatabase();
        if (DataGenerator.GENERATOR.isDatabaseEmpty()) {
            LOG.info("Database is empty. Generating new database content");
            DataGenerator.GENERATOR.generateDB(size);
        } else {
            LOG.info("Populated database found. Skipping data generation");
        }
        LOG.info("Persistence finished initializing database");
    }

    private static void waitForDatabase() {
        boolean databaseOffline = true;
        while (databaseOffline) {
            try {
                DataGenerator.GENERATOR.isDatabaseEmpty();
                databaseOffline = false;
            } catch (PersistenceException e) {
                LOG.warn("Exception connecting to database. Is database offline? Wating for "
                        + DATABASE_OFFLINE_WAIT_MS + " ms.");
                try {
                    Thread.sleep(DATABASE_OFFLINE_WAIT_MS);
                } catch (InterruptedException e1) {
                    LOG.error("Exception waiting for database to come online: " + e1.getMessage());
                }
            }
        }
    }
}