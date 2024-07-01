package tools.descartes.teastore.image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.descartes.teastore.image.setup.SetupController;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;


public class Main {

    public static File extractResourceToTempFile(String resourcePath) throws Exception {
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }

            // Create a temporary file
            File tempFile = File.createTempFile("database", ".db");
            tempFile.deleteOnExit();

            // Copy the InputStream to the temporary file
            try (OutputStream outStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }

            return tempFile;
        }
    }

    public static void main(String[] args) throws Exception {


        Logger log = LoggerFactory.getLogger(Main.class);
        log.info("Arguments: " + args.length);

        for (String arg : args) {
            log.info("Argument: " + arg);
        }

        if (args.length != 1) {
            log.error("Too many arguments. Only one argument is allowed.");
            System.exit(-1);
        }

        String dbSize = args[0];
        String dbPath = null;

        switch (dbSize){
            case "small":
                dbPath = "db-dumps/teadb_small.db";
                break;
            case "mid":
                dbPath = "db-dumps/teadb_mid.db";
                break;
            case "large":
                dbPath = "db-dumps/teadb_large.db";
                break;
            default:
                log.error("Only small - mid - large sizes are allowed");
                System.exit(-1);

        }

        File dbFile = Main.extractResourceToTempFile(dbPath);
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        SetupController.SETUP.deleteWorkingDir();
        SetupController.SETUP.createWorkingDir();
        SetupController.SETUP.generateImagesStatic(url);

        System.exit(0);


    }
}
