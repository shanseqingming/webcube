package cn.edu.zju.webcube.shared.msg;

public interface Message {
	
	/**
	 * parse a string and generate the message object
	 * @param serializeData
	 */
	public void parse(String serializedData);
	
	/**
	 * 
	 * @return a serialized representation of the messages
	 */
	public String serialize();
}
