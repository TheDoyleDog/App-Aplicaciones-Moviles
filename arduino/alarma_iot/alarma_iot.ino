/*
 * Firmware para Sistema de Alarma IoT
 * Dispositivo: ESP32 (Recomendado) o ESP8266
 * 
 * Componentes:
 * - Sensor Ultrasónico HC-SR04 (Distancia)
 * - Sensor PIR HC-SR501 (Movimiento)
 * - Buzzer Pasivo (Sonido)
 * - LED (Red por defecto, indicativo)
 * 
 * Librerías necesarias (Instalar desde Gestor de Librerías de Arduino):
 * 1. "Firebase ESP32 Client" de Mobizt (si usas ESP32) o "Firebase ESP8266 Client" (si usas ESP8266)
 * 2. "NTPClient" de Fabrice Weinberg (para obtener la hora exacta)
 */

#if defined(ESP32)
  #include <WiFi.h>
  #include <FirebaseESP32.h>
#elif defined(ESP8266)
  #include <ESP8266WiFi.h>
  #include <FirebaseESP8266.h>
#endif

#include <NTPClient.h>
#include <WiFiUdp.h>
#include <time.h>

// --- CONFIGURACIÓN DE RED Y FIREBASE ---
#define WIFI_SSID "DoyleDog"
#define WIFI_PASSWORD "cris02894"

// URL de la base de datos (sin https:// y sin la barra al final)
// Ejemplo: "alarmasensores-ca4b1-default-rtdb.firebaseio.com"
#define FIREBASE_HOST "alarmasensores-ca4b1-default-rtdb.firebaseio.com" 

// Clave secreta de la base de datos (Ver Configuración del proyecto -> Cuentas de servicio -> Secretos de la base de datos)
#define FIREBASE_AUTH "HpFlOugEY0oHot8458bXIXUvwLF0QcAyFrhNYbAb"

// TU UID de Usuario (El mismo que aparece en la App bajo 'users')
// IMPORTANTE: Reemplazar con el UID real, ej: "bbY2Q1i4YUdsY0s0q3yxZTZ03Bl2"
#define USER_UID "bbY2Q1i4YUdsY0s0q3yxZTZ03Bl2" 

// --- PINES DE HARDWARE ---
// Ajustados para ESP8266 (NodeMCU / Wemos D1)
// Nomenclatura GPIO -> Pin Placa (NodeMCU)
const int PIN_PIR = 14;      // GPIO 14 (D5) - Entrada PIR
const int PIN_TRIG = 12;     // GPIO 12 (D6) - Salida Trigger SR04
const int PIN_ECHO = 13;     // GPIO 13 (D7) - Entrada Echo SR04
const int PIN_BUZZER = 4;    // GPIO 4  (D2) - Salida Buzzer
const int PIN_LED = 5;       // GPIO 5  (D1) - Salida LED

// --- OBJETOS GLOBALES ---
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 0); // Usamos UTC (offset 0). La App convierte a hora local.

// --- VARIABLES DE ESTADO ---
bool alarmEnabled = false;
int detectionDistanceConfig = 50; // Distancia en cm para activar (valor por defecto)
String alarmSoundType = "Siren"; // "Siren", "Bell", etc.

// --- BUFFER DE EVENTOS OFFLINE ---
struct BufferedEvent {
  String message;
  float distance;
  unsigned long timestamp; // Timestamp local de cuando ocurrió (milisegundos)
  bool pending;
};

const int BUFFER_SIZE = 20;
BufferedEvent eventBuffer[BUFFER_SIZE];
int bufferHead = 0; // Índice de escritura
int bufferTail = 0; // Índice de lectura
int bufferCount = 0; // Cantidad de elementos en buffer

unsigned long lastTriggerTime = 0;
const long triggerCooldown = 5000; // Esperar 5 seg entre alertas para no saturar

void setup() {
  Serial.begin(115200);

  // Configurar Pines
  pinMode(PIN_PIR, INPUT);
  pinMode(PIN_TRIG, OUTPUT);
  pinMode(PIN_ECHO, INPUT);
  pinMode(PIN_BUZZER, OUTPUT);
  pinMode(PIN_LED, OUTPUT);
  
  digitalWrite(PIN_LED, HIGH); // Apagado inicial (Active Low: HIGH=OFF)
  digitalWrite(PIN_TRIG, LOW);

  // Conectar WiFi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Conectando a WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("Conectado con IP: ");
  Serial.println(WiFi.localIP());

  // Iniciar NTP
  timeClient.begin();

  // Configurar Firebase
  config.host = FIREBASE_HOST;
  config.signer.tokens.legacy_token = FIREBASE_AUTH;
  
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void loop() {
  timeClient.update();
  
  // 1. Leer Configuración y Estado desde Firebase
  Serial.print("Check Firebase... ");
  checkFirebaseState();
  Serial.println("Done.");

  // 1.5 Procesar Buffer Offline (si hay conexión)
  if (Firebase.ready() && bufferCount > 0) {
      processBuffer();
  }

  // 2. Si la alarma está activada, leer sensores
  if (alarmEnabled) {
    Serial.print("Alarm ON. Reading sensors... ");
    // digitalWrite(PIN_LED, HIGH); // REMOVED: No encender fijo, solo al detectar
    
    bool motionDetected = digitalRead(PIN_PIR);
    float distance = readDistance();
    
    Serial.print("PIR: "); Serial.print(motionDetected);
    Serial.print(" | Dist: "); Serial.println(distance);
    
    // Lógica de Disparo (Doble Verificación)
    bool trigger = false;
    String triggerSource = "";
    
    // Solo disparar si AMBOS sensores confirman (Movimiento + Presencia cercana)
    if (motionDetected && (distance > 0 && distance < detectionDistanceConfig)) {
       trigger = true;
       triggerSource = "Movimiento y Proximidad (" + String(distance) + "cm)";
    }

    // Ejecutar Alarma
    if (trigger) {
      if (millis() - lastTriggerTime > triggerCooldown) {
        Serial.println("ALARM TRIGGERED: " + triggerSource);
        
        // a. Sonido (Buzzer Pasivo) + LED
        playAlarmSound();
        
        // b. Enviar a Firebase
        sendAlertToFirebase(triggerSource, distance);
        
        lastTriggerTime = millis();
      }
    }
  } else {
    // Si la alarma NO está activada
    digitalWrite(PIN_LED, HIGH); // Apagado (Active Low: HIGH=OFF)
    noTone(PIN_BUZZER); // Asegurar silencio
  }
  
  delay(200); // Pequeña pausa para estabilidad del loop
}

void checkFirebaseState() {
  String pathBase = "/users/" + String(USER_UID);
  
  // Leer estado (enabled)
  if (Firebase.getBool(fbdo, pathBase + "/alarm_state/enabled")) {
    alarmEnabled = fbdo.boolData();
  }
  
  // Leer configuración de distancia
  if (Firebase.getInt(fbdo, pathBase + "/alarm_config/detectionDistance")) {
    detectionDistanceConfig = fbdo.intData(); 
  }
  
  // Leer tipo de sonido
  if (Firebase.getString(fbdo, pathBase + "/alarm_config/alarmSound")) {
    alarmSoundType = fbdo.stringData();
  }
}

float readDistance() {
  digitalWrite(PIN_TRIG, LOW);
  delayMicroseconds(2);
  digitalWrite(PIN_TRIG, HIGH);
  delayMicroseconds(10);
  digitalWrite(PIN_TRIG, LOW);
  
  // Timeout para evitar bloqueo
  long duration = pulseIn(PIN_ECHO, HIGH, 30000); 
  
  if (duration == 0) return 999.0; // Fuera de rango o error
  
  float distanceCm = duration * 0.034 / 2;
  return distanceCm;
}

void playAlarmSound() {
  digitalWrite(PIN_LED, LOW); // Encender LED (Active Low: LOW=ON)
  
  if (alarmSoundType == "BELL") {
    // TIPO CAMPANA (Ding-Dong)
    for (int i=0; i<3; i++) {
        tone(PIN_BUZZER, 880, 200); // La5
        delay(250);
        tone(PIN_BUZZER, 698, 400); // Fa5
        delay(550);
    }
  } 
  else if (alarmSoundType == "BEEP") {
    // TIPO BEEP (Intermitente rápido)
    for (int i=0; i<8; i++) {
        tone(PIN_BUZZER, 3000, 100);
        delay(100);
        noTone(PIN_BUZZER);
        delay(100);
    }
  } 
  else if (alarmSoundType == "BUZZER") {
    // TIPO ZUMBADOR (Tono grave constante pulsante)
    for (int i=0; i<3; i++) {
        tone(PIN_BUZZER, 150, 500);
        delay(600);
    }
  } 
  else if (alarmSoundType == "MARIO") {
    // TIPO MARIO BROS (Intro Theme)
    // Notas: Mi7, Mi7, 0, Mi7, 0, Do7, Mi7, 0, Sol7, 0, Sol6
    int melody[] = { 2637, 2637, 0, 2637, 0, 2093, 2637, 0, 3136, 0, 1568 };
    int duration[] = { 100, 100, 100, 100, 100, 100, 100, 100, 100, 300, 100 };
    
    for (int i = 0; i < 11; i++) {
        if (melody[i] == 0) noTone(PIN_BUZZER);
        else tone(PIN_BUZZER, melody[i]);
        delay(duration[i]);
    }
    delay(500); // Pausa final
  }
  else {
    // POR DEFECTO: SIRENA POLICIAL ("SIREN" o cualquier otro valor)
    // 2 ciclos de subida y bajada
    for (int i = 0; i < 2; i++) {
        // Subida
        for (int hz = 500; hz < 1500; hz += 20) {
          tone(PIN_BUZZER, hz);
          delay(4);
        }
        // Bajada
        for (int hz = 1500; hz > 500; hz -= 20) {
          tone(PIN_BUZZER, hz);
          delay(4);
        }
    }
  }
  
  noTone(PIN_BUZZER);
  digitalWrite(PIN_LED, HIGH); // Apagar LED (Active Low: HIGH=OFF)
}

void sendAlertToFirebase(String message, float distInfo) {
  // Obtener timestamp actual en milisegundos desde NTP
  unsigned long long timestamp = timeClient.getEpochTime() * 1000ULL; 
  
  String eventId = "evt_" + String((unsigned long)timeClient.getEpochTime());
  String path = "/users/" + String(USER_UID) + "/history/" + eventId;
  
  FirebaseJson json;
  json.set("id", eventId);
  json.set("message", message);
  json.set("timestamp", (double)timestamp);
  
  json.set("sensorType", (message.indexOf("PIR") > 0) ? "MOTION" : "DISTANCE");
  if (distInfo > 0 && distInfo < 900) json.set("distance", distInfo);

  // Enviar
  // Enviar
  if (Firebase.ready() && Firebase.setJSON(fbdo, path, json)) {
    Serial.println("Alerta guardada en Firebase: " + message);
  } else {
    Serial.println("Error enviando: " + fbdo.errorReason());
    // Guardar en buffer local para reintento
    saveToBuffer(message, distInfo);
  }
}

// --- GESTIÓN DE BUFFER OFFLINE ---

void saveToBuffer(String message, float distance) {
  if (bufferCount < BUFFER_SIZE) {
    eventBuffer[bufferHead].message = message;
    eventBuffer[bufferHead].distance = distance;
    eventBuffer[bufferHead].timestamp = millis(); 
    eventBuffer[bufferHead].pending = true;
    
    Serial.println("OFFLINE: Alerta guardada en buffer [" + String(bufferCount+1) + "/" + String(BUFFER_SIZE) + "]");
    
    bufferHead = (bufferHead + 1) % BUFFER_SIZE;
    bufferCount++;
  } else {
    Serial.println("OFFLINE: Buffer LLENO. Alerta descartada (Sobreescribiendo antigua no ideal, simplemente descartamos nueva para proteger memoria).");
    // Estrategia: Descartar la nueva o sobreescribir la vieja. 
    // Por simplicidad y para preservar el historial más antiguo primero, descartamos la nueva.
  }
}

void processBuffer() {
  Serial.println("SYNC: Procesando eventos offline...");
  
  // Procesar solo uno por ciclo para no bloquear el loop principal demasiado tiempo
  if (bufferCount > 0) {
    BufferedEvent evt = eventBuffer[bufferTail];
    
    // Convertir timestamp relativo (millis cuando ocurrió) a timestamp absoluto aproximado
    // Nota: Esto asume que tenemos hora NTP válida AHORA.
    // Si no, se enviará con hora actual de subida.
    
    unsigned long long currentEpoch = timeClient.getEpochTime() * 1000ULL;
    // Diferencia de tiempo desde que ocurrió hasta ahora
    unsigned long timeDiff = millis() - evt.timestamp; 
    
    // Reconstruir timestamp del evento (Aproximado)
    // timestamp_evento = timestamp_actual - tiempo_transcurrido
    unsigned long long eventTimestamp = currentEpoch - timeDiff;
    
    String eventId = "evt_offline_" + String((unsigned long)(eventTimestamp/1000));
    String path = "/users/" + String(USER_UID) + "/history/" + eventId;
    
    FirebaseJson json;
    json.set("id", eventId);
    json.set("message", evt.message + " (Sincronizado)");
    json.set("timestamp", (double)eventTimestamp);
    json.set("sensorType", (evt.message.indexOf("PIR") > 0) ? "MOTION" : "DISTANCE");
    if (evt.distance > 0 && evt.distance < 900) json.set("distance", evt.distance);
    
    if (Firebase.setJSON(fbdo, path, json)) {
      Serial.println("SYNC: Evento offline recuperado y enviado!");
      
      // Avanzar cola
      bufferTail = (bufferTail + 1) % BUFFER_SIZE;
      bufferCount--;
    } else {
      Serial.println("SYNC: Fallo al sincronizar (Reintentaremos luego): " + fbdo.errorReason());
    }
  }
}
