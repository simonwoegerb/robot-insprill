package me.Insprill.RI.misc;

import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.storage.SaveAll;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;

import java.io.File;
import java.util.Scanner;

public class Console {
    public static void startConsoleListner(Scanner scanner) {
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            String base = input;
            if (input.contains(" "))
                base = input.substring(0, input.indexOf(' '));
            boolean hasArgs = input.contains(" --");
            String[] args = input.substring(input.indexOf('-') + 1).split(" ");
            switch (base) {

                case "stop":
                    RobotInsprill.stop(true);
                    if (hasArgs)
                        stop(args);
                    break;

                case "servers":
                    if (hasArgs)
                        servers(args);
                    else System.out.println("Usage: servers [--size | --list]");
                    break;

                case "tess":
                    if (hasArgs)
                        tess(args);
                    else System.out.println("Usage: tess [--update | --download]");
                    break;

                case "save":
                    if (hasArgs)
                        save(args);
                    else System.out.println("Usage: save [--all]");
                    break;

                default:
                    System.out.println("Commands:");
                    System.out.println("  - servers [--size | --list]");
                    System.out.println("  - tess [--update | --download]");
                    System.out.println("  - stop {--force}");
                    System.out.println("  - save [--all]");
                    break;
            }
        }
    }

    static void servers(String[] args) {
        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("-size")) {
                    System.out.println("Robot Insprill is currently in " + RobotInsprill.jda.getGuilds().size() + " servers!");
                    break;
                }
            case 2:
                if (args[0].equalsIgnoreCase("-list")) {
                    System.out.println("Robot Insprill is in the following servers:");
                    for (Guild guild : RobotInsprill.jda.getGuilds()) {
                        String name = guild.getName();
                        Invite invite = guild.retrieveInvites().complete().stream().findFirst().orElse(null);
                        String inviteUrl = invite == null ? "No Invite" : invite.getUrl();
                        System.out.println(" " + name + " - " + inviteUrl);
                    }
                }
                break;
        }
    }

    static void tess(String[] args) {
        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("-update")) {
                    RobotInsprill.tessDownloadInProgress = true;
                    System.out.println("Deleting old tess files...");
                    File tessFolder = new File("tessdata");
                    if (tessFolder.listFiles() != null) {
                        for (File f : tessFolder.listFiles()) {
                            f.delete();
                        }
                        File scriptFolder = new File("tessdata" + File.separator + "script");
                        if (scriptFolder.listFiles() != null) {
                            for (File f : scriptFolder.listFiles()) {
                                f.delete();
                            }
                            scriptFolder.delete();
                        }
                        tessFolder.delete();
                    }
                    RobotInsprill.executor.execute(RobotInsprill::downloadTess);
                    break;
                }
            case 2:
                if (args[0].equalsIgnoreCase("-download")) {
                    if (new File("tessdata").exists()) {
                        System.out.println("Tess files are already downloaded! Type \"tess --update\" to update them.");
                        return;
                    }
                    RobotInsprill.executor.execute(RobotInsprill::downloadTess);
                }
                break;
        }
    }

    static void save(String[] args) {
        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("-all")) {
                    SaveAll.saveAll();
                    System.out.println("All data saved!");
                }
                break;
        }
    }

    static void stop(String[] args) {
        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("-force")) {
                    RobotInsprill.stop(false);
                }
                break;
        }
    }

}
