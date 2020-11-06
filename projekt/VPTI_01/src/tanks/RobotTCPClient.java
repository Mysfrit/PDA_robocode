package tanks;

import robocode.Robot;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

class RobotTCPClient extends Robot {
    public static void main(String argv[]) throws Exception {
            String sentence;
            String responseFromIAServer;
            String dataToSendFromRobocode2 = "100;100;image_data";
            String dataToSendFromRobocode = "hi";
            System.out.println("Sending data: "+dataToSendFromRobocode);
            InputStream inStream = new ByteArrayInputStream(dataToSendFromRobocode.getBytes(StandardCharsets.UTF_8));
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(inStream));
            Socket clientSocket = new Socket("localhost", 5000);

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            sentence = inFromUser.readLine();
            outToServer.writeBytes(sentence + 'n');
            responseFromIAServer = inFromServer.readLine();
            System.out.println("Action to do from server: " + responseFromIAServer);
            clientSocket.close();
        }

}