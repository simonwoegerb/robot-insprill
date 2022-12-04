package me.Insprill.RI.custom.codedred;

import me.Insprill.RI.RobotInsprill;
import me.Insprill.RI.misc.IDs;
import me.Insprill.RI.misc.ThreadHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseError extends ListenerAdapter {

    private static final Pattern urlRegex = Pattern.compile("(https?|http?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    public static final ExecutorService imageParser = Executors.newCachedThreadPool(ThreadHandler.createThreadFactory("Image Parser - %d", Thread.MIN_PRIORITY));

    public static File downloadFile(Message.Attachment attachment) {
        try {
            File file = attachment.downloadToFile("temp" + File.separator + attachment.getFileName()).get();
            RobotInsprill.scheduler.schedule((Runnable) file::delete, 5, TimeUnit.MINUTES);
            return file;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.TEXT) return;
        if (!event.getGuild().getId().equals(IDs.CodedRedGuildId) && !event.getGuild().getId().equals(IDs.BotTestingGuildId))
            return;
        if (event.getAuthor().isBot()) return;
        MessageChannel channel = event.getChannel();
        if (channel.getId().equals(IDs.CodedRedMemes)
                || channel.getId().equals(IDs.CodedRedTechTalk)
                || channel.getId().equals(IDs.CodedRedGeneral)
                || channel.getId().equals(IDs.CodedRedShowcase)
                || channel.getId().equals(IDs.CodedRedDiscussion)
                || channel.getId().equals(IDs.CodedRedServerHelp1)
                || channel.getId().equals(IDs.CodedRedServerHelp2))
            return;

        RobotInsprill.executor.execute(() -> {
            String message = event.getMessage().getContentRaw();
            if (!event.getMessage().getAttachments().isEmpty()) {
                for (Message.Attachment attachment : event.getMessage().getAttachments()) {
                    String fileExtension = attachment.getFileExtension().toLowerCase();
                    if (fileExtension != null) {
                        if (fileExtension.contains("txt") || fileExtension.contains("log")) {
                            File file = downloadFile(attachment);
                            if (file == null) return;
                            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                                String line;
                                StringBuilder sb = new StringBuilder();
                                while ((line = br.readLine()) != null)
                                    sb.append(line);
                                file.delete();
                                parseError(channel, sb.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (fileExtension.contains("png") || fileExtension.contains("jpg") || fileExtension.contains("jpeg") || fileExtension.contains("bmp") || fileExtension.contains("wbmp") || fileExtension.contains("gif")) {
                            if (!new File("tessdata").exists() || new File("tessdata" + File.separator + "master.zip").exists()) {
                                if (RobotInsprill.tessDownloadInProgress) return;
                                if (!RobotInsprill.downloadTess()) return;
                            }
                            imageParser.execute(() -> {
                                File file = downloadFile(attachment);
                                if (file == null) return;
                                try (InputStream is = new FileInputStream(file)) {
                                    BufferedImage bi = ImageIO.read(is);
                                    Image newImage = bi.getScaledInstance(bi.getWidth() << 1, bi.getHeight() << 1, Image.SCALE_SMOOTH);
                                    bi = new BufferedImage(newImage.getWidth(null), newImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
                                    if (bi.getWidth() < 4 || bi.getHeight() < 4)
                                        return;
                                    Graphics bg = bi.getGraphics();
                                    bg.drawImage(newImage, 0, 0, null);
                                    bg.dispose();
                                    ITesseract tess = new Tesseract();
                                    tess.setDatapath("tessdata");
                                    tess.setLanguage("eng");
                                    tess.setTessVariable("user_defined_dpi", "70");
                                    file.delete();
                                    parseError(channel, tess.doOCR(bi));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }
            }

            Matcher m = urlRegex.matcher(message);

            while (m.find()) {
                String url = message.substring(m.start(), m.end());
                message = StringUtils.replace(message, url, "");
                m = urlRegex.matcher(message);
                parseError(channel, getFromUrl(url));
            }

            parseError(channel, message);
        });
    }

    boolean contains(String str, String contains) {
        str = StringUtils.replace(str, " ", "");
        contains = StringUtils.replace(contains, " ", "");
        str = StringUtils.replace(str, "\n", "");
        contains = StringUtils.replace(contains, "\n", "");
        return StringUtils.containsIgnoreCase(str, contains);
    }

    void parseError(MessageChannel channel, String message) {
        RobotInsprill.executor.execute(() -> {

            EmbedBuilder eb = new EmbedBuilder();


            // ------------------------------------------------------------------------------------------------ //
            //                                           Plugin.yml                                             //
            // ------------------------------------------------------------------------------------------------ //

            // Jar does not contain plugin.yml.
            if (contains(message, "FileNotFoundException: Jar does not contain plugin.yml")) {
                eb.setTitle("Looks like your `plugin.yml` doesn't exist, or is in the wrong location.");
                eb.setDescription("If you aren't using Maven or Gradle (if you don't know what they are you aren't using them),\n" +
                        "then make sure the `plugin.yml` is *just* in the `src` folder, **not in a package**.\n" +
                        "If you are using Maven or Gradle, then it should be in `src/main/java/resources`.\n" +
                        "You can find more information about the plugin.yml at https://www.spigotmc.org/wiki/plugin-yml/");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Uses the space-character.
            if (contains(message, "uses the space-character (0x20) in its name")) {
                eb.setTitle("You cannot use spaces in your plugin name. You may only use the following: `a-z,A-Z,0-9, _`.");
                eb.setDescription("You can find more information about the plugin.yml at https://www.spigotmc.org/wiki/plugin-yml/");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Ambiguous plugin name.
            if (contains(message, "Ambiguous plugin name") && contains(message, "for files")) {
                eb.setTitle("You have 2 plugins with the same name on your server.");
                eb.setDescription("This can happen when the name in the plugin.yml is the same between two plugins.\n" +
                        "You can find more information about the plugin.yml at https://www.spigotmc.org/wiki/plugin-yml/");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Authors are of wrong type.
            if (contains(message, "org.bukkit.plugin.InvalidDescriptionException: authors are of wrong type")) {
                eb.setTitle("Looks like you used `authors` without it being an array. If it's only one author, use `author`.");
                eb.setDescription("You can find more information about the plugin.yml at https://www.spigotmc.org/wiki/plugin-yml/");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Commands are of wrong type.
            if (contains(message, "org.bukkit.plugin.InvalidDescriptionException: commands are of wrong type") && contains(message, "ClassCastException: class java.lang.String cannot be cast to class java.util.Map")) {
                eb.setTitle("Seems like you forgot a `:` after a command in your plugin.yml.");
                eb.setDescription("Commands should look like ```yaml\n" +
                        "commands:\n" +
                        "  hello:\n" +
                        "  doctor:```\n" +
                        "You can find more information about the plugin.yml at https://www.spigotmc.org/wiki/plugin-yml/");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Unsupported API version.
            if (contains(message, "org.bukkit.plugin.InvalidPluginException: Unsupported API version")) {
                eb.setTitle("The API version you specified in your plugin.yml either doesn't exist, or is newer then your server version.");
                eb.setDescription("API versions only use major version numbers, (eg. 1.13, 1.14) starting at 1.13. Anything below 1.13 will not work.\n" +
                        "You can find more information about the plugin.yml at https://www.spigotmc.org/wiki/plugin-yml/");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Name is not defined.
            if (contains(message, "org.bukkit.plugin.InvalidDescriptionException: name is not defined")) {
                eb.setTitle("You need to specify your plugins name!");
                eb.setDescription("You can find more information about the plugin.yml at https://www.spigotmc.org/wiki/plugin-yml/");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Plugin has Minecraft, Mojang, or Bukkit in it's name.
            if (contains(message, "Could not load") && contains(message, "in folder 'plugins': Restricted Name")) {
                eb.setTitle("You cannot have `Minecraft`, `Mojang` or `Bukkit` in your plugins name.");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Load is not a valid choice.
            if (contains(message, "org.bukkit.plugin.InvalidDescriptionException: load is not a valid choice")) {
                eb.setTitle("Load must either be `STARTUP` or `POSTWORLD`.");
                eb.setDescription("You can find more information about the plugin.yml at https://www.spigotmc.org/wiki/plugin-yml/");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Cannot find main class.
            if (contains(message, "org.bukkit.plugin.InvalidPluginException: Cannot find main class")) {
                eb.setTitle("Looks like the server can't find the main class of your plugin.");
                eb.setDescription("Make sure the `main` key in your plugin.yml __**is typed correctly and DOESN'T HAVE ANY TYPOS**__ (***it's case-sensitive***), and it must also contain the name of the class, not just the package.\n" +
                        "The correct format is `main: <MainClassPackage>.<MainClass>`\n\n" +
                        "if you think it's right but you still get this error, **YOU HAVE A TYPO!!!!!** You just need to look *really* hard for it.\n\n" +
                        "You can find more information about the plugin.yml at https://www.spigotmc.org/wiki/plugin-yml/");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // TAB for indentation.
            if (contains(message, "that cannot start any token. (Do not use \\t(TAB) for indentation)")) {
                eb.setTitle("You can't use tabs in any .yml files");
                eb.setDescription("Just replace any tabs with 2 spaces and your good to go :)\n" +
                        "You can also run your file though https://www.yamlchecker.com/ and it will tell you what's wrong.");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            if (contains(message, "org.bukkit.plugin.InvalidPluginException: java.lang.IllegalArgumentException: Name cannot be null")) {
                eb.setTitle("Your plugin name cannot be null!");
                eb.setDescription("Make sure your name declaration looks like `name: PluginName`. Note that names can only contain letters, numbers, underscores, and hyphens.");
                channel.sendMessageEmbeds(eb.build()).queue();
            }


            // ------------------------------------------------------------------------------------------------ //
            //                                              Casting                                             //
            // ------------------------------------------------------------------------------------------------ //

            if (contains(message, "java.lang.ClassCastException")) {
                // ConsoleCommandSender -> Player.
                if (contains(message, "ConsoleCommandSender cannot be cast to") && contains(message, "Player")) {
                    eb.setTitle("Looks like you're trying to cast the server console to a Player!");
                    eb.setDescription("Before casting the commands sender to a player, you need to make sure it's a player.\n" +
                            "You can check if it's **not** a Player & return with the following code:" +
                            "```" +
                            "if (!(sender instanceof Player)) {\n" +
                            "    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + \"This command can't be executed from console!\");\n" +
                            "    return true;\n" +
                            "}```");
                    channel.sendMessageEmbeds(eb.build()).queue();
                }

                // OfflinePlayer -> Player.
                if (contains(message, "OfflinePlayer cannot be cast to") && contains(message, "Player")) {
                    eb.setTitle("Looks like you're trying to cast an OfflinePlayer to a Player.");
                    eb.setDescription("You can't do this, however, you can get a Player from an OfflinePlayer by doing `OfflinePlayer#getPlayer()`. \n" +
                            "But do note that if the player isn't online, it will return null.");
                    channel.sendMessageEmbeds(eb.build()).queue();
                }

            }


            // ------------------------------------------------------------------------------------------------ //
            //                                                Misc                                              //
            // ------------------------------------------------------------------------------------------------ //

            // Plugin already initialized.
            if (contains(message, "Plugin already initialized!")) {
                eb.setTitle("You're either extending JavaPlugin in more than one class, or created another instance of your main class.");
                eb.setDescription("Firstly, remove the extension or the creation of the new main class instance." +
                        " Then, you want to pass in an instance of the main class to the other class(es). To do this, for each class (other than main) add the following code: ```\n" +
                        "    private final Main plugin;\n" +
                        "    public ClassName(Main plugin) {\n" +
                        "        this.plugin = plugin;\n" +
                        "    }``` The `private final Main plugin;` lets you access the main class throughout the `ClassName` class." +
                        " You can use the `plugin` variable just like `this` if you were working in the main class." +
                        " The other method sets `plugin` to reference the Main class." +
                        " Then, in your main class, in your `onEnable` method add `new ClassName(this);`." +
                        " This initializes that class and runs any code in the previous method." +
                        " The `this` simply passes in an instance of the main class.");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Abnormal plugin type.
            if (contains(message, "org.bukkit.plugin.InvalidPluginException: Abnormal plugin type")) {
                eb.setTitle("It appears you have parameters in your onEnable/ onLoad. This is not allowed.");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Embedded resource cannot be found.
            if (contains(message, "The embedded resource '") && contains(message, ".yml' cannot be found in plugins")) {
                eb.setTitle("Looks like you forgot to add the file into your jar!");
                eb.setDescription("Just create a new file with the wanted name in the same way & location as your plugin.yml.");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Not a depend.
            if (contains(message, "Loaded class") && contains(message, "which is not a depend, softdepend or loadbefore of this plugin")) {
                eb.setTitle("When using plugin APIs, you need to add that plugin to your plugin.yml.");
                eb.setDescription("All you have to do is add ```yaml\n" +
                        "depend:\n" +
                        "  - OtherPlugin``` or ```yaml\n" +
                        "softdepend:\n" +
                        "  - OtherPlugin````depend` means that your plugin won't load without the other plugin installed.\n" +
                        "`softdepend` means that your plugin will work without the other installed.");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // No public constructor.
            if (contains(message, "org.bukkit.plugin.InvalidPluginException: No public constructor")) {
                eb.setTitle("It appears your Main class isn't public.");
                eb.setDescription("Your Main class needs to be public so the server can access its methods.\n" +
                        "To make it public, simply put `public` *before* `class` at the top of your class that extends JavaPlugin.\n" +
                        "It should then look like `public class ClassName extends JavaPlugin {`.");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            if (contains(message, "org.bukkit.plugin.InvalidPluginException: main class") && contains(message, "does not extend JavaPlugin")) {
                eb.setTitle("You forgot to extend `JavaPlugin` in your main class.");
                eb.setDescription("Your Main class needs to extend `JavaPlugin` for your plugin to work.\n" +
                        "To do this, simply add `extends JavaPlugin` to your class declaration.\n" +
                        "It should then look like `public class ClassName extends JavaPlugin {`.");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            //
            if (contains(message, "java.lang.IllegalArgumentException: Cannot make player fly if getAllowFlight() is false")) {
                eb.setTitle("You need to allow players to fly before you set them to flying!");
                eb.setDescription("You need to do `Player#setAllowFlight(true);` before setting the player to flying.");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Invalid Namespaced Key
            if (contains(message, "java.lang.IllegalArgumentException: Invalid key. Must be [a-z0-9/._-]:")) {
                String str = message.substring(StringUtils.indexOf(message, "[a-z0-9/._-]: ") + 14);
                eb.setTitle("\"" + str + "\" is an invalid Namespaced Key!");
                eb.setDescription("Like the error suggests, Namespaced Keys can only contain lowercase letters, numbers, underscores, hyphens, and forward slashes.\n" +
                        "You can ensure the key is lowercase by using `String#toLowerCase()`.\n" +
                        "You can also check if the key is valid by using the following code. ```java\n" +
                        "if (string.matches(\"a-z0-9\\/._-\") {\n" +
                        "  // key is valid\n" +
                        "} else {\n" +
                        "  // key is not valid\n" +
                        "}```");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Duplicate recipe ignored with ID.
            if (contains(message, "java.lang.IllegalArgumentException: NamespacedKey must be less than 256 characters")) {
                eb.setTitle("Namespaced Keys can't be longer then 256 characters.");
                eb.setDescription("You need to trim down the length so it is 256 characters or less.");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Duplicate recipe ignored with ID.
            if (contains(message, "java.lang.IllegalStateException: Duplicate recipe ignored with ID")) {
                eb.setTitle("You can't have multiple recipes with the same id.");
                eb.setDescription("This error will also be thrown if you re-register it. Make sure to check if it's registered already before registering it.");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Compiled by a more recent version of the Java Runtime.
            if (contains(message, "has been compiled by a more recent version of the Java Runtime") && contains(message, "this version of the Java Runtime only recognizes class file versions up to")) {
                eb.setTitle("It appears you compiled your plugin with a Java version newer than what you have installed.");
                eb.setDescription("Please recompile your plugin with the same version of Java your server is running.\n" +
                        "You can compile with a version lower then your server's, but you can't compile with a newer version. " +
                        "If your unsure of your Java version, you can compile with Java 8 as its the most universally compatible.\n" +
                        "How to change compile version: \n" +
                        "  Eclipse: https://www.codejava.net/ides/eclipse/change-java-compiler-version-for-eclipse-project \n" +
                        "  IntelliJ: https://mkyong.com/intellij/how-to-change-the-intellij-idea-jdk-version/ (you don't need to change SDK if it's newer then 7)\n" +
                        "\n" +
                        "If you have Java 7 or lower installed, you need to download Java 8. Download it here: https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html.");
                channel.sendMessageEmbeds(eb.build()).queue();
            }

            // Lists
            if (contains(message, "java.lang.IndexOutOfBoundsException")) {
                eb.setTitle("Looks like you're trying to access an element of a List that doesn't exist.");
                int index = Integer.MIN_VALUE;
                int size = Integer.MIN_VALUE;

                // Get & set "Index" number.
                if (message.contains("Index: ")) {
                    String indexString = message.substring(StringUtils.indexOf(message, "Index: ") + 7);
                    index = getIndexSize(index, indexString);
                }

                // Get & set "Size" number.
                if (message.contains("Size: ")) {
                    String sizeString = message.substring(StringUtils.indexOf(message, "Size: ") + 6);
                    size = getIndexSize(size, sizeString);
                }

                String listsStartFrom0 = "Remember that Lists start from 0. So the first element is '0', the second is '1', and so on." +
                        " You can always check the list's size by doing List#size(), just remember that that returns the size starting from '1', not '0'.";

                // No index or size.
                if (index == Integer.MIN_VALUE && size == Integer.MIN_VALUE) {
                    eb.setDescription("Make sure to check if the element exists before trying to get it." +
                            " If there is more to the error, please send the whole thing so i can give you more help.");

                    // Size but no index.
                } else if (index == Integer.MIN_VALUE && size != Integer.MIN_VALUE) {
                    eb.setDescription("You are trying to get an element at an index that doesn't exit. The list only contains '" + size + "' elements" +
                            " so hopefully that can give you a starting point." + listsStartFrom0);

                    // Index but no size.
                } else if (index != Integer.MIN_VALUE && size == Integer.MIN_VALUE) {
                    eb.setDescription("You are trying to get an element at index '" + index + "' that doesn't exit. " + listsStartFrom0);

                    // Size of 0.
                } else if (size == 0) {
                    eb.setDescription("You are trying to get element at index '" + index + "', however the list is empty." +
                            " You can check if the list is empty by doing `List#isEmpty()`.");

                } else if (index >= 0 && size >= 1 && size > index) // This should *never* happen unless someone is messing with the bot.
                {
                    eb.setDescription("uhhhhh");
                    eb.setImage("https://imgur.com/mxQu3sA.png");

                    // Anything else.
                } else {
                    eb.setDescription("You are trying to get the element at index '" + index + "', however, the List only contains '" + size + "' elements. " + listsStartFrom0);
                }

                channel.sendMessageEmbeds(eb.build()).queue();
            }

        });
    }

    private int getIndexSize(int index, String indexString) {
        Pattern pattern = Pattern.compile("-?\\d+");
        Matcher matcher = pattern.matcher(indexString);
        if (matcher.find())
            indexString = indexString.substring(matcher.start(), matcher.end());
        if (isInteger(indexString)) {
            int tempIndex = Integer.parseInt(indexString);
            index = (tempIndex == Integer.MIN_VALUE) ? tempIndex + 1 : tempIndex;
        }
        return index;
    }

    private String getFromUrl(String url) {
        try {
            if ((!url.contains("https://pastebin.com/")
                    && (!url.contains("https://hastebin.com/"))
                    && (!url.contains("https://paste.md-5.net/"))
                    && (!url.contains("https://paste.helpch.at/"))
                    && (!url.contains("https://paste.insprill.net/"))
                    && (!url.contains("https://sourceb.in/"))))
                return "";
            if (url.contains("https://hastebin.com/") && !url.contains("https://hastebin.com/raw/"))
                url = StringUtils.replace(url, "https://hastebin.com/", "https://hastebin.com/raw/");

            if (url.contains("https://paste.md-5.net/") && !url.contains("https://paste.md-5.net/raw/"))
                url = StringUtils.replace(url, "https://paste.md-5.net/", "https://paste.md-5.net/raw/");

            if (url.contains("https://paste.helpch.at/") && !url.contains("https://paste.helpch.at/raw/"))
                url = StringUtils.replace(url, "https://paste.helpch.at/", "https://paste.helpch.at/raw/");

            if (url.contains("https://paste.insprill.net/") && !url.contains("https://paste.insprill.net/raw/"))
                url = StringUtils.replace(url, "https://paste.insprill.net/", "https://paste.insprill.net/raw/");

            if (url.contains("https://pastebin.com/") && !url.contains("https://pastebin.com/raw/"))
                url = StringUtils.replace(url, "https://pastebin.com/", "https://pastebin.com/raw/");

            if (url.contains("https://sourceb.in/"))
                url = StringUtils.replace(url, "https://sourceb.in/", "https://cdn.sourceb.in/bins/");


            URLConnection urlCon = new URL(url).openConnection();
            urlCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()))) {
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null)
                    sb.append(line);
                return sb.toString();
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    /**
     * Checks whether a string is a valid integer.
     *
     * @param string String to check.
     * @return True if the string is a valid integer, false otherwise.
     */
    private boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

}
