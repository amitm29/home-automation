#include <SoftwareSerial.h>
#include <Wire.h>
#include <LCD.h>
#include <LiquidCrystal_I2C.h>
#include <Keypad.h>

LiquidCrystal_I2C  lcd(0x27,2,1,0,4,5,6,7); // 0x27 is the I2C bus address for an unmodified backpack
SoftwareSerial BT(2, 3); // RX | TX

const int aNUM = 100;
const int rNUM = 100;

byte i=0;
int ldrVal, LEDbound=40, cState[9], maxBound = 80, tempBound, pirState=0;
int relay1=10, relay2=11, relay3=12, relay4=13, relay5 = A2, ldrPin = A0, pirPin = A1, tempPin = A3, tState[9];
int count1 = 0, count2 = 0;
float temp;
String tem = "Temp. : ", timeString = "";
char key;
float temps[aNUM];
byte position = 0;
float averageTemp;
bool readingTime = false;


bool tBound[9];
long timeInSecs[9];
long targetTime[9];
long timeOn[9];
long timeRunning[9];
long totalTime[9];

const byte ROWS = 3; //three rows
const byte COLS = 3; //three columns
char keys[ROWS][COLS] = {
  {'2','3','A'},
  {'5','6','B'},
  {'8','9','C'}
};

byte rowPins[ROWS] = {9, 8, 7}; //connect to the row pinouts of the keypad
byte colPins[COLS] = {6, 5, 4}; //connect to the column pinouts of the keypad

Keypad keypad = Keypad( makeKeymap(keys), rowPins, colPins, ROWS, COLS );

void setup()
{
  // activate LCD module
  lcd.begin (16,2); // for 16 x 2 LCD module
  lcd.setBacklightPin(3,POSITIVE);
  tempBound=LEDbound;
  lcd.home (); // set cursor to 0,0
  digitalWrite(relay1, HIGH);
  digitalWrite(relay2, HIGH);
  digitalWrite(relay3, HIGH);
  digitalWrite(relay4, HIGH);
  digitalWrite(relay5, HIGH);
  pinMode(relay1, OUTPUT);
  pinMode(relay2, OUTPUT);
  pinMode(relay3, OUTPUT);
  pinMode(relay4, OUTPUT);
  pinMode(relay5, OUTPUT);

  pinMode(pirPin, INPUT);

  for(int i = 0; i < 9; i++)
  {
  tBound[i] = false;
  timeOn[i] = 0;
  timeRunning[i] = 0;
  cState[i] = 0;
  totalTime[i] = 0;
  }
  switchOn(5);
  BT.begin(9600);

  Serial.begin(9600);

}

void loop()
{
  
  for(int j=0; j<9; j++){
    if(cState[j] == 1)
      timeRunning[j] = millis() - timeOn[j];
  }

  
  
  int tMin=100, tMax=0, tTem;
  for(i=0; i<rNUM; i++)
  {
      tTem = analogRead(tempPin);
      temp += tTem;
      if(tTem < tMin)
        tMin = tTem;
      if(tTem>tMax)
        tMax=tTem;
  }

  temp = temp - tMin - tMax;
  temp = temp / (rNUM-2) * 0.48828125;

  temps[position] = temp;
    if (position == aNUM-1) {
        position = 0;
    } else {
        position++;
    }

    //Get average
    averageTemp = 0;
    for (i = 0; i < aNUM; i++) {
        averageTemp += temps[i] / aNUM;
    }

  lcd.home (); // set cursor to 0,0
  lcd.print(tem + (averageTemp-1));

  if(count2 > 348)
  {
    for(i=0; i<9; i++){
    String k = String(i);
  if(cState[i]==0)
  {
    String t = String(totalTime[i]);
    BT.print("S" + k + "/" + t + "#");
    delay(50);
  }
  else{
    String t = String(totalTime[i]+timeRunning[i]);
    BT.print("S" + k + "/" + t + "#");
    delay(50);
  }
  }
  String k = String(9);
  String t = String(millis());
  BT.print("S" + k + "/" + t + "#");
  delay(50);
  
    count2=0;
  }
  else{count2 += 1;}


  if(count1 > 100)         //write temperature and status after every 100 repeatitions
  {
    String s = String(averageTemp-1);
    BT.print("Z" + s + "#");
    
    count1 = 0;
  }
  else{
    count1 += 1;
  }

  lcd.write(0xdf);
  lcd.print("C");

  if(cState[8]==1)
  {
    lcd.setCursor (7,1);
    lcd.print("LDR");
  }
  else if(cState[7]==1)
  {
    lcd.setCursor (7,1);
    lcd.print("PIR");
  }
  else if(cState[7]==0)
  {
    lcd.setCursor (7,1);
    lcd.print("   ");
  }
  lcd.setCursor (0,1);        // go to start of 2nd line
  lcd.print(millis()/1000);

  for(int i = 0; i<9; i++)
  {
    if(tBound[i])
    {
      if(millis() >= targetTime[i])
      {
        if(tState[i] == 0)
          switchOff(i);
        else if(tState[i] ==1)
          switchOn(i);
      tBound[i] = false;
      }
    }
  }

  pirState = digitalRead(pirPin);
  ldrVal = analogRead(ldrPin);

  if(cState[8]==1){
//    Serial.println(ldrVal);

    if ((ldrVal < tempBound) && (tempBound-ldrVal>2) && (cState[4]==0)) {
      digitalWrite(relay5, LOW);
      BT.print("81#");
      cState[4] = 1;

      if(tempBound<=maxBound)
         tempBound = tempBound + 2;
      else
         tempBound=LEDbound;

    }

    else if(ldrVal>tempBound + 50 && cState[4]==1){
      digitalWrite(relay5, HIGH);
      BT.print("80#");
      cState[4] = 0;
    }

  }
  else if(cState[7] == 1)
  {
      if(pirState==1 && cState[4]==0)
      {
        digitalWrite(relay5, LOW);
        BT.print("81#");
        cState[4] = 1;
        cState[5] = 1;
        lcd.setBacklight(HIGH);
      }
      else if(pirState==0 && cState[4]==1)
      {
       digitalWrite(relay5, HIGH);
       BT.print("80#");
       cState[4] = 0;
       cState[5] = 0;
       lcd.setBacklight(LOW);
      }
  }


  key = keypad.getKey();

  if (BT.available()>0 || key!=NO_KEY)
  {

      if(BT.available()>0)
        key = BT.read();

  //        delay(3);
      Serial.println(key);

      if(key=='T')
      {
        Serial.println("T detected");
        readingTime = true;
        long tempTime = 0;
        while(true)
        {
          key = BT.read();
          if(key == 'R')
            break;

          else
          {
            int tmp;
            if(key=='0')
              tmp=0;
            else if(key=='1')
              tmp=1;
            else if(key=='2')
              tmp=2;
            else if(key=='3')
              tmp=3;
            else if(key=='4')
              tmp=4;
            else if(key=='5')
              tmp=5;
            else if(key=='6')
              tmp=6;
            else if(key=='7')
              tmp=7;
            else if(key=='8')
              tmp=8;
            else if(key=='9')
              tmp=9;
            tempTime = tempTime*10 + tmp;
          }
        }
      char k = BT.read();
      int i;
      if(k=='2')
        i=0;
      else if(k=='3')
        i=1;
      else if(k=='5')
        i=2;
      else if(k=='6')
        i=3;
      else if(k=='8')
        i=4;
      else if(k=='9')
        i=5;
      else if(k=='A')
        i=6;
      else if(k=='B')
        i=7;
      else if(k=='C')
        i=8;
      else
        Serial.println("else");
      if(i==4)
      {
        cState[8] = 0;
        cState[7] = 0;
      }


      if(tempTime!=0){
        timeInSecs[i] = tempTime;
        targetTime[i] = millis()+ tempTime*1000;
        Serial.println(targetTime[i]);

        tBound[i] = true;
        Serial.println(i);
        Serial.println(tBound[i]);

        while(true)
        {
          key = BT.read();
          if(key == '#')
          {
          readingTime = false;
          break;
          }

          else if(key == '/')
          continue;

          else{
          if(key=='1')
            tState[i] = 1;
          else
            tState[i] = 0;
          Serial.println(tState[i]);
          }
        }
      }
      else{
        tBound[i] = false;
        readingTime = false;
        Serial.print("Timer deleted on : ");
        Serial.println(i);
      }

      }





      if(!readingTime)
      {
          if(key=='9')
          {
      if(cState[5]==0)
          switchOn(5);
      
      else
        switchOff(5);         
          }


        else if(key=='0')
        {
          if(cState[0]==0)
          BT.print("20#");
          else
          BT.print("21#");

          if(cState[1]==0)
          BT.print("30#");
          else
          BT.print("31#");

          if(cState[2]==0)
          BT.print("50#");
          else
          BT.print("51#");

          if(cState[3]==0)
          BT.print("60#");
          else
          BT.print("61#");

          if(cState[4]==0)
          BT.print("80#");
          else
          BT.print("81#");

          if(cState[5]==0)
          BT.print("90#");
          else
          BT.print("91#");

          if(cState[6]==0)
          BT.print("A0#");
          else
          BT.print("A1#");

          if(cState[8]==0)
          BT.print("C0#");
          else
          BT.print("C1#");

          if(cState[7]==0)
          BT.print("B0#");
          else
          BT.print("B1#");
        }



        else if(key=='2')
        {
        if(cState[0]==0)
        switchOn(0);
      
        else
        switchOff(0);  
        }

        else if(key=='3')
        {
      if(cState[1]==0)
        switchOn(1);
      
        else
        switchOff(1);
        }

        else if(key=='5')
        {
     if(cState[2]==0)
        switchOn(2);
      
        else
        switchOff(2);
        }

        else if(key=='6')
        {
      if(cState[3]==0)
        switchOn(3);
      
        else
        switchOff(3);
        }

         else if(key=='8')
        {
      if(cState[4]==0)
        switchOn(4);
      
        else
        switchOff(4);
        }


        else if(key=='A')
        {
            if(cState[0]==1 || cState[1]==1 || cState[2]==1 || cState[3]==1 || cState[4]==1)
        switchOff(6);

            else if (cState[6]==0)
                switchOn(6);
        }

        else if(key=='B')
        {
      if(cState[7]==0)
        switchOn(7);
      
        else
        switchOff(7);
        }

        else if(key=='C')
        {
      if(cState[8]==0)
        switchOn(8);
      
        else
        switchOff(8);
      }





  }

}
}

void switchOff(int i)
{
  totalTime[i] += timeRunning[i];
  timeRunning[i] = 0;
  cState[i]=0;
  tBound[i] = false;
  
  if(i==0)
  {
    digitalWrite(relay1, HIGH);
    BT.print("20#");
  }
  else if(i==1)
  {
    digitalWrite(relay2, HIGH);
    BT.print("30#");
  }
  else if(i==2)
  {
    digitalWrite(relay3, HIGH);
    BT.print("50#");
  }
  else if(i==3)
  {
    digitalWrite(relay4, HIGH);
    BT.print("60#");
  }
  else if(i==4)
  {
    if(cState[7]!=0)  
    switchOff(7);
  if(cState[8]!=0) 
    switchOff(8);
    digitalWrite(relay5, HIGH);
    BT.print("80#");
  }
  else if(i==5)
  {
  if(cState[7]!=0)  
    switchOff(7);
  if(cState[8]!=0) 
    switchOff(8);
    lcd.setBacklight(LOW);
    BT.print("90#");
  }
  else if(i==6)
  {
  if(cState[7]!=0)  
    switchOff(7);
  if(cState[8]!=0) 
    switchOff(8);
  BT.print("A0#");
    for(int k=0; k<5; k++)
        switchOff(k);
  }
  else if(i==7)
  {
    BT.print("B0#");
  }
  else if(i==8)
  {
    BT.print("C0#");
  }

}



void switchOn(int i)
{
  timeOn[i] = millis();
  cState[i] = 1;
  tBound[i] = false;
  if(i==0)
  {
    digitalWrite(relay1, LOW);
    BT.print("21#");
  }
  else if(i==1)
  {
    digitalWrite(relay2, LOW);
    BT.print("31#");
  }
  else if(i==2)
  {
    digitalWrite(relay3, LOW);
    BT.print("51#");
  }
  else if(i==3)
  {
    digitalWrite(relay4, LOW);
    BT.print("61#");
  }
  else if(i==4)
  {
  if(cState[7]!=0)  
    switchOff(7);
  if(cState[8]!=0) 
    switchOff(8);
    digitalWrite(relay5, LOW);
    BT.print("81#");
  }
  else if(i==5)
  {
  if(cState[7]!=0)  
    switchOff(7);
  if(cState[8]!=0) 
    switchOff(8);
    lcd.setBacklight(HIGH);
  BT.print("91#");
  }
  else if(i==6)
  {
  if(cState[7]!=0)  
    switchOff(7);
  if(cState[8]!=0) 
    switchOff(8);
  BT.print("A1#");
    for(int k =0; k<5; k++)
        switchOn(k);
  }
  else if(i==7)
  {
    switchOff(8);
    BT.print("B1#");
  }
  else if(i==8)
  {
  switchOff(7);
    BT.print("C1#");
  }

}

