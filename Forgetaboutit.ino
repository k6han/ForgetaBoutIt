
#include "rfid1.h"

int state = 0;
void checkRFID(int i){ //i is the sensor that wants to be read
  RFID1 rfid;
  uchar serNum[5];
  //THIS IS THE STANDARD LAYOUT FOR THE FLAGSHIP
  if(i==0){
    rfid.begin(43,35,39,51,31,47);
  }
  //rfid.begin( IRQ_PIN, SCK_PIN,MOSI_PIN, (this is the one that changes)-> MISO_PIN, SDA_PIN ,RST_PIN)
  if(i==1){
     rfid.begin(43,35,39,8,31,47);
  }
  
  if(i==2){
   rfid.begin(43,35,39,9,31,47);
  }
  if(i==3){
    rfid.begin(43,35,39,10,31,47);
  }
  
  if(i==4){
   rfid.begin(43,35,39,11,31,47);
  }
  
  delay(100);

  rfid.init();
  uchar status;
  uchar str[MAX_LEN];
  // Search card, return card types
  status = rfid.request(PICC_REQIDL, str);
  if (status != MI_OK)
  {
    return;
  }
  // Show card type
 // rfid.showCardType(str);
  //Prevent conflict, return the 4 bytes Serial number of the card
  status = rfid.anticoll(str);
  if (status == MI_OK)
  {
    memcpy(serNum, str, 5);
    String endstring;
    for(int k = 0; k < 5; k++){
      endstring += (serNum[k]);
    }
    digitalWrite(13, LOW);
    Serial.print(endstring);
   Serial.print("\n");
  //  rfid.showCardID(serNum);//show the card ID
  //Serial.println();
  }
  delay(250);
  digitalWrite(13, HIGH);
  rfid.halt(); //command the card into sleep mode 
}



void setup() {
  Serial.begin(9600); 
  pinMode(13, OUTPUT);
  digitalWrite(13, LOW);//this begins the Serial monitor which actually leads the BT
}

//Global variables


void loop() {
 if(Serial.available() > 0){ // Checks whether data is comming from the serial port
    state = Serial.read(); // Reads the data from the serial port
 }
 
 if (state == 1) {
  digitalWrite(13, HIGH);
  for ( int i = 0; i < 4 ; i++ ){
  checkRFID(0);
  checkRFID(1);
  checkRFID(2);
  checkRFID(3);
  checkRFID(4);
  delay(50);
  
 }
 digitalWrite(13, LOW);
  state = 0;
 } 
}
