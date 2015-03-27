/*
 * www.Tief-Dunkel-Kalt.org
 *
 * Submarine Navigation Software
 *
 * Support for all sensors to collect and aggregate data and send to processing unit.
 *
 *
 *
 * Changed in SoftwareSerial:
 * #define _SS_MAX_RX_BUFF 128 // RX buffer size
 * Reason is the NMEA String, which should be send as a whole.
 */
#include <SoftwareSerial.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_HMC5883_U.h>
#include <Adafruit_BMP085.h>

/*
* Compass Module API
*/
Adafruit_HMC5883_Unified mag = Adafruit_HMC5883_Unified(12345);

/*
* Pressure and Temperature senor module
*/
Adafruit_BMP085 bmp;

/*
* GPS Communication Constants
*/
#define RX_PIN_GPS 8
#define TX_PIN_GPS 9
SoftwareSerial gpsSerial = SoftwareSerial(RX_PIN_GPS, TX_PIN_GPS);

/*
* Speed for software serial to connect to GPS.
* Because GPS is configured to a Hz, there is no need for a high speed.
*/
#define GPS_SERIAL_SPEED 9600

/*
* Serial connectivity to raspberry pi.
*/
#define SERIAL_SPEED 115200

/*
* This is the global storage for the current sensor to read.
* It is used by all sensors, except the GPS.
*/
const short MAX_MSG_SIZE = 80;
short currBufferSize = -1;
byte sensorBuffer[MAX_MSG_SIZE];


/*
* This is the storage for the GPS sensor only.
*/
const short MAX_GPS_MSG_SIZE = 80;
short currGpsBufferSize = 1;
byte gpsSensorBuffer[MAX_GPS_MSG_SIZE];
boolean gpsReceivedCompleteMsg = false;
boolean gpsStringStarted = false;

/*
* Count the iterations of the main loop.
* Used for some actions not executed every loop.
*/
int loopCounter = 0;

/*
* Timeout for Off switch.
* If the Off button is pushed and no shutdown command is received from arduino,
* this timeout applies.
*/
long shutdownTimeout = 0;

/*
* Stores the checksum of the last send message for each
* message type. Prevents sending same measurement twice.
*/
byte lastSend [9][2];

byte ambientPressureOnStartup = 0;

/*
 * Main setup routine
 */
void setup() {

  Serial.begin(SERIAL_SPEED);

   
   
  
 configureGPS();
  
  

  // LED indicating Serial connectivity between arduino and pi
  pinMode(12, OUTPUT);

  pinMode(13, OUTPUT);

  //GPS standby
  pinMode(10, OUTPUT);
  digitalWrite(10, LOW);

  // Shutdown send
  pinMode(2, OUTPUT);

  // On-/Off switch
  pinMode(3, INPUT);

  // Current battery voltage
  digitalWrite(2, LOW);

  mag.begin();

  bmp.begin();

  // write protocol marker
  sensorBuffer[0] = '$';
  gpsSensorBuffer[0] = '$';

  gpsSerial.listen();

  ambientPressureOnStartup = analogRead (A3);

}


/*
 * Main loop
 */
void loop() {
  short lastWritePos = 0;
  delay (2);
  //GPS data
  currGpsBufferSize = collectGPSData(gpsSensorBuffer, currGpsBufferSize);

  if (loopCounter % 127 == 0) {
    digitalWrite(13, HIGH);
  } else if (loopCounter % 277 == 0) {
    digitalWrite (13, LOW);
  }


//if (loopCounter % 500 == 0) {
 // byte req[] = { 0xB5, 0x62 , 0x06 , 0x24 };
  //calcChecksum(&req[2], sizeof(req) - 4);
  //sendUBX(&req[0], sizeof(req));
 // Serial.println ("SENDED REQ");

//}
if (gpsReceivedCompleteMsg) {
    calcChecksum(&gpsSensorBuffer[1], currGpsBufferSize - 1);
    
    //Serial.print("GPS: ");
    //for (unsigned short b = 0; b < currGpsBufferSize; b++) {
    //   Serial.write(gpsSensorBuffer[b]);
    //}
    // Serial.println("");
     
    if (currGpsBufferSize > 8 && gpsSensorBuffer[6] == 'A' && gpsSensorBuffer[5] == 'G'){
      sendLastBuffer (gpsSensorBuffer, currGpsBufferSize);
    }
    gpsReceivedCompleteMsg = false;
    currGpsBufferSize = 1;
    gpsStringStarted = false;
  }

  //Compass data
  lastWritePos = collectCompassData(sensorBuffer);
  calcChecksum(&sensorBuffer[1], lastWritePos);
  sendLastBuffer (sensorBuffer, lastWritePos);

  //Depth
  if (loopCounter % 50 == 0) {
    lastWritePos = collectDepthData(sensorBuffer);
    calcChecksum(&sensorBuffer[1], lastWritePos);
    sendLastBuffer (sensorBuffer, lastWritePos);
  }
  
  //Leak detection
  if (loopCounter % 500 == 0) {
    lastWritePos = collectLeakData(sensorBuffer);
    calcChecksum(&sensorBuffer[1], lastWritePos);
    sendLastBuffer (sensorBuffer, lastWritePos);
  }

  //Temperature
  if (loopCounter % 1000 == 0) {
    lastWritePos = collectTemperatureData(sensorBuffer);
    calcChecksum(&sensorBuffer[1], lastWritePos);
    sendLastBuffer (sensorBuffer, lastWritePos);
  }


  //Voltage
  if (loopCounter % 100 == 0) {
    lastWritePos = collectVoltageData(sensorBuffer);
    calcChecksum(&sensorBuffer[1], lastWritePos);
    sendLastBuffer (sensorBuffer, lastWritePos);
  }
  
   //Off Button
  lastWritePos = checkOffButton(sensorBuffer);
  if (lastWritePos > 0) {
  
    calcChecksum(&sensorBuffer[1], lastWritePos );
    sendLastBuffer (sensorBuffer, lastWritePos);
    // set timeout, if no shutdown received.
    shutdownTimeout = millis() + 180000;
  }



  //Connectivity feedback via LED
  if (Serial.available())  {
    char incoming = Serial.read();
    if (incoming == 0x6F) {
      digitalWrite(12, HIGH);
    } else if (incoming == 0x70) {
      digitalWrite(12, LOW);
    } else if (incoming == 0x21) {
      shutdownTimeout = millis() + 60000;
    }
  }

  if (loopCounter++ > 10000) {
    loopCounter = 0;
  }
  

  // Shutdown if shutdownTimeout is reached.
  if (shutdownTimeout != 0 && shutdownTimeout < millis()) {
    digitalWrite(2, HIGH);
  }
}


// Here, the last buffer shall be send via ttl
void sendLastBuffer (byte  bufferToSend [], unsigned short lastWritePos) {

  //check if last message of this type has the same chk sum. Discard then:
  unsigned char lastMsgType = bufferToSend [1] - 97;

  if (lastSend[lastMsgType][0] == bufferToSend [lastWritePos + 1] &&
      lastSend[lastMsgType][1] == bufferToSend [lastWritePos + 2]) {
    return;
  }

  // and send:
  for (unsigned short b = 0; b < lastWritePos + 3; b++) {
   Serial.write(bufferToSend[b]);
  }

  lastSend [lastMsgType][0] = bufferToSend [lastWritePos + 1];
  lastSend [lastMsgType][1] = bufferToSend [lastWritePos + 2];
}

/*
* Read digital input pin for shutdown and store to the message buffer.
*/
short checkOffButton (byte sensorBuffer  []) {
  if (digitalRead(3) == HIGH) {
    sensorBuffer[1] = 'z';
    String result = "1";
    result.getBytes(&sensorBuffer[2], 1) ;
    sensorBuffer[3] = '*';
    return 3;
  } else {
    return -1;
  }
}

/*
* Read data from voltage input pin and store to the message buffer.
*/
short collectVoltageData (byte sensorBuffer  []) {
  sensorBuffer[1] = 'g';
  String result = printDouble (calculateVoltage(), 2);
  result.getBytes(&sensorBuffer[2], 5) ;
  sensorBuffer[6] = '*';
  return 6;
}


short collectTemperatureData (byte sensorBuffer  []) {
  sensorBuffer[1] = 'd';
  String result = printDouble (bmp.readTemperature(), 2);
  result.getBytes(&sensorBuffer[2], 5) ;
  sensorBuffer[6] = '*';
  return 6;
}

short collectLeakData (byte sensorBuffer  []) {
  sensorBuffer[1] = 'c';

  int sensorValue = analogRead(A0);  // Bow sensor
  String result = printDouble (sensorValue, 5);
  result.getBytes(&sensorBuffer[2], 5) ;

  sensorBuffer[6] = ',';
  sensorValue = 0; //analogRead(A1); // Stern sensor
  result = printDouble (sensorValue, 5);
  result.getBytes(&sensorBuffer[7], 5) ;

  sensorBuffer[11] = ',';
  //sensorValue = 1013; //analogRead(A2);  //Ambient Pressure
  result = printDouble (bmp.readPressure() / 100, 5);
  result.getBytes(&sensorBuffer[12], 5) ;

  sensorBuffer[16] = '*';
  return 16;
}


short collectDepthData (byte sensorBuffer  []) {

  int value = analogRead(A3);
  float depth = (value - ambientPressureOnStartup) / 70.0f; // 70=calibration value, to be determined...

  sensorBuffer[1] = 'i';
  String result = printDouble (depth, 5);
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
  result.getBytes(&sensorBuffer[6], 5) ;
  sensorBuffer[10] = ',';
  result = printDouble (event.magnetic.y, 6);
  result.getBytes(&sensorBuffer[11], 5) ;
  sensorBuffer[15] = ',';
  result = printDouble (event.magnetic.z, 6);
  result.getBytes(&sensorBuffer[16], 5) ;
  sensorBuffer[20] = '*';
  
  // for (unsigned short b = 0; b < 17 + 3; b++) {
   // Serial.write(sensorBuffer[b]);
  //}
  //Serial.println();
  return 20;
}


short collectGPSData (byte sensorBuffer [], short lastGpsWritePos) {

  while (gpsSerial.available() && !gpsReceivedCompleteMsg) {

    char in = gpsSerial.read();
    if (in == '$') {
      sensorBuffer[lastGpsWritePos++] = 'a';
      gpsStringStarted = true;
    } else if (gpsStringStarted && in > 31 && in < 128) {
      sensorBuffer[lastGpsWritePos++] = in;
    }
    gpsReceivedCompleteMsg = (in == '*' || lastGpsWritePos > (MAX_GPS_MSG_SIZE - 5));
  }

  return lastGpsWritePos;
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


double isInRange (float rangeMin, float rangeMax, short measureMin, short measureMax, short measurement) {
  if (measurement < measureMax && measurement >= measureMin) {
    return rangeMin + ((rangeMax - rangeMin) / (measureMax - measureMin) * (measurement - measureMin));
  }
  return -1;
}

/*
The input is not exactly linear, so we measured the value and estimate as best as possible around 0.2V fixpoints.
*/
float calculateVoltage () {
  short inputVoltage = analogRead(A2);

  // Serial.print (inputVoltage);
  // Serial.print (" -> ");

  float  range [] = {0.0, 5.6, 5.8, 6.0, 6.2, 6.4, 6.6, 6.8, 7.0, 7.2, 7.4, 7.6, 7.8, 8.0, 8.2, 8.4, 8.6, 8.8, 9.0, 20.0};
  static short  measureBoundaries [] = {0, 470, 478, 484, 490, 495, 500, 508, 514, 517, 521, 526, 530, 535, 538, 542, 546, 548, 552, 1024};

  float returnValue = -1.0;
  for (int i = 0; i < sizeof(range) - 2; i++) {
    returnValue = isInRange(range[i], range[i + 1], measureBoundaries[i], measureBoundaries[i + 1], inputVoltage);
    if (returnValue > -1) {
      //     Serial.println (returnValue);
      return returnValue;
    }
  }

  return 20.0;
}




