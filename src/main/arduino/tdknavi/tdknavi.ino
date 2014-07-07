/*
 * www.Tief-Dunkel-Kalt.org
 *
 * Submarine Navigation Software
 *
 * Support for all sensors to collect and aggregate data and send to processing unit.
 */
#include <SoftwareSerial.h>
#include <Wire.h>

//GPS Communication Constants
#define RX_PIN_GPS 9
#define TX_PIN_GPS 8
SoftwareSerial gpsSerial = SoftwareSerial(RX_PIN_GPS, TX_PIN_GPS);
#define GPS_SERIAL_SPEED 9600

// I2C Constants
#define ARDUINO_ADDR 0x04
#define GPS_SERIAL_SPEED 9600

// Serial port
#define SERIAL_SPEED 9600
#define DEBUG true

//current sensor buffer
const int BUFFER_SIZE = 6;
const int MAX_MSG_SIZE = 78;
byte buffer[BUFFER_SIZE][MAX_MSG_SIZE];
short currBufferSize = 0;
short currReadBufferIdx = 0;
short currWriteBufferIdx = -1;

//to be deleted...
int number = 0;
int state = 0;
boolean gpsStatus[] = { false, false, false, false, false, false, false };
unsigned long start;

/*
 * Main setup routine
 */
void setup() {

  if (DEBUG)
    Serial.begin(9600);

  configureI2C();
  configureGPS();

  pinMode(12, OUTPUT);
  pinMode(6, OUTPUT);
  pinMode(6, OUTPUT);
  digitalWrite(10, LOW);
  digitalWrite(6, HIGH);



  if (DEBUG)
    Serial.println("Setup done.");

}

/*
 * Main loop
 */
void loop() {

  Serial.println("Starting main loop");

  delay(500);
  int currentByteCount = 0;
  boolean complete = false;
  while (!complete) {

    if (gpsSerial.available()) {
      char in = gpsSerial.read();

   

      //Detected a new GPS String:
      if (in == '$') {
        if (currWriteBufferIdx >= BUFFER_SIZE) {
          currWriteBufferIdx = -1;
        }
        if (currBufferSize >= BUFFER_SIZE) {
          if (DEBUG)
            Serial.println("WARNING: Buffer overflow!");
          currBufferSize = 0;
        }

        // increase buffer size and update to next index.
        currBufferSize++;
        currWriteBufferIdx++;
        currentByteCount = 0;
        // write protocol marker
        buffer[currWriteBufferIdx][currentByteCount++] = 'b';

        //Got a new character of the gps string
      } else if (currentByteCount > 0 && currentByteCount < (MAX_MSG_SIZE - 4)  && in > 31 && in < 128) {
           
        buffer[currWriteBufferIdx][currentByteCount++] = in;
      }
     
          
      // last character war a termination char
      if (in == '*' || currentByteCount > (MAX_MSG_SIZE - 4)) {

        byte chkS1 = gpsSerial.read();
        byte chkS2 = gpsSerial.read();

        buffer[currWriteBufferIdx][currentByteCount++] = chkS1;
        buffer[currWriteBufferIdx][currentByteCount++] = chkS2;
        buffer[currWriteBufferIdx][currentByteCount++] = '\0';
        while (currentByteCount++ < MAX_MSG_SIZE) {
          buffer[currWriteBufferIdx][currentByteCount] = '%';
        }
        Serial.write (buffer[currWriteBufferIdx], MAX_MSG_SIZE);
       
        Serial.print ("  => ");
        Serial.println ( currWriteBufferIdx);
        complete = true;
      }
    }
  }
}


void configureGPS() {
  Serial.println("Configuring GPS receiver");
  gpsSerial.begin(GPS_SERIAL_SPEED);
  byte settingsArray[] = {
    0x05, 0xF4, 0x03, 0x80, 0x25, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
  }; //

  configureUblox(settingsArray);

}


void configureUblox(byte *settingsArrayPointer) {
  byte gpsSetSuccess = 0;
  Serial.println("Configuring u-Blox GPS initial state...");

  //Generate the configuration string for Navigation Mode
  byte setNav[] = {
    0xB5, 0x62, 0x06, 0x24, 0x00, 0x00, 0xFF, 0xFF, *settingsArrayPointer, 0x03, 0x00, 0x00, 0x00, 0x00, 0x10, 0x27, 0x00, 0x00, 0x05, 0x00, 0xFA, 0x00, 0xFA, 0x00, 0x64, 0x00, 0x2C, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
  };
  //  byte setNav[] = {0xB5, 0x62, 0x06, 0x24, 0x24, 0x00, 0xFF, 0xFF, *settingsArrayPointer, 0x03, 0x00, 0x00, 0x00, 0x00, 0x10, 0x27, 0x00, 0x00, 0x05, 0x00, 0xFA, 0x00, 0xFA, 0x00, 0x64, 0x00, 0x2C, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
  calcChecksum(&setNav[2], sizeof(setNav) - 4);

  //Generate the configuration string for Data Rate
  byte setDataRate[] = {
    0xB5, 0x62, 0x06, 0x08, 0x06, 0x00, settingsArrayPointer[1], settingsArrayPointer[2], 0x01, 0x00, 0x01, 0x00, 0x00, 0x00
  };
  calcChecksum(&setDataRate[2], sizeof(setDataRate) - 4);

  //Generate the configuration string for Baud Rate
  byte setPortRate[] = {
    0xB5, 0x62, 0x06, 0x00, 0x14, 0x00, 0x01, 0x00, 0x00, 0x00, 0xD0, 0x08, 0x00, 0x00, settingsArrayPointer[3], settingsArrayPointer[4], settingsArrayPointer[5], 0x00, 0x07, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
  };
  calcChecksum(&setPortRate[2], sizeof(setPortRate) - 4);

  byte setGLL[] = {
    0xB5, 0x62, 0x06, 0x01, 0x08, 0x00, 0xF0, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x01, 0x2B
  };
  byte setGSA[] = {
    0xB5, 0x62, 0x06, 0x01, 0x08, 0x00, 0xF0, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x32
  };
  byte setGSV[] = {
    0xB5, 0x62, 0x06, 0x01, 0x08, 0x00, 0xF0, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x03, 0x39
  };
  byte setRMC[] = {
    0xB5, 0x62, 0x06, 0x01, 0x08, 0x00, 0xF0, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x04, 0x40
  };
  byte setVTG[] = {
    0xB5, 0x62, 0x06, 0x01, 0x08, 0x00, 0xF0, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04, 0x46
  };

  delay(2500);

  while (gpsSetSuccess < 3)
  {
    Serial.print("Setting Navigation Mode... ");
    sendUBX(&setNav[0], sizeof(setNav));  //Send UBX Packet
    gpsSetSuccess += getUBX_ACK(&setNav[2]); //Passes Class ID and Message ID to the ACK Receive function
    if (gpsSetSuccess == 5) {
      gpsSetSuccess -= 4;
      setBaud(settingsArrayPointer[4]);
      delay(1500);
      byte lowerPortRate[] = {
        0xB5, 0x62, 0x06, 0x00, 0x14, 0x00, 0x01, 0x00, 0x00, 0x00, 0xD0, 0x08, 0x00, 0x00, 0x80, 0x25, 0x00, 0x00, 0x07, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0xA2, 0xB5
      };
      sendUBX(lowerPortRate, sizeof(lowerPortRate));
      gpsSerial.begin(9600);
      delay(2000);
    }
    if (gpsSetSuccess == 6) gpsSetSuccess -= 4;
    if (gpsSetSuccess == 10) gpsStatus[0] = true;
  }
  if (gpsSetSuccess == 3) Serial.println("Navigation mode configuration failed.");
  gpsSetSuccess = 0;
  while (gpsSetSuccess < 3) {
    Serial.print("Setting Data Update Rate... ");
    sendUBX(&setDataRate[0], sizeof(setDataRate));  //Send UBX Packet
    gpsSetSuccess += getUBX_ACK(&setDataRate[2]); //Passes Class ID and Message ID to the ACK Receive function
    if (gpsSetSuccess == 10) gpsStatus[1] = true;
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6) gpsSetSuccess -= 4;
  }
  if (gpsSetSuccess == 3) Serial.println("Data update mode configuration failed.");
  gpsSetSuccess = 0;

  return;
  while (gpsSetSuccess < 3 && settingsArrayPointer[6] == 0x00) {
    Serial.print("Deactivating NMEA GLL Messages ");
    sendUBX(setGLL, sizeof(setGLL));
    gpsSetSuccess += getUBX_ACK(&setGLL[2]);
    if (gpsSetSuccess == 10) gpsStatus[2] = true;
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6) gpsSetSuccess -= 4;
  }
  if (gpsSetSuccess == 3) Serial.println("NMEA GLL Message Deactivation Failed!");
  gpsSetSuccess = 0;

  while (gpsSetSuccess < 3 && settingsArrayPointer[7] == 0x00) {
    Serial.print("Deactivating NMEA GSA Messages ");
    sendUBX(setGSA, sizeof(setGSA));
    gpsSetSuccess += getUBX_ACK(&setGSA[2]);
    if (gpsSetSuccess == 10) gpsStatus[3] = true;
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6) gpsSetSuccess -= 4;
  }
  if (gpsSetSuccess == 3) Serial.println("NMEA GSA Message Deactivation Failed!");
  gpsSetSuccess = 0;

  while (gpsSetSuccess < 3 && settingsArrayPointer[8] == 0x00) {
    Serial.print("Deactivating NMEA GSV Messages ");
    sendUBX(setGSV, sizeof(setGSV));
    gpsSetSuccess += getUBX_ACK(&setGSV[2]);
    if (gpsSetSuccess == 10) gpsStatus[4] = true;
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6) gpsSetSuccess -= 4;
  }
  if (gpsSetSuccess == 3) Serial.println("NMEA GSV Message Deactivation Failed!");
  gpsSetSuccess = 0;

  while (gpsSetSuccess < 3 && settingsArrayPointer[9] == 0x00) {
    Serial.print("Deactivating NMEA RMC Messages ");
    sendUBX(setRMC, sizeof(setRMC));
    gpsSetSuccess += getUBX_ACK(&setRMC[2]);
    if (gpsSetSuccess == 10) gpsStatus[5] = true;
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6) gpsSetSuccess -= 4;
  }
  if (gpsSetSuccess == 3) Serial.println("NMEA RMC Message Deactivation Failed!");
  gpsSetSuccess = 0;

  while (gpsSetSuccess < 3 && settingsArrayPointer[10] == 0x00) {
    Serial.print("Deactivating NMEA VTG Messages ");
    sendUBX(setVTG, sizeof(setVTG));
    gpsSetSuccess += getUBX_ACK(&setVTG[2]);
    if (gpsSetSuccess == 10) gpsStatus[6] = true;
    if (gpsSetSuccess == 5 | gpsSetSuccess == 6) gpsSetSuccess -= 4;
  }
  if (gpsSetSuccess == 3) Serial.println("NMEA VTG Message Deactivation Failed!");

  gpsSetSuccess = 0;
  if (settingsArrayPointer[4] != 0x25) {
    Serial.print("Setting Port Baud Rate... ");
    sendUBX(&setPortRate[0], sizeof(setPortRate));
    setBaud(settingsArrayPointer[4]);
    Serial.println("Success!");
    delay(500);
  }
}


void calcChecksum(byte *checksumPayload, byte payloadSize) {
  byte CK_A = 0, CK_B = 0;
  for (int i = 0; i < payloadSize ; i++) {
    CK_A = CK_A + *checksumPayload;
    CK_B = CK_B + CK_A;
    checksumPayload++;
  }
  *checksumPayload = CK_A;
  checksumPayload++;
  *checksumPayload = CK_B;
}

void sendUBX(byte *UBXmsg, byte msgLength) {
  for (int i = 0; i < msgLength; i++) {
    gpsSerial.write(UBXmsg[i]);
    gpsSerial.flush();
  }

  gpsSerial.println();
  gpsSerial.flush();
}


byte getUBX_ACK(byte *msgID) {
  byte CK_A = 0, CK_B = 0;
  byte incoming_char;
  boolean headerReceived = false;
  unsigned long ackWait = millis();
  byte ackPacket[10] = {
    0xB5, 0x62, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
  };
  int i = 0;
  while (1) {
    if (gpsSerial.available()) {

      incoming_char = gpsSerial.read();

      if (incoming_char == ackPacket[i]) {
        i++;
      }
      else if (i > 2) {
        ackPacket[i] = incoming_char;
        i++;
      }
    }
    if (i > 9) break;
    if ((millis() - ackWait) > 2500) {
      Serial.println("ACK Timeout");
      return 5;
    }
    if (i == 4 && ackPacket[3] == 0x00) {
      Serial.println("NAK Received");
      return 1;
    }
  }

  for (i = 2; i < 8 ; i++) {
    CK_A = CK_A + ackPacket[i];
    CK_B = CK_B + CK_A;
  }
  if (msgID[0] == ackPacket[6] && msgID[1] == ackPacket[7] && CK_A == ackPacket[8] && CK_B == ackPacket[9]) {
    Serial.println("Success!");
    Serial.print("ACK Received! ");
    printHex(ackPacket, sizeof(ackPacket));
    return 10;
  }
  else {
    Serial.print("ACK Checksum Failure: ");
    printHex(ackPacket, sizeof(ackPacket));
    delay(1000);
    return 1;
  }
}


void printHex(uint8_t *data, uint8_t length) // prints 8-bit data in hex
{
  char tmp[length * 2 + 1];
  byte first ;
  int j = 0;
  for (byte i = 0; i < length; i++)
  {
    first = (data[i] >> 4) | 48;
    if (first > 57) tmp[j] = first + (byte)7;
    else tmp[j] = first ;
    j++;

    first = (data[i] & 0x0F) | 48;
    if (first > 57) tmp[j] = first + (byte)7;
    else tmp[j] = first;
    j++;
  }
  tmp[length * 2] = 0;
  for (byte i = 0, j = 0; i < sizeof(tmp); i++) {
    Serial.print(tmp[i]);
    if (j == 1) {
      Serial.print(" ");
      j = 0;
    }
    else j++;
  }
  Serial.println();
}

void setBaud(byte baudSetting) {
  if (baudSetting == 0x12) gpsSerial.begin(4800);
  if (baudSetting == 0x25) gpsSerial.begin(9600);
  if (baudSetting == 0x4B) gpsSerial.begin(19200);
  if (baudSetting == 0x96) gpsSerial.begin(38400);
  if (baudSetting == 0xE1) gpsSerial.begin(57600);
  if (baudSetting == 0xC2) gpsSerial.begin(115200);
  if (baudSetting == 0x84) gpsSerial.begin(230400);
}






//
//  I2C
//

/*
 * Configure all settings required to establish I2C connectivity to all clients.
 */
void configureI2C() {
  if (DEBUG) Serial.println("Configuring I2C connectivity");
  Wire.begin(ARDUINO_ADDR);
  Wire.onReceive(receiveData);
  Wire.onRequest(sendData);
}

/*
 * Callback for received data.
 */
void receiveData(int byteCount) {
  while (Wire.available()) {
    int number = Wire.read();
    if (number == 00) {
      if (DEBUG)
        Serial.println("Received HIGH signal on i2c");
      digitalWrite(12, HIGH); // set the LED on
    } else {
      if (DEBUG)
        Serial.println("Received LOW signal on i2c");
      digitalWrite(12, LOW); // set the LED off
    }
  }
}

// callback for sending data
void sendData() {
  if (currBufferSize > 0) {
    if (currWriteBufferIdx > BUFFER_SIZE) {
      currWriteBufferIdx = 0;
    }

   // byte [] hdr  = {'T','D','K'};
    //Wire.write (hdr, 3);
    Wire.write ("TDK");
    Wire.write ( buffer [currWriteBufferIdx],MAX_MSG_SIZE );
    currWriteBufferIdx++;
    currBufferSize--;
  }
  //Wire.write("TDKb12345612345*4");

}
