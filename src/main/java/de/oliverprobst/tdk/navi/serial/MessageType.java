package de.oliverprobst.tdk.navi.serial;

public enum MessageType {

	NMEA_GGA((byte) 0x61), COURSE((byte) 0x62), LEAK((byte) 0x63), TEMPERATURE(
			(byte) 0x64), SPEED((byte) 0x65), DEPTH((byte) 0x69), HUMIDITY(
			(byte) 0x65), PITCH((byte) 0x66), VOLTAGE((byte) 0x67), SHUTDOWN((byte) 0x7A), UNKNOWN((byte) 0x97);

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
		case 0x64:
			return MessageType.TEMPERATURE;
		case 0x65:
			return MessageType.SPEED;
		case 0x67:
			return MessageType.VOLTAGE;
		case 0x7A:
			return MessageType.SHUTDOWN;
		default:
			return UNKNOWN;
		}
	}

}
