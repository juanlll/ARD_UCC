#include <SD.h> //Load SD library

//VARIABLES
int chipSelect = 4; //chip select pin for the MicroSD Card Adapter
File file; // file object that is used to read and write data
int i = 0;
String date;
int pulsadorPin = 2;
int temp,humedad,co2,humidity_ground,lecturaHumedad;
int lectura; //lectura analógica (0 - 1023)
int lecturaEnPPM; //lectura en ppm (20 ppm - 2000 ppm


void setup() {
  // put your setup code here, to run once:
   pinMode(chipSelect, OUTPUT); // chip select pin must be set to OUTPUT mode
  if (!SD.begin(chipSelect)) { Serial.println("Could not initialize SD card."); }
  if (SD.exists("datos.txt")) { Serial.println("File exists.");  }

}

void loop() {
  // put your main code here, to run repeatedly:
 file = SD.open("datos.txt", FILE_WRITE); // open "file.txt" to write data
  if (file) {
    
    i +=1; 

    lectura = analogRead(sensor); //leemos del sensor MQ7S valores entre 0 1023
    lecturaHumedad = map(analogRead(sensorHumedad), 1023, 0, 0, 100);
    lecturaEnPPM = map(lectura, 0, 1023, 20, 1023);//Convertimos el rango de lectura analógica (0-1023) al rango de lectura en ppm (20 ppm - 2000 pmm) que soporta MQ7 
    humedad = dht.readHumidity();
    temp =dht.readTemperature();
    date = "16/04/2019";
   
    String objeto = "{";
           objeto += "id:"+String(i);
           objeto +=",";
           objeto += "temp:"+String(temp);
           objeto +=",";
           objeto += "humidity:"+String(humedad);
           objeto +=",";
           objeto += "co2:"+String(lecturaEnPPM);
           objeto +=",";
           objeto += "humidity_ground:"+String(lecturaHumedad);
           objeto +=",";
           objeto += "date:"+String(date);
           objeto += "},";

    file.println(objeto); // write number to file
    file.close(); // close file
    Serial.print("Wrote number: "); // debug output: show written number in serial monitor
    Serial.println(objeto);
    delay(5000);
    
  } else {
    Serial.println("Could not open file (writing).");
  }
}