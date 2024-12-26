package net.abhinav.clumps;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerManager {

    private final Logger logger;
    private final File logFile;
    private final PrintWriter logWriter;

    public LoggerManager(Clumps plugin) {
        // Create logger
        logger = Logger.getLogger("Clumps");
        logger.setLevel(Level.ALL);

        // Set up console handler for log output
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);

        // Create logs directory inside the plugin's data folder (not the main directory)
        File logsDir = new File(plugin.getDataFolder(), "logs"); // Access plugin's data folder and append 'logs' directory
        if (!logsDir.exists()) {
            boolean dirCreated = logsDir.mkdirs(); // Ensure the directory is created if it doesn't exist
            if (!dirCreated) {
                logger.severe("Failed to create logs directory.");
            }
        }

        // Create log file with timestamp in the 'logs' directory
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        logFile = new File(logsDir, "run_" + timestamp + ".log");

        try {
            logWriter = new PrintWriter(new FileWriter(logFile, true));
            logWriter.println("Log started at " + timestamp);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to create log file.");
        }
    }

    public void log(String message) {
        // Log to the console
        logger.info(message);

        // Also log to the log file with timestamp
        logWriter.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " - " + message);
        logWriter.flush();
    }

    public void close() {
        logWriter.close();
    }
}
