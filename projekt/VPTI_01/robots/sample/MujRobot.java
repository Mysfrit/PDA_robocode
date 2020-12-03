package sample;

import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MujRobot extends Robot {

	/**
	 * MyFirstRobot's run method - Seesaw
	 */

	public void run() {
		System.out.println("ON RUN");

		while (true) {
			try {
				Socket socket = new Socket("localhost", 50000);
				System.out.println("Connected!");

				// get the output stream from the socket.
				OutputStream outputStream = socket.getOutputStream();
				// create a data output stream from the output stream so we can send data through it
				DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

				System.out.println("Sending string to the ServerSocket");

				// write the message we want to send
				dataOutputStream.writeUTF("FUCK OFF SCUM");
				dataOutputStream.flush(); // send the message

				// get the input stream from the connected socket
				//InputStream inputStream = socket.getInputStream();
				// create a DataInputStream so we can read data from it.

				//DataInputStream dataInputStream = new DataInputStream(inputStream);

				//DataInputStream dIn = new DataInputStream(socket.getInputStream());

				////// read the message from the socket
				//var a = dIn.readUTF();
				// System.out.println(a);

				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				var sentence = inFromServer.readLine();

				socket.close();
				Thread.sleep(2000);
			} catch (IOException | InterruptedException ioException) {
				ioException.printStackTrace();
				System.out.println("I don't feel so good, Mr. Stark");
			}
		}
	}

	/**
	 * Fire when we see a robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(1);
	}

	/**
	 * We were hit!  Turn perpendicular to the bullet,
	 * so our seesaw might avoid a future shot.
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		turnLeft(90 - e.getBearing());
	}

	@Override
	public void onStatus(StatusEvent e) {
		System.out.println("ON STATUS");
		//String sentence;
		//String responseFromIAServer;
		//String dataToSendFromRobocode = "hi";
		//System.out.println("Sending data: "+dataToSendFromRobocode);
		//InputStream inStream = new ByteArrayInputStream(dataToSendFromRobocode.getBytes(StandardCharsets.UTF_8));
		//BufferedReader inFromUser = new BufferedReader(new InputStreamReader(inStream));
//
		//try {
		//	Socket clientSocket = new Socket("localhost", 50000);
		//	OutputStream outputStream = clientSocket.getOutputStream();
		//	// create a data output stream from the output stream so we can send data through it
		//	DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
		//	System.out.println("Sending string to the ServerSocket");
		//	// write the message we want to send
		//	dataOutputStream.writeUTF("FUCK OFF SCUM");
		//	dataOutputStream.flush(); // send the message
//
//
		//	//BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		//	//sentence = inFromUser.readLine();
		//	//responseFromIAServer = inFromServer.readLine();
		//	//System.out.println("Action to do from server: " + responseFromIAServer);
//
		//	clientSocket.close();
		//} catch (IOException ioException) {
		//	ioException.printStackTrace();
		//}
	}
}