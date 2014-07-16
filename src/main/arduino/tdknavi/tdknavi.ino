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
#include <LiquidCrystal.h>

LiquidCrystal lcd(2, 3, 7, 6, 5, 4);

Adafruit_HMC5883_Unified mag = Adafruit_HMC5883_Unified(12345);

//GPS Communication Constants
#define RX_PIN_GPS 9
#define TX_PIN_GPS 8
SoftwareSerial gpsSerial = SoftwareSerial(RX_PIN_GPS, TX_PIN_GPS);

// I2C Constants
#define ARDUINO_ADDR 0x04
#define GPS_SERIAL_SPEED 9600

// Serial port
#define SERIAL_SPEED 9600
#define DEBUG false
#define DEBUG_LCD true

//current sensor buffer
const short BUFFER_SIZE = 2;
const short MAX_MSG_SIZE = 80;
short currBufferSize = -1;
short currReadBufferIdx = 0;
short currWriteBufferIdx = -1;
byte sensorBuffer[BUFFER_SIZE][MAX_MSG_SIZE];

/*
 * Main setup routine
 */
void setup() {
  lcd.begin(16, 2);
  lcd.print("Tief Dunkel Kalt");
  lcd.setCursor(0, 1);
  lcd.print("Init Navi...");

  if (DEBUG)
    Serial.begin(9600);

  configureI2C();
  configureGPS();

  pinMode(12, OUTPUT);
  digitalWrite(10, LOW);

  lcd.clear();
  mag.begin();
  if (DEBUG)
    Serial.println("Setup done.");
}


/*
 * Main loop
 */
void loop() {
  delay (30);
   short lastWritePos = 0;
  unsigned long lastGPS = 0;
if (millis() - lastGPS > 1000) {
    lastGPS = millis();
    //once per second
    prepareNextBuffer ();
    lastWritePos = collectGPSData(sensorBuffer[currWriteBufferIdx]);
    calcChecksum(&sensorBuffer[currWriteBufferIdx][3], lastWritePos - 3);
    sendLastBuffer ();
  }

  prepareNextBuffer ();
  lastWritePos = collectCompassData(sensorBuffer[currWriteBufferIdx]);
  calcChecksum(&sensorBuffer[currWriteBufferIdx][3], lastWritePos - 3);
  sendLastBuffer ();

  prepareNextBuffer ();
  lastWritePos = collectLeakData(sensorBuffer[currWriteBufferIdx]);
  calcChecksum(&sensorBuffer[currWriteBufferIdx][3], lastWritePos - 3);
  while (lastWritePos++ < MAX_MSG_SIZE -1) {
          sensorBuffer[currWriteBufferIdx][lastWritePos] = '%';
    }
  sendLastBuffer ();

  //collectCompassData(sensorBuffer[currWriteBufferIdx]);

}

// Here, the last buffer shall be send via ttl
void sendLastBuffer () {
  if (DEBUG) {
    
    Serial.write(sensorBuffer[currWriteBufferIdx], sizeof(sensorBuffer[currWriteBufferIdx])); //MAX_MSG_SIZE);
    Serial.print("=> ");
    Serial.println(currWriteBufferIdx);
  }

  if (DEBUG_LCD) {
    if (sensorBuffer[currWriteBufferIdx][3] == 'a' && sensorBuffer[currWriteBufferIdx][19] == ',') {
      lcd.setCursor (0, 0);
      lcd.print (" ");
      lcd.write (sensorBuffer[currWriteBufferIdx][20]);
      lcd.write (sensorBuffer[currWriteBufferIdx][21]);
      lcd.print ("N");
      lcd.write (sensorBuffer[currWriteBufferIdx][22]);
      lcd.write (sensorBuffer[currWriteBufferIdx][23]);
      lcd.print (",");
      lcd.write (sensorBuffer[currWriteBufferIdx][25]);
      lcd.write (sensorBuffer[currWriteBufferIdx][26]);
      lcd.write (sensorBuffer[currWriteBufferIdx][27]);
      lcd.write (sensorBuffer[currWriteBufferIdx][28]);
      lcd.setCursor (0, 1);
      lcd.write (sensorBuffer[currWriteBufferIdx][33]);
      lcd.write (sensorBuffer[currWriteBufferIdx][34]);
      lcd.write (sensorBuffer[currWriteBufferIdx][35]);
      lcd.print ("E");
      lcd.write (sensorBuffer[currWriteBufferIdx][36]);
      lcd.write (sensorBuffer[currWriteBufferIdx][37]);
      lcd.print (",");
      lcd.write (sensorBuffer[currWriteBufferIdx][39]);
      lcd.write (sensorBuffer[currWriteBufferIdx][40]);
      lcd.write (sensorBuffer[currWriteBufferIdx][41]);
      lcd.write (sensorBuffer[currWriteBufferIdx][42]);

    } else if (sensorBuffer[currWriteBufferIdx][3] == 'b') {
      lcd.setCursor (13, 0);
      lcd.write (sensorBuffer[currWriteBufferIdx][4]);
      if (sensorBuffer[currWriteBufferIdx][5] != '.') {
        lcd.write (sensorBuffer[currWriteBufferIdx][5]);
        if (sensorBuffer[currWriteBufferIdx][6] != '.') {
          lcd.write (sensorBuffer[currWriteBufferIdx][6]);
        } else {
          lcd.write (" ");
        }
      } else {
        lcd.write ("  ");
      }
    } else if (sensorBuffer[currWriteBufferIdx][3] == 'c') {

      lcd.setCursor (12, 2);
      if (sensorBuffer[currWriteBufferIdx][6] > 47 && sensorBuffer[currWriteBufferIdx][6] < 58) {
      
        lcd.print ("Beer");
      } else {
        lcd.print (" Dry");
      }
    }
  }
}

void prepareNextBuffer() {
  currBufferSize++;
  currWriteBufferIdx++;

  if (currWriteBufferIdx >= BUFFER_SIZE) {
    currWriteBufferIdx = 0;
  }
  if (currBufferSize >= BUFFER_SIZE) {
    if (DEBUG)
      Serial.println("WARN: Buffer overflow");
    currBufferSize = 0;
  }

  // write protocol marker
  sensorBuffer[currWriteBufferIdx][0] = 'T';
  sensorBuffer[currWriteBufferIdx][1] = 'D';
  sensorBuffer[currWriteBufferIdx][2] = 'K';
}


short collectLeakData (byte sensorBuffer  []) {
  sensorBuffer[3] = 'c';
  int sensorValue = analogRead(A0);
  String result = printDouble (sensorValue, 0);
  result.getBytes(&sensorBuffer[4], 4) ;
  sensorBuffer[7] = '*';
  return 8;
}

short collectCompassData (byte sensorBuffer  []) {
  
    /* Get a new sensor event */
  sensors_event_t event;
  mag.getEvent(&event);

  /* Display the results (magnetic vector values are in micro-Tesla (uT)) */
  //Serial.print("X: "); Serial.print(event.magnetic.x); Serial.print("  ");
  //Serial.print("Y: "); Serial.print(event.magnetic.y); Serial.print("  ");
  //Serial.print("Z: "); Serial.print(event.magnetic.z); Serial.print("  "); Serial.println("uT");

  // Hold the module so that Z is pointing 'up' and you can measure the heading with x&y
  // Calculate heading when the magnetometer is level, then correct for signs of axis.
  float heading = atan2(event.magnetic.y, event.magnetic.x);

  // Once you have your heading, you must then add your 'Declination Angle', which is the 'Error' of the magnetic field in your location.
  // Find yours here: http://www.magnetic-declination.com/
  // Mine is: -13* 2' W, which is ~13 Degrees, or (which we need) 0.22 radians
  // If you cannot find your Declination, comment out these two lines, your compass will be slightly off.
  float declinationAngle = 0.22;
  heading += declinationAngle;

  // Correct for when signs are reversed.
  if (heading < 0)
    heading += 2 * PI;

  // Check for wrap due to addition of declination.
  if (heading > 2 * PI)
    heading -= 2 * PI;

  // Convert radians to degrees for readability.
  float headingDegrees = heading * 180 / M_PI;

  uint8_t *p = (uint8_t*)&headingDegrees;

  sensorBuffer[3] = 'b';

  //Serial.print("Heading (degrees): ");
  //Serial.println(headingDegrees);

  String result = printDouble (headingDegrees, 3);
  result.getBytes(&sensorBuffer[4], 5) ;
  sensorBuffer[8] = '*';
  return 9;

}


short collectGPSData (byte sensorBuffer []) {

  boolean started = false;
  boolean complete = false;

  short currentByteCount = 3;
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
  if (DEBUG)
    Serial.println("Conf. GPS");

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

  delay(500);

  while (gpsSetSuccess < 5) {

    while (gpsSetSuccess < 3) {
      Serial.print("Set Data Upd. Rate");
      sendUBX(&setDataRate[0], sizeof(setDataRate));  //Send UBX Packet
      gpsSetSuccess += getUBX_ACK(&setDataRate[2]); //Passes Class ID and Message ID to the ACK Receive function
      if (gpsSetSuccess == 5 | gpsSetSuccess == 6)
        gpsSetSuccess -= 4;
    }
    if (gpsSetSuccess == 3 && DEBUG)
      Serial.println("...failed.");
    gpsSetSuccess = 0;

    if (DEBUG)
      Serial.print("Set Sea Mode");
    sendUBX(&setNav[0], sizeof(setNav));  //Send UBX Packet
    gpsSetSuccess += getUBX_ACK(&setNav[2]); //Passes Class ID and Message ID to the ACK Receive function

    if (gpsSetSuccess == 6)
      gpsSetSuccess -= 4;
  }

  if (gpsSetSuccess == 3 && DEBUG)
    Serial.println("...failed.");

  gpsSetSuccess = 0;


  while (gpsSetSuccess < 3) {
    Serial.print("Deact. NMEA GLL");
    sendUBX(setGLL, sizeof(setGLL));
    gpsSetSuccess += getUBX_ACK(&setGLL[2]);
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6)
      gpsSetSuccess -= 4;
  }
  if (gpsSetSuccess == 3 && DEBUG)
    Serial.println("...failed.");
  gpsSetSuccess = 0;

  while (gpsSetSuccess < 3) {
    Serial.print("Deact. NMEA GSA");
    sendUBX(setGSA, sizeof(setGSA));
    gpsSetSuccess += getUBX_ACK(&setGSA[2]);
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6)
      gpsSetSuccess -= 4;
  }
  if (gpsSetSuccess == 3 && DEBUG)
    Serial.println("...failed.");
  gpsSetSuccess = 0;

  while (gpsSetSuccess < 3) {
    Serial.print("Deact. NMEA GSV");
    sendUBX(setGSV, sizeof(setGSV));
    gpsSetSuccess += getUBX_ACK(&setGSV[2]);
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6)
      gpsSetSuccess -= 4;
  }
  if (gpsSetSuccess == 3 && DEBUG)
    Serial.println("...failed.");
  gpsSetSuccess = 0;

  while (gpsSetSuccess < 3) {
    Serial.print("Deact. NMEA RMC");
    sendUBX(setRMC, sizeof(setRMC));
    gpsSetSuccess += getUBX_ACK(&setRMC[2]);
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6)
      gpsSetSuccess -= 4;
  }
  if (gpsSetSuccess == 3 && DEBUG)
    Serial.println("...failed.");
  gpsSetSuccess = 0;

  while (gpsSetSuccess < 3) {
    Serial.print("Deact. NMEA VTG");
    sendUBX(setVTG, sizeof(setVTG));
    gpsSetSuccess += getUBX_ACK(&setVTG[2]);
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6)
      gpsSetSuccess -= 4;
  }
  if (gpsSetSuccess == 3 && DEBUG)
    Serial.println("...failed.");

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
      Serial.println(" - ACK Timeout ");
      return 5;
    }
    if (i == 4 && ackPacket[3] == 0x00) {
      Serial.println(" - NAK ");
      return 1;
    }
  }

  for (i = 2; i < 8; i++) {
    CK_A = CK_A + ackPacket[i];
    CK_B = CK_B + CK_A;
  }
  if (msgID[0] == ackPacket[6] && msgID[1] == ackPacket[7]
      && CK_A == ackPacket[8] && CK_B == ackPacket[9]) {
    Serial.print(" - ACK ");
    printHex(ackPacket, sizeof(ackPacket));
    Serial.println();
    return 10;
  } else {
    Serial.print(" - ACK Checksum Fail: ");
    printHex(ackPacket, sizeof(ackPacket));
    Serial.println();
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
    Serial.print(tmp[i]);
    if (j == 1) {
      Serial.print(" ");
      j = 0;
    } else
      j++;
  }
  //Serial.println();
}

//
//  I2C
//

/*
 * Configure all settings required to establish I2C connectivity to all clients.
 */
void configureI2C() {
  if (DEBUG)
    Serial.println("Conf. I2C");
  Wire.begin(ARDUINO_ADDR);
  Wire.onReceive(receiveData);
  Wire.onRequest(sendData);
}

/*
 * Callback for received data.
 */
void receiveData(int byteCount) {
  //  return;
  while (Wire.available()) {
    int number = Wire.read();
    if (number == 00) {
      // if (DEBUG)
      //   Serial.println("Received HIGH signal on i2c");
      digitalWrite(12, HIGH); // set the LED on
    } else {
      // if (DEBUG)
      //   Serial.println("Received LOW signal on i2c");
      digitalWrite(12, LOW); // set the LED off
    }
  }
}

// callback for sending data
void sendData() {

  Wire.write("TDKb12345612345*4");
  return;
  if ( currBufferSize > 0 && currReadBufferIdx != currWriteBufferIdx) {
    Wire.write(sensorBuffer[currReadBufferIdx], sizeof(sensorBuffer[currReadBufferIdx]));
    //Wire.write("TDKb12345612345*4");
    //  Wire.write("TDKa");
    if (DEBUG) {
      Serial.write(sensorBuffer[currReadBufferIdx],
                   sizeof(sensorBuffer[currReadBufferIdx]));
      Serial.println("=> SENT");
    }

    currReadBufferIdx++;

    if (currReadBufferIdx >= BUFFER_SIZE) {
      currReadBufferIdx = 0;
    }

    currBufferSize--;
  } else {
    //    Wire.write (0xFF);
    //Wire.write("TDKb12345612345*4");
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

