/*
 * www.Tief-Dunkel-Kalt.org
 *
 * Submarine Navigation Software
 *
 * Support for all sensors to collect and aggregate data and send to processing unit.
 *
 * "The Arduino use a hard coded I2C sensorBuffer size of 32 bytes.
 * If you send more than 32 bytes the Arduino will crash and you need to power cycle the board!
 *  I changed the sensorBuffer size from 32 bytes to 96 bytes, by editing those Arduino library files (make sure the Arduino IDE is closed):
 *
 *
 *  utility/twi.h:
 *  #define TWI_sensorBuffer_LENGTH 96 (was 32)
 *
 *  wire.h:
 * #define sensorBuffer_LENGTH 96 (was 32)
 *
 * Recompile your sketch and reupload it. "
 * From http://neophob.com/2013/04/i2c-communication-between-a-rpi-and-a-arduino/

 */
#include <SoftwareSerial.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_HMC5883_U.h>

Adafruit_HMC5883_Unified mag = Adafruit_HMC5883_Unified(12345);

//GPS Communication Constants
#define RX_PIN_GPS 9
#define TX_PIN_GPS 8
SoftwareSerial gpsSerial = SoftwareSerial(RX_PIN_GPS, TX_PIN_GPS);

// I2C Constants
#define ARDUINO_ADDR 0x04
#define GPS_SERIAL_SPEED 9600

// Serial port
#define SERIAL_SPEED 38400
#define DEBUG true

//current sensor buffer
const short MAX_MSG_SIZE = 80;
short currBufferSize = -1;
byte sensorBuffer[MAX_MSG_SIZE];

/*
 * Main setup routine
 */
void setup() {

  Serial.begin(SERIAL_SPEED);

  configureGPS();

  pinMode(12, OUTPUT);
  digitalWrite(10, LOW);

  mag.begin();

  // write protocol marker
  sensorBuffer[0] = '$';

  gpsSerial.listen();
}


/*
 * Main loop
 */
void loop() {
  //delay (30);
  short lastWritePos = 0;

  //GPS data
  if (gpsSerial.available() > 40) {
    lastWritePos = collectGPSData(sensorBuffer);
    calcChecksum(&sensorBuffer[1], lastWritePos);
    sendLastBuffer (lastWritePos + 3);
  }

  //Compass data
  lastWritePos = collectCompassData(sensorBuffer);
  calcChecksum(&sensorBuffer[1], lastWritePos);
  sendLastBuffer (lastWritePos + 3);

  //Leak detection
  lastWritePos = collectLeakData(sensorBuffer);
  calcChecksum(&sensorBuffer[1], lastWritePos);
  sendLastBuffer (lastWritePos + 3);

}



// Here, the last buffer shall be send via ttl
void sendLastBuffer (unsigned short lastWritePos) {

  if (Serial.available())  {
    for (unsigned short b = 0; b < lastWritePos; b++) {
      Serial.write(sensorBuffer[b]);
    }
  }
}

short collectLeakData (byte sensorBuffer  []) {
  sensorBuffer[1] = 'c';
  int sensorValue = analogRead(A0);
  String result = printDouble (sensorValue, 5);
  result.getBytes(&sensorBuffer[2], 5) ;
  sensorBuffer[6] = '*';
  return 6;
}

short collectCompassData (byte sensorBuffer  []) {

  sensors_event_t event;
  mag.getEvent(&event);
  float heading = atan2(event.magnetic.y, event.magnetic.x);

  float declinationAngle = 0.22;
  heading += declinationAngle;

  if (heading < 0)
    heading += 2 * PI;

  if (heading > 2 * PI)
    heading -= 2 * PI;

  float headingDegrees = heading * 180 / M_PI;

  uint8_t *p = (uint8_t*)&headingDegrees;

  sensorBuffer[1] = 'b';

  String result = printDouble (headingDegrees, 3);
  result.getBytes(&sensorBuffer[2], 5) ;
  sensorBuffer[5] = ',';
  result = printDouble (event.magnetic.x, 6);
  result.getBytes(&sensorBuffer[6], 4) ;
  sensorBuffer[10] = ',';
  result = printDouble (event.magnetic.y, 6);
  result.getBytes(&sensorBuffer[11], 4) ;
  sensorBuffer[15] = ',';
  result = printDouble (event.magnetic.z, 6);
  result.getBytes(&sensorBuffer[16], 4) ;
  sensorBuffer[20] = '*';
  return 20;
}


short collectGPSData (byte sensorBuffer []) {

  boolean started = false;
  boolean complete = false;

  short currentByteCount = 1;
  if (gpsSerial.available()) {
    while (!complete ) {
      char in = gpsSerial.read();
      if (in == '$') {
        sensorBuffer[currentByteCount++] = 'a';
        started = true;
      } else if (started && in > 31 && in < 128) {
        sensorBuffer[currentByteCount++] = in;
      }

      if (started) {
        complete = (in == '*' || currentByteCount > (MAX_MSG_SIZE - 5));
      }

    }
  }

  return currentByteCount;
}

void configureGPS() {

  gpsSerial.begin(GPS_SERIAL_SPEED);

  byte gpsSetSuccess = 0;

  //Generate the configuration string for Navigation Mode
  //Example received
  //      Sync   , class, id   , length      , mask (all) , dynM , fixM , fixedAlt (2D)             , fixedAltVar (2D)          , minEl,
  //  0xB5, 0x62 , 0x06 , 0x24 , 0x24 , 0x00 , 0xFF , 0xFF, 0x00 , 0x03 , 0x00 , 0x00 , 0x00 , 0x00 , 0x10 , 0x27 , 0x00 , 0x00 , 0x05 , 0x00 , 0xFA , 0x00 ,
  //      Sync   , class, id   , length      ,
  //  0xFA , 0x00 , 0x64 , 0x00 , 0x2C , 0x01 , 0x00 , 0x3C , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00

  // Setting to 'Sea' Mode
  byte setNav[] = { 0xB5, 0x62 , 0x06 , 0x24 , 0x24 , 0x00 , 0xFF , 0xFF,
                    0x05 , 0x01 , 0x00 , 0x00 , 0x00 , 0x00 , 0x10 , 0x27 , 0x00 , 0x00 , 0x05 , 0x00 , 0xFA , 0x00 , 0xFA , 0x00 ,
                    0x64 , 0x00 , 0x2C , 0x01 , 0x00 , 0x3C , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00
                  };
  calcChecksum(&setNav[2], sizeof(setNav) - 4);


  //Generate the configuration string for Data Rate
  byte setDataRate[] = { 0xB5, 0x62, 0x06, 0x08, 0x06, 0x00,
                         0xE8, 0x03, 0x01, 0x00, 0x01,
                         0x00, 0x00, 0x00
                       };
  calcChecksum(&setDataRate[2], sizeof(setDataRate) - 4);

  //Generate the configuration string for Baud Rate
  byte setPortRate[] = { 0xB5, 0x62, 0x06, 0x00, 0x14, 0x00, 0x01, 0x00, 0x00,
                         0x00, 0xD0, 0x08, 0x00, 0x00, 0x80, 0x25, 0x00, 0x00, 0x07, 0x00,
                         0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                       };
  calcChecksum(&setPortRate[2], sizeof(setPortRate) - 4);

  byte setGLL[] = { 0xB5, 0x62, 0x06, 0x01, 0x08, 0x00, 0xF0, 0x01, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x2B
                  };
  byte setGSA[] = { 0xB5, 0x62, 0x06, 0x01, 0x08, 0x00, 0xF0, 0x02, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x32
                  };
  byte setGSV[] = { 0xB5, 0x62, 0x06, 0x01, 0x08, 0x00, 0xF0, 0x03, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x01, 0x03, 0x39
                  };
  byte setRMC[] = { 0xB5, 0x62, 0x06, 0x01, 0x08, 0x00, 0xF0, 0x04, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x01, 0x04, 0x40
                  };
  byte setVTG[] = { 0xB5, 0x62, 0x06, 0x01, 0x08, 0x00, 0xF0, 0x05, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x04, 0x46
                  };

  while (gpsSetSuccess < 5) {

    while (gpsSetSuccess < 3) {
      sendUBX(&setDataRate[0], sizeof(setDataRate));  //Send UBX Packet
      gpsSetSuccess += getUBX_ACK(&setDataRate[2]); //Passes Class ID and Message ID to the ACK Receive function
      if (gpsSetSuccess == 5 | gpsSetSuccess == 6)
        gpsSetSuccess -= 4;
    }

    gpsSetSuccess = 0;

    sendUBX(&setNav[0], sizeof(setNav));  //Send UBX Packet
    gpsSetSuccess += getUBX_ACK(&setNav[2]); //Passes Class ID and Message ID to the ACK Receive function

    if (gpsSetSuccess == 6)
      gpsSetSuccess -= 4;
  }

  gpsSetSuccess = 0;
  while (gpsSetSuccess < 3) {
    sendUBX(setGLL, sizeof(setGLL));
    gpsSetSuccess += getUBX_ACK(&setGLL[2]);
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6)
      gpsSetSuccess -= 4;
  }
  gpsSetSuccess = 0;

  while (gpsSetSuccess < 3) {
    sendUBX(setGSA, sizeof(setGSA));
    gpsSetSuccess += getUBX_ACK(&setGSA[2]);
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6)
      gpsSetSuccess -= 4;
  }
  gpsSetSuccess = 0;

  while (gpsSetSuccess < 3) {
    sendUBX(setGSV, sizeof(setGSV));
    gpsSetSuccess += getUBX_ACK(&setGSV[2]);
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6)
      gpsSetSuccess -= 4;
  }

  gpsSetSuccess = 0;

  while (gpsSetSuccess < 3) {
    sendUBX(setRMC, sizeof(setRMC));
    gpsSetSuccess += getUBX_ACK(&setRMC[2]);
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6)
      gpsSetSuccess -= 4;
  }

  gpsSetSuccess = 0;

  while (gpsSetSuccess < 3) {

    sendUBX(setVTG, sizeof(setVTG));
    gpsSetSuccess += getUBX_ACK(&setVTG[2]);
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6)
      gpsSetSuccess -= 4;
  }

  gpsSetSuccess = 0;

}

void calcChecksum(byte * checksumPayload, byte payloadSize) {
  byte CK_A = 0, CK_B = 0;
  for (int i = 0; i < payloadSize; i++) {
    CK_A = CK_A + *checksumPayload;
    CK_B = CK_B + CK_A;
    checksumPayload++;
  }
  *checksumPayload = CK_A;
  checksumPayload++;
  *checksumPayload = CK_B;
}

void sendUBX(byte * UBXmsg, byte msgLength) {
  for (int i = 0; i < msgLength; i++) {
    gpsSerial.write(UBXmsg[i]);
    gpsSerial.flush();
  }

  gpsSerial.println();
  gpsSerial.flush();
}

byte getUBX_ACK(byte * msgID) {
  byte CK_A = 0, CK_B = 0;
  byte incoming_char;
  boolean headerReceived = false;
  unsigned long ackWait = millis();
  byte ackPacket[10] = { 0xB5, 0x62, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00
                       };
  int i = 0;
  while (1) {
    if (gpsSerial.available()) {

      incoming_char = gpsSerial.read();

      if (incoming_char == ackPacket[i]) {
        i++;
      } else if (i > 2) {
        ackPacket[i] = incoming_char;
        i++;
      }
    }
    if (i > 9)
      break;
    if ((millis() - ackWait) > 2500) {
      return 5;
    }
    if (i == 4 && ackPacket[3] == 0x00) {
      return 1;
    }
  }

  for (i = 2; i < 8; i++) {
    CK_A = CK_A + ackPacket[i];
    CK_B = CK_B + CK_A;
  }
  if (msgID[0] == ackPacket[6] && msgID[1] == ackPacket[7]
      && CK_A == ackPacket[8] && CK_B == ackPacket[9]) {
    printHex(ackPacket, sizeof(ackPacket));
    return 10;
  } else {
    printHex(ackPacket, sizeof(ackPacket));
    return 1;
  }
}

void printHex(uint8_t * data, uint8_t length) // prints 8-bit data in hex
{
  char tmp[length * 2 + 1];
  byte first;
  int j = 0;
  for (byte i = 0; i < length; i++) {
    first = (data[i] >> 4) | 48;
    if (first > 57)
      tmp[j] = first + (byte) 7;
    else
      tmp[j] = first;
    j++;

    first = (data[i] & 0x0F) | 48;
    if (first > 57)
      tmp[j] = first + (byte) 7;
    else
      tmp[j] = first;
    j++;
  }
  tmp[length * 2] = 0;
  for (byte i = 0, j = 0; i < sizeof(tmp); i++) {

    if (j == 1) {

      j = 0;
    } else
      j++;
  }

}

//
//  I2C
//



String printDouble(double val, byte precision) {
  // prints val with number of decimal places determine by precision
  // precision is a number from 0 to 6 indicating the desired decimial places
  // example: printDouble( 3.1415, 2); // prints 3.14 (two decimal places)

  String output = "";
  output += int(val);
  if ( precision > 0) {
    output += ".";
    unsigned long frac;
    unsigned long mult = 1;
    byte padding = precision - 1;
    while (precision--)
      mult *= 10;

    if (val >= 0)
      frac = (val - int(val)) * mult;
    else
      frac = (int(val) - val ) * mult;
    unsigned long frac1 = frac;
    while ( frac1 /= 10 )
      padding--;
    while (  padding--)
      output += "0";
    output += frac;
  }

  return output;
}

