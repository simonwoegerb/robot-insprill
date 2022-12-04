package me.Insprill.RI.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

public class Logger {

    private static final Logger instance = new Logger();
    final String dateFormat = "yyyy/MM/dd HH:mm:ss";
    final File dir = new File("logs");
    public final ExecutorService logExecutor = Executors.newSingleThreadScheduledExecutor();
    String fileDateFormat;
    String logDateFormat;

    public Logger() {
        fileDateFormat = dateFormat;
        fileDateFormat = StringUtils.replace(fileDateFormat, "/", "-");
        fileDateFormat = StringUtils.replace(fileDateFormat, " ", "");
        fileDateFormat = StringUtils.replace(fileDateFormat, "H", "");
        fileDateFormat = StringUtils.replace(fileDateFormat, "m", "");
        fileDateFormat = StringUtils.replace(fileDateFormat, "s", "");
        fileDateFormat = StringUtils.replace(fileDateFormat, ":", "");
        logDateFormat = dateFormat;
        logDateFormat = StringUtils.replace(logDateFormat, "/", "");
        logDateFormat = StringUtils.replace(logDateFormat, " ", "");
        logDateFormat = StringUtils.replace(logDateFormat, "y", "");
        logDateFormat = StringUtils.replace(logDateFormat, "M", "");
        logDateFormat = StringUtils.replace(logDateFormat, "d", "");
        logDateFormat = StringUtils.replace(logDateFormat, "/", "");
    }

    public static Logger getInstance() {
        return instance;
    }

    /**
     * Add to the log file.
     *
     * @param msg  Action the player performed.
     * @param name Player's name.
     */
    public synchronized void addToLog(String msg, String name) {
        logExecutor.execute(() -> {
            try {
                dir.mkdirs();
                Date date = new Date();
                File logFile = new File(dir + File.separator + new SimpleDateFormat(fileDateFormat).format(date) + ".txt");
                String textToAppend = "[" + new SimpleDateFormat(logDateFormat).format(date) + "] " + name + ": " + msg + "\n";
                logFile.createNewFile();
                Files.write(logFile.toPath(), textToAppend.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * Delete log.
     *
     * @param days How many days to delete after.
     */
    public synchronized void delete(int days) {
        logExecutor.execute(() -> {
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File file : directoryListing) {
                    long diff = new Date().getTime() - file.lastModified();
                    if (diff > (long) days * 24 * 60 * 60 * 1000)
                        file.delete();
                }
            }
        });
    }

    /**
     * Compress log files after specific amount of days.
     *
     * @param days How many days old a file needs to be to get compressed.
     */
    public synchronized void compressLogs(int days) {
        logExecutor.execute(() -> {

            File[] directoryListing = dir.listFiles();
            if (directoryListing == null) return;
            for (File file : directoryListing) {
                if (file.getName().endsWith(".gz")) continue;
                long diff = new Date().getTime() - file.lastModified();
                if (diff > (long) days * 24 * 60 * 60 * 1000) {
                    byte[] buffer = new byte[1024];
                    try (GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(dir + File.separator + file.getName().replace(".txt", ".gz"))); FileInputStream fis = new FileInputStream(file)) {

                        int length;
                        while ((length = fis.read(buffer)) > 0)
                            gos.write(buffer, 0, length);

                        gos.finish();
                        fis.close();
                        file.delete();

                        System.out.println("Compressed " + file.getName() + "!");

                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        });
    }

}
