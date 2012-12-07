import java.io.IOException;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;


public class GMCStart {
	private static String sIDN1="";
	private static final String myApiKey="AIzaSyCgbCcesCqLGdAFJtZxnbxMKhl4lcNBvcA";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		sIDN1=args[0];		
		Sender sender = new Sender(myApiKey);
		System.out.println("start send message"+sIDN1);
		//Message message = new Message.Builder().collapseKey("1").timeToLive(3).delayWhileIdle(true).addData("message",
		//  "this text will be seen in notification bar!!").build();
		Message message = new Message.Builder().addData("message",
		  "this text will be seen in notification bar!!").build();
		System.out.println("prepare send message");
		try {
			System.out.println("send message");
			Result result = sender.send(message, sIDN1, 5);
			System.out.println("result send message getMessageId: "+result.getMessageId());
			System.out.println("result send message getErrorCodeName: "+result.getErrorCodeName());
			System.out.println(result.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
