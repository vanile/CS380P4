import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;


public class Ipv6Client {

	public static void main(String[] args) {
		try(Socket socket = new Socket("18.221.102.182", 38004)) {
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			String hostAddress = socket.getInetAddress().getHostAddress();
			System.out.println("Connected to server.");

			byte packet[] = new byte[40];

			for(int counter = 1; counter < 13; counter++) {
				
				//Ipv6 Version 0110 + 0000 in int
				packet[0] = (byte) 96;

				//traffic class, flow label = 0; packet[1] ~ packet[3] = 0
				//Payload Length (Data length in bytes)
				int dataLength = (int) Math.pow(2, counter);
				byte lowerLoad = (byte) (dataLength & 0xFF);
				byte upperLoad = (byte) ((dataLength>>8) & 0xFF);

				packet[4] = upperLoad;
				packet[5] = lowerLoad;

				//Next Header => UDP (17 in int)
				//Hop Limit = 20
				packet[6] = (byte) 17;
				packet [7] = (byte) 20;

				//packet[8] ~ packet[17] = 0
				packet[18] = (byte) 255;
				packet[19] = (byte) 255;
				packet[20] = (byte) 127;
				packet[23] = (byte) 1;

				//Destination/Receiver IP Address
				String[] tempAddress = hostAddress.split("\\.");
				int destAddress[] = new int[4];

				for(int i = 0; i < tempAddress.length; i++) {
					int val = Integer.valueOf(tempAddress[i]);
					destAddress[i] = val;
				}

				packet[34] = (byte) 255;
				packet[35] = (byte) 255;
				packet[36] = (byte) destAddress[0];
				packet[37] = (byte) destAddress[1];
				packet[38] = (byte) destAddress[2];
				packet[39] = (byte) destAddress[3];

				for(byte b : packet) {
					os.write(b);
				}

				System.out.println("Data length: " + dataLength);
				for(int i = 0; i < dataLength; i++) {
					os.write(0);
				}

				System.out.print("Response: 0x");
				for(int j = 0; j < 4; j++) {
					System.out.printf("%02X", is.read());
				}
				System.out.println("\n");
			}
		} catch(Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		}
	}
}