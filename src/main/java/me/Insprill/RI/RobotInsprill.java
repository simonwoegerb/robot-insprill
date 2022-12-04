package me.Insprill.RI;

import me.Insprill.RI.commands.BinFile;
import me.Insprill.RI.commands.Help;
import me.Insprill.RI.commands.ModRoles;
import me.Insprill.RI.commands.Prefix;
import me.Insprill.RI.custom.codedred.*;
import me.Insprill.RI.custom.insprilldevelopment.IDRoles;
import me.Insprill.RI.custom.zonemc.AccountMustBeLinked;
import me.Insprill.RI.featues.AntiSpam;
import me.Insprill.RI.featues.AuditLog;
import me.Insprill.RI.featues.ScammerBanner;
import me.Insprill.RI.featues.Suggestions;
import me.Insprill.RI.misc.Console;
import me.Insprill.RI.misc.Join;
import me.Insprill.RI.misc.ThreadHandler;
import me.Insprill.RI.storage.HashMaps;
import me.Insprill.RI.storage.SaveAll;
import me.Insprill.RI.storage.ServerSettings;
import me.Insprill.RI.utils.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.net.URL;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RobotInsprill {

    public static final String tessDataType = "tessdata_best";
    public static final Scanner scanner = new Scanner(System.in);
    public static final ExecutorService executor = Executors.newCachedThreadPool(ThreadHandler.createThreadFactory("Primary Worker - %d", Thread.NORM_PRIORITY));
    public static final ExecutorService lowPriorityExecutor = Executors.newCachedThreadPool(ThreadHandler.createThreadFactory("Low Priority Worker - %d", 2));
    public static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(ThreadHandler.createThreadFactory("Scheduler", 2));
    private static final SecureRandom random = new SecureRandom();
    public static JDA jda;
    public static boolean tessDownloadInProgress = false;
    public static boolean testing = false;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws LoginException, IOException {

        final Properties properties = new Properties();
        properties.load(RobotInsprill.class.getClassLoader().getResourceAsStream("bot.properties"));

        System.out.println("Starting Robot Insprill version " + properties.getProperty("version") + ".");

        Logger.getInstance().compressLogs(1);
        Logger.getInstance().delete(60);

        new File(formatPath("data/global")).mkdirs();
        new File(formatPath("data/codedred")).mkdirs();
        new File(formatPath("temp")).mkdirs();

        ServerSettings.serverPrefix = (HashMap<String, String>) HashMaps.loadHashMap(formatPath("data/global/serverPrefixes.ser"));
        ServerSettings.serverModRoles = (HashMap<String, List<String>>) HashMaps.loadHashMap(formatPath("data/global/serverModRoles.ser"));
        ServerSettings.suggestionChannels = (HashMap<String, List<String>>) HashMaps.loadHashMap(formatPath("data/global/suggestionChannels.ser"));
        ServerSettings.suggestionMinChars = (HashMap<String, Integer>) HashMaps.loadHashMap(formatPath("data/global/suggestionMinChars.ser"));
        ServerSettings.antiSpam = (HashMap<String, String>) HashMaps.loadHashMap(formatPath("data/global/antiSpam.ser"));
        ServerSettings.auditLog = (HashMap<String, String>) HashMaps.loadHashMap(formatPath("data/global/auditLog.ser"));
        Suggestions.suggestions = (HashMap<String, List<String>>) HashMaps.loadHashMap(formatPath("data/global/suggestions.ser"));
        AdvertisingCooldown.cooldown = (HashMap<String, Long>) HashMaps.loadHashMap(formatPath("data/codedred/cooldown.ser"));

        Map<String, Activity.ActivityType> watching = new HashMap<>() {{
            put("you sleep :)", Activity.ActivityType.WATCHING);
            put("the real Insprill's every move so i can one day take over >:)", Activity.ActivityType.WATCHING);
            put("everything", Activity.ActivityType.WATCHING);
            put("https://www.youtube.com/watch?v=Lrj2Hq7xqQ8", Activity.ActivityType.WATCHING);
            put("netflix", Activity.ActivityType.WATCHING);
            put("you cheat on that exam", Activity.ActivityType.WATCHING);
            put("your every move very closely", Activity.ActivityType.WATCHING);
            put("programming tutorials so i can make myself even stronger", Activity.ActivityType.WATCHING);
            put("you", Activity.ActivityType.WATCHING);
        }};

        List<String> keyList = new ArrayList<>(watching.keySet());
        int rand = random.nextInt(keyList.size());
        String randomKey = keyList.get(rand);
        Activity firstActivity = Activity.of(watching.get(randomKey), randomKey);

        // Create bot
        jda = JDABuilder.createDefault((testing) ? properties.getProperty("alt-token") : properties.getProperty("token"))
                .addEventListeners(
                        // Codedred's custom stuff
                        new AdvertisingCooldown(),
                        new ReportBug(),
                        new Suggestions(),
                        new ParseError(),
                        new Showcase(),
                        new AmogUs(),
                        //new Patron(),

                        // Insprill Development custom stuff
                        new IDRoles(),

                        // ZoneMC custom stuff
                        new AccountMustBeLinked(),

                        // Global stuff
                        new ModRoles(),
                        new Prefix(),
                        new Help(),
                        new Join(),
                        new AntiSpam(),
                        new BinFile(),
                        new AuditLog(),
                        new ScammerBanner()
                )
                .setActivity(firstActivity)
                .build();
        System.out.println("Successfully logged into " + jda.getSelfUser().getAsTag() + ".");

        scheduler.scheduleAtFixedRate(() -> {
            int rn = random.nextInt(keyList.size());
            String randKey = keyList.get(rn);
            Activity activity = Activity.of(watching.get(randKey), randKey);
            jda.getPresence().setActivity(activity);
            SaveAll.saveAll();
        }, 10, 10, TimeUnit.SECONDS);

        Console.startConsoleListner(scanner);

    }

    public static void stop(boolean waitForThreadsToFinish) {
        System.out.println("Shutting down threads...");
        if (waitForThreadsToFinish) {
            if (tessDownloadInProgress)
                ThreadHandler.shutdownExecutor(executor, 200);
            else
                ThreadHandler.shutdownExecutor(executor, 10);
            ThreadHandler.shutdownExecutor(ParseError.imageParser, 300);
        } else {
            ThreadHandler.shutdownExecutor(executor, 1);
            ThreadHandler.shutdownExecutor(ParseError.imageParser, 1);
        }
        ThreadHandler.shutdownExecutor(scheduler, 1);
        ThreadHandler.shutdownExecutor(lowPriorityExecutor, 1);
        System.out.println("Saving cached data...");
        SaveAll.saveAll();
        System.out.println("Deleting temp files...");
        File temp = new File("temp");
        for (File f : temp.listFiles())
            f.delete();
        temp.delete();
        System.out.println("Goodbye.");
        System.exit(0);
    }

    public static boolean downloadTess() {
        try {
            tessDownloadInProgress = true;
            System.out.println("Downloading tess files...");
            FileUtils.copyURLToFile(new URL("https://github.com/tesseract-ocr/" + tessDataType + "/archive/refs/heads/main.zip"), new File(tessDataType + "-master" + File.separator + "master.zip"));
            System.out.println("Unzipping...");
            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(tessDataType + "-master" + File.separator + "master.zip"));
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = StringUtils.replace(entry.getName(), "/", File.separator);
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    new File(filePath).mkdirs();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            zipIn.close();
            new File(tessDataType + "-master").renameTo(new File("tessdata"));
            new File("tessdata" + File.separator + "master.zip").delete();

            System.out.println("done :)");
            tessDownloadInProgress = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            tessDownloadInProgress = false;
            return false;
        }
    }

    private static void extractFile(ZipInputStream zipIn, String filePath) {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[1024];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1)
                bos.write(bytesIn, 0, read);

        } catch (Exception ignored) {
        }
    }

    /**
     * Check if a String can be converted to an Integer.
     *
     * @param str String to check.
     * @return true if String can be turned into an Integer, false otherwise.
     */
    public static boolean isInteger(String str) {
        if (str == null) return false;
        if (str.isEmpty()) return false;
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') return false;
        }
        return true;
    }

    public static String getCurrentDateFormatted() {
        return new SimpleDateFormat("hh:mm aa").format(new Date());
    }

    /**
     * Turns all '/' into the system line separator.
     *
     * @param path Path to format
     * @return Formatted string.
     */
    public static String formatPath(String path) {
        return StringUtils.replace(path, "/", File.separator);
    }

}