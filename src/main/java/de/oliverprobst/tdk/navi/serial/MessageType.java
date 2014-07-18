package de.oliverprobst.tdk.navi.serial;

public enum MessageType {

	NMEA_GGA((byte) 0x61), COURSE((byte) 0x62), LEAK((byte) 0x63), DEPTH(
			(byte) 0x69), TEMPERATURE((byte) 0x63), HUMIDITY((byte) 0x65), PITCH(
			(byte) 0x66), VOLTAGE((byte) 0x67), UNKNOWN((byte) 0x97);

	private final byte msgId;

	MessageType(byte b) {
		this.msgId = b;
	}

	/**
	 * @return the msgId
	 */
	public byte getMsgId() {
		return msgId;
	}

	public static MessageType getMessageType(byte b) {
		switch (b) {
		case 0x61:
			return MessageType.NMEA_GGA;
		case 0x62:
			return MessageType.COURSE;
		case 0x63:
			return MessageType.LEAK;
		default:
			return UNKNOWN;
		}
	}

}