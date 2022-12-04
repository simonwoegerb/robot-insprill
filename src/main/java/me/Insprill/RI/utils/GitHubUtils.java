package me.Insprill.RI.utils;

//import org.kohsuke.github.GHOrganization;
//import org.kohsuke.github.GHRepository;
//import org.kohsuke.github.GitHub;
//import org.kohsuke.github.GitHubBuilder;

//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;

public class GitHubUtils {

    protected static final String oAuthToken = "[redacted]";
    private static final String dateFormatString = "MM/dd/yyyy HH:mm:ss";
    public static final int RATE_LIMITED = -2;
    public static final int ERROR = -1;
    public static final int SUCCESS = 0;
    public static final int ALREADY_ADDED = 1;


    /**
     * Checks if a user exists.
     *
     * @param username Name of user to check.
     * @return True if the user exists, false otherwise.
     */
    public static boolean userExists(String username) {
//        try {
//            GitHub github = new GitHubBuilder().withOAuthToken(oAuthToken).build();
//            System.out.println("name: " + github.getMyself().getName());
//            System.out.println("is it not broke: " + github.isCredentialValid());
//            return github.getUser(username) != null;
//        } catch (IOException exception) {
//            exception.printStackTrace();
//            return false;
//        }
        return false;
    }


    /**
     * Adds a user to a organization repository.
     *
     * @param organizationName Name of organization that holds the repository.
     * @param repoName         Name of the repository to add the user to.
     * @param username         The name of the user to add.
     * @return RATE_LIMITED - Bot is rate limited.<br>
     * ALREADY_ADDED - User is already added to the repository.<br>
     * ERROR - An error occurred while adding the user.<br>
     * SUCCESS - The user was added to the repository.<br>
     */
    public static int addMemberToOrganizationRepo(String organizationName, String repoName, String username) {
//        try {
//            GitHub github = new GitHubBuilder().withOAuthToken(oAuthToken).build();
//            if (!github.getRateLimit().isExpired())
//                return RATE_LIMITED;
//            GHOrganization organization = github.getOrganization(organizationName);
//            GHRepository repo = organization.getRepository(repoName);
//            if (github.getUser(username).isMemberOf(organization))
//                return ALREADY_ADDED;
//            repo.addCollaborators(github.getUser(username));
//        } catch (IOException exception) {
//            exception.printStackTrace();
//            return ERROR;
//        }
//        return SUCCESS;
        return -1;
    }


    /**
     * @return Formatted date when the Rate Limit will expire.
     */
    public static String getRateLimitExpire() {
//        try {
//            DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
//            GitHub github = new GitHubBuilder().withOAuthToken(oAuthToken).build();
//            return dateFormat.format(github.getRateLimit().getResetDate());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "ERROR";
//        }
        return null;
    }


}
