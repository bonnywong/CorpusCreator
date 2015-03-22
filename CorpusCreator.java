import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.github.jreddit.entity.Comment;
import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;
import com.github.jreddit.retrieval.Comments;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.CommentSort;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;

/**
 * Extracts submissions and comments from subreddits using Reddit's API via the
 * jReddit library and writes them to file.
 * 
 * A minor change was added to jReddit, to allow for properly sorted
 * submissions.
 * 
 * @author bwong
 * 
 */
public class KorpusCreator {
	public static final String SUBREDDIT = "AdviceAnimals"; 
	public static RestClient restClient;
	public static BufferedWriter bw;
	public static User user;

	public CorpusCreator() throws IOException {
		restClient = new HttpRestClient();
		restClient.setUserAgent("Your unique Useragent."); //Make sure to use a unique agent or face a ban. 
		user = new User(restClient, "your_Reddit_username", "your_password"); 
		File file = new File(SUBREDDIT);
		FileWriter fw = new FileWriter(file);
		bw = new BufferedWriter(fw);
	}

	public static void main(String[] args) throws IOException {
		new CorpusCreator();
		getSubmissions(SUBREDDIT, 60); 
		bw.close();
	}

	/**
	 * Retrieves the submissions and the comments from the specified subreddit.
	 * 
	 * @param subReddit The targetted subreddit.
	 * @param amount The amount of submissions to be requested. 
	 * @throws IOException
	 */
	public static void getSubmissions(String subReddit, int amount) throws IOException {
		Submissions subms = new Submissions(restClient, user);
		Comments comments = new Comments(restClient, user);
		List<Submission> submissionsSubreddit = subms.ofSubreddit(subReddit,
				SubmissionSort.TOP, -1, 60, null, null, true);

		for (Submission sub : submissionsSubreddit) {
			List<Comment> commentsFromArticle = comments.ofSubmission(sub,
					null, -1, 8, 10000, CommentSort.TOP);
			String s = sub.getSelftext() + " ";
			for (Comment c : commentsFromArticle) {
				s += c.getBody() + " ";
			}
			bw.write(s);
			// writeString(s, sub.getAuthor());
		}
	}

	/**
	 * Creates a new file and writes 
	 * a string to it. 
	 * 
	 * @param s String to be written
	 * @param fileName Name of the file
	 * @throws IOException
	 */
	public static void writeString(String s, String fileName)
			throws IOException {
		File file = new File(fileName);
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(s);
		bw.close();
		System.out.println("Finished writing");
	}

}
