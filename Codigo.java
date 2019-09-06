#include <DHT.h>
// Definimos el pin digital donde se conecta el sensor
#define DHTPIN 2
// Dependiendo del tipo de sensor
#define DHTTYPE DHT11
// Inicializamos el sensor DHT11
DHT dht(DHTPIN, DHTTYPE);

const int LED = 8;


void setup() {
 // Inicializamos comunicación serie
  Serial.begin(9600);
 
  // Comenzamos el sensor DHT
  dht.begin();

  // Configuro el pin de salida
  pinMode(LED,OUTPUT);
  
}

void loop() {
  // put your main code here, to run repeatedly:
  //CorrectoLed();
  //delay(1000);
  AlertarTemperatura();
  //CorrectoLed();
}

// metodo encargado de iterar enendido de led
void CorrectoLed(){
  digitalWrite(LED,HIGH);
  delay(2  * 1000);
  
  digitalWrite(LED,LOW);
  delay(2 * 1000);

}

void AlertarTemperatura(){

  //----------------------------------------------
   // Leemos la humedad relativa
  float h = dht.readHumidity();
  // Leemos la temperatura en grados centígrados (por defecto)
  float t = dht.readTemperature();
  // Leemos la temperatura en grados Fahreheit
  //float f = dht.readTemperature(true);
  //----------------------------------------------


  
 //Imprimir valores en serial-------------------
  Serial.print("Humedad: ");
  Serial.println(h);
  Serial.println("------------------------------");
  Serial.print("Temperatura: ");
  Serial.print(t);
  Serial.println(" *C ");
  //---------------------------------------------



  //----------------------------------------------
  if(t >= 27){
        digitalWrite(LED,LOW);//encender
    }
    
    if(t < 27){
        digitalWrite(LED,HIGH);//apagar
    }
  //----------------------------------------------
   
  }
  