package controllers;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import models.Fortune;
import models.World;
import models.WorldBean;
import play.Logger;
import play.db.jpa.JPAPlugin;
import play.jobs.Job;
import play.libs.F.Promise;
import play.mvc.Controller;

public class Application extends Controller {

	private static final int TEST_DATABASE_ROWS = 10000;
	private static final int TEST_FORTUNE_ROWS = 100;

	// FIXME: should this test be consistent - ie set seed or not?
	private static Random random = new Random();

	public static void index() {
		render();
	}

	public static void hello() {
		renderText("hello world");
	}

	public static void plaintext() {
		renderText("hello world");
	}

	public static void json() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("message", "Hello World!");
		renderJSON(result);
	}

	/**
	 * this version is used in the tests. it is the simplest and fastest.
	 * 
	 * @param queries
	 */
	public static void db(int queries) {
		if (queries == 0)
			queries = 1;
		final List<World> worlds = new ArrayList<World>();
		for (int i = 0; i < queries; ++i) {
			Long id = Long.valueOf(random.nextInt(TEST_DATABASE_ROWS) + 1);
			World result = World.findById(id);
			worlds.add(result);
		}
		renderJSON(worlds);
	}

	@play.db.jpa.NoTransaction
	public static void setupTx() {
		JPAPlugin plugin = play.Play.plugin(JPAPlugin.class);
		plugin.startTx(true);

		// clean out the old
		World.deleteAll();
		System.out.println("DELETED");
		// in with the new
		for (long i = 0; i <= TEST_DATABASE_ROWS; i++) {
			int randomNumber = random.nextInt(TEST_DATABASE_ROWS) + 1;
			new World(i, randomNumber).save();
			if (i % 100 == 0) {

				World.em().flush();
				World.em().clear();
				System.out.println("FLUSHED : " + i + "/" + TEST_DATABASE_ROWS);

			}
		}
		System.out.println("ADDED");
		plugin.closeTx(false);
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static void createBeanWorlds() {
		final int MAXROWS = 100;
		WorldBean[] items = new WorldBean[MAXROWS];
		for (int i = 0; i < MAXROWS; i++) {
			int randomNumber = random.nextInt(MAXROWS) + 1;
			WorldBean item = new WorldBean();
			item.setId(new Long(i));
			item.setRandomNumber(new Long(randomNumber));
			items[i] = item;
		}
		renderJSON(items);
	}

	/*
	 * 
	 * <li><a href="@{Application.db(queries:100)}">DB</a></li>
	<li><a href="@{Application.dbAsyncEachQuery(queries:100)}">DB asynchronous (slower)</a></li>
	<li><a href="@{Application.json}">JSON test</a></li>
	<li><a href="@{Application.setup}">populate db with Worlds and Fortunes</a></li>
	<li><a href="@{Application.hello">displays ""hello world"</a></li>
	<li><a href="@{Application.plaintext">displays ""hello world" (same as hello)</a></li>
	<li><a href="@{Application.staticplaintext">serves a static file containing</a></li>

	 */
	public static void createWorlds() {
		final int MAXROWS = 100;
		World[] items = new World[MAXROWS];
		for (int i = 0; i < MAXROWS; i++) {
			int randomNumber = random.nextInt(MAXROWS) + 1;
			World item = new World();
			item.id = new Long(i);
			item.randomNumber = new Long(randomNumber);
			items[i] = item;
		}
		renderJSON(items);
	}

	public static void setup() {

		// clean out the old
		World.deleteAll();
		System.out.println("(setup) Worlds deleted");
		// in with the new
		for (long i = 0; i <= TEST_DATABASE_ROWS; i++) {
			int randomNumber = random.nextInt(TEST_DATABASE_ROWS) + 1;
			new World(i, randomNumber).save();
			if (i % 100 == 0) {

				World.em().flush();
				World.em().clear();
				System.out.println("(setup) Worlds added : " + i + "/"
						+ TEST_DATABASE_ROWS);

			}

		}
		System.out.println("(setup) Worlds created");

		Fortune.deleteAll();
		System.out.println("(setup) Fortunes deleted");
		for (int i = 0; i <= TEST_FORTUNE_ROWS; i++) {
			int randomNumber = random.nextInt(TEST_FORTUNE_ROWS) + 1;

			byte[] bytes = ByteBuffer.allocate(4).putInt(randomNumber).array();
			byte[] hash;
			try {
				hash = MessageDigest.getInstance("MD5").digest(bytes);
				String message = bytesToHex(hash);
				new Fortune(i, message).save();

				if (i % 100 == 0) {

					Fortune.em().flush();
					Fortune.em().clear();
					System.out.println("(setup) Fortunes added : " + i + "/"
							+ TEST_FORTUNE_ROWS);

				}

			} catch (NoSuchAlgorithmException e) {
				Logger.fatal("Can't get MD5 digest", e);
				e.printStackTrace();
			}

		}
		System.out.println("(setup) Fortunes created");
	}

	/**
	 * note this is method is much slower than the synchronous version
	 */
	public static void dbAsyncEachQuery(int queries)
			throws InterruptedException, ExecutionException {
		if (queries == 0)
			queries = 1;
		final int queryCount = queries;
		List<Promise<World>> promises = new ArrayList<Promise<World>>();
		for (int i = 0; i < queryCount; ++i) {
			final Long id = Long
					.valueOf(random.nextInt(TEST_DATABASE_ROWS) + 1);
			Job<World> job = new Job<World>() {
				public World doJobWithResult() throws Exception {
					World result = World.findById(id);
					return result;
				};
			};
			promises.add(job.now());
		}
		List<World> result = await(Promise.waitAll(promises));
		renderJSON(result);
	}

	/**
	 * note this is method is a bit slower than the synchronous version
	 */
	public static void dbAsyncAllQueries(int queries)
			throws InterruptedException, ExecutionException {
		if (queries == 0)
			queries = 1;
		final int queryCount = queries;
		final List<World> worlds = new ArrayList<World>();
		Job<List<World>> job = new Job<List<World>>() {
			public java.util.List<World> doJobWithResult() throws Exception {
				for (int i = 0; i < queryCount; ++i) {
					Long id = Long
							.valueOf(random.nextInt(TEST_DATABASE_ROWS) + 1);
					World result = WorldBean.findById(id);
					worlds.add(result);
				}
				return worlds;
			};
		};
		List<World> result = job.now().get();
		renderJSON(result);
	}

	public static void update(int queries) {
		// Bounds check.
		if (queries > 500) {
			queries = 500;
		}
		if (queries < 1) {
			queries = 1;
		}
		final World[] worlds = new World[queries];

		// Run the query the number of times requested.
		for (int i = 0; i < queries; i++) {
			final long id = random.nextInt(TEST_DATABASE_ROWS) + 1;
			World item = World.findById(id);
			worlds[i] = item;
			item.randomNumber = new Long(random.nextInt(TEST_DATABASE_ROWS) + 1);
			item.save();
		}
		renderJSON(worlds);
	}

	public static void fortunes() {
		List<Fortune> items = Fortune.findAll();
		items.add(new Fortune(0, "Additional fortune added at request time."));
		Collections.sort(items);
	}

	public static void queries() {

	}
}