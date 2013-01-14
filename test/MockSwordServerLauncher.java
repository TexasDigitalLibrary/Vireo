import java.net.BindException;

import play.Logger;
import play.db.jpa.NoTransaction;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import edu.tamu.mocksword.server.MockSwordServer;

/**
 * Start and stop the mock sword server when the application comes up. This
 * class will ignore port binding exceptions, so it is possible to have two
 * instances of the application running at the same time. The first one to start
 * up will launch the mock sword server and all other instances on the machine
 * will use that one mock instance.
 * 
 * @author <a href="http://www.scottphillips.com">Scott Phillips</a>
 */
public class MockSwordServerLauncher {

	// The port on which the sword server will be operating. No one knows why we
	// picked this number.
	public static final int PORT = 8082;

	/**
	 * Start the sword server when the application starts.
	 */
	@OnApplicationStart
	@NoTransaction
	public static class startServer extends Job {

		public void doJob() throws Exception {
			try {
				MockSwordServer.start(8082);
				Logger.info("Mock Sword Server started on port: "+PORT);
			} catch (BindException be) {
				// If the port is already in use, then another thread is
				// running the sword server. It's okay, we'll ignore that error.
				// Everything else we send up the stack.
				Logger.info("Mock Sword Server failed to started on port "+PORT+" because it is already in use, ignoring.");
			}
		}

	}

//	/**
//	 * Stop the mock sword server when the application is shutting down.
//	 **/
//	@OnApplicationStop
//	@NoTransaction
//	public static class stopServer extends Job {
//
//		public void doJob() throws Exception {
//			try {
//				MockSwordServer.stop();
//				Logger.info("Mock Sword Server stopped.");
//			} catch (Throwable t) {
//				Logger.info("Unable to stop the Mock Sword Server, ignoring.");
//			}
//		}
//
//	}

}
