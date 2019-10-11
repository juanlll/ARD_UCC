#include <SoftwareSerial.h>
void setup() {
  // put your setup code here, to run once:
SoftwareSerial SerialESP8266(10,11);
iniciarWifi();
}

void loop() {
  // put your main code here, to run repeatedly:
 
}


//Crear metodo llamado void iniciarWifi()
void iniciarWifi(){
  SerialESP8266.begin(115200); 
  Serial.begin(9600);
  SerialESP8266.setTimeout(2000);

//Verificamos si el ESP8266 responde
  SerialESP8266.println("AT");
  if(SerialESP8266.find("OK"))
    Serial.println("Respuesta AT correcto");
  else
    Serial.println("Error en ESP8266");

//-----Configuración de red-------//Podemos comentar si el ESP ya está configurado

//ESP8266 en modo estación (nos conectaremos a una red existente)
  SerialESP8266.println("AT+CWMODE=1");
  if(SerialESP8266.find("OK"))
    Serial.println("ESP8266 en modo Estacion");

//Nos conectamos a una red wifi
  SerialESP8266.println("AT+CWJAP=\"Materialize\",\"Mateo28:19\"");
  Serial.println("Conectandose a la red ...");
SerialESP8266.setTimeout(10000); //Aumentar si demora la conexion
if(SerialESP8266.find("OK"))
  Serial.println("WIFI conectado");
else
  Serial.println("Error al conectarse en la red");
SerialESP8266.setTimeout(2000);
//Desabilitamos las conexiones multiples
SerialESP8266.println("AT+CIPMUX=0");
if(SerialESP8266.find("OK"))
  Serial.println("Multiconexiones deshabilitadas");

 //------fin de configuracion-------------------

delay(1000);

} 
void enviarPeticion(){

  lectura = analogRead(sensor); //leemos del sensor MQ7S valores entre 0 1023
  lecturaHumedad = map(analogRead(sensorHumedad), 1023, 0, 0, 100);
  lecturaEnPPM = map(lectura, 0, 1023, 20, 1023);//Convertimos el rango de lectura analógica (0-1023) al rango de lectura en ppm (20 ppm - 2000 pmm) que soporta MQ7 
  humedad = dht.readHumidity();
  temp =dht.readTemperature();

 //---------enviamos las variables al servidor---------------------
  SerialESP8266.println("AT+CIPSTART=\"TCP\",\"" + server + "\",80");

  if( SerialESP8266.find("OK"))
  {  
    Serial.println("ESP8266 conectado con el servidor...");            
 //Armamos el encabezado de la peticion http://apiarduino.herokuapp.com/api/rec?temp=28&humidity=32&co2=32&humididy_ground=
    String peticionHTTP= "POST/api/rec?temp="+String(temp)+"&humidity="+String(humedad)+"&co2="+String(lecturaEnPPM)+" HTTP/1.1\r\n";
    peticionHTTP=peticionHTTP+"Host: apiarduino.herokuapp.com\r\n\r\n"; 
    Serial.println(peticionHTTP);


           //Enviamos el tamaño en caracteres de la peticion http:  
    SerialESP8266.print("AT+CIPSEND=");
    SerialESP8266.println(peticionHTTP.length());

           //esperamos a ">" para enviar la petcion  http
           if(SerialESP8266.find(">")) // ">" indica que podemos enviar la peticion http
           {
            Serial.println("Enviando HTTP . . .");
            SerialESP8266.println(peticionHTTP);
            if( SerialESP8266.find("SEND OK"))
            {  
              Serial.println("Peticion HTTP enviada:");
              Serial.println(peticionHTTP);
              Serial.println("Esperando respuesta...");

              boolean fin_respuesta=false;
              long tiempo_inicio=millis(); 
              cadena="";

              while(fin_respuesta==false)
              {
                while(SerialESP8266.available()>0)
                {
                  char c=SerialESP8266.read();
                  Serial.write(c);
                       cadena.concat(c);  //guardamos la respuesta en el string "cadena"
                   }
                   //finalizamos si la respuesta es mayor a 500 caracteres
                   if(cadena.length()>500) //Pueden aumentar si tenen suficiente espacio en la memoria
                   {
                    Serial.println("La respuesta a excedido el tamaño maximo");

                    SerialESP8266.println("AT+CIPCLOSE");
                    if( SerialESP8266.find("OK"))
                      Serial.println("Conexion finalizada");
                    fin_respuesta=true;
                   }
                  if((millis()-tiempo_inicio)>10000) //Finalizamos si ya han transcurrido 10 seg
                  {
                    Serial.println("Tiempo de espera agotado");
                    SerialESP8266.println("AT+CIPCLOSE");
                    if( SerialESP8266.find("OK"))
                      Serial.println("Conexion finalizada");
                    fin_respuesta=true;
                  }
                   if(cadena.indexOf("CLOSED")>0) //si recibimos un CLOSED significa que ha finalizado la respuesta
                   {
                    Serial.println();
                    Serial.println("Cadena recibida correctamente,conexion finalizada");        
                    fin_respuesta=true;
                   }
               }


           }
           else
           {
            Serial.println("No se ha podido enviar HTTP.....");
           }            
       }
   } 
   else
   {
    Serial.println("No se ha podido conectarse con elservidor"); 
   }  
} 