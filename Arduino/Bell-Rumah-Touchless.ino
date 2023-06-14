#include <Wire.h>
#include <Adafruit_MLX90614.h>
#include <Adafruit_GFX.h>
#include <SPI.h>
#include <Adafruit_SSD1306.h>
#include <FirebaseArduino.h>
#include <ESP8266WiFi.h>
// #include <ESP8266HTTPClient.h>

// Define WiFi
#define WIFI_SSID "YOUR WI-FI SSID/NAME"
#define WIFI_PASSWORD "YOUR WI-FI PASSWORD"

// Define OLED
#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64
#define OLED_RESET -1
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);

// MLX90614
Adafruit_MLX90614 mlx = Adafruit_MLX90614();
double temp_amb;
double temp_obj;
double calibration = 7.36;

// Define Firebase
#define FIREBASE_HOST "FIREBASE HOSTNAME ex. (exampe-default-rtdb.firebaseio.com)"
#define FIREBASE_AUTH "FIREBASE AUTH KEY"

// Sensor Ultrasonik
const int trigPin = 14;
const int echoPin = 12;
long duration;
int distance;

void setup()
{
    pinMode(trigPin, OUTPUT);                  // Sets the trigPin as an Output
    pinMode(echoPin, INPUT);                   // Sets the echoPin as an Input
    Serial.begin(9600);                        // Starts the serial communication
    mlx.begin();                               // Initialize MLX90614
    display.begin(SSD1306_SWITCHCAPVCC, 0x3C); // Initialize OLED with I2C addr 0x3C (128x64)

    display.clearDisplay();
    display.setCursor(25, 15);
    display.setTextSize(1);
    display.setTextColor(WHITE);
    display.println("Bell-Touchless");
    display.setCursor(20, 35);
    display.setTextSize(1);
    display.print("Sedang memulai...");
    display.display();
    delay(1000);

    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    if (WiFi.status() != WL_CONNECTED)
    {
        Firebase.setInt("status_Koneksi", 0);
        Serial.println("Tidak Terkoneksi");
        delay(1000);
    }
    else
    {
        Firebase.setInt("status_Koneksi", 1);
        Serial.println();
        Serial.println("Terkoneksi");
        Serial.print("SSID: ");
        Serial.println(WiFi.SSID());
        Serial.print("IP Address: ");
        Serial.println(WiFi.localIP());
    }

    Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
}

void loop()
{

    temp_obj = mlx.readObjectTempC();
    temp_amb = mlx.readAmbientTempC();

    Serial.print("Object Temperature: ");
    Serial.println(temp_obj);
    Serial.print("Ambient Temperature: ");
    Serial.println(temp_amb);

    display.clearDisplay();
    display.setCursor(25, 0);
    display.setTextSize(1);
    display.setTextColor(WHITE);
    display.println(" Informasi");

    Serial.print("SSID: ");
    Serial.println(WiFi.SSID());
    Serial.print("IP Address: ");
    Serial.println(WiFi.localIP());

    display.setCursor(10, 20);
    display.setTextSize(1);
    display.print("Suhu: ");
    display.print(temp_amb + calibration);
    display.print((char)247);
    display.print("C");
    display.setCursor(10, 30);
    display.setTextSize(1);
    display.print("Jarak: ");
    display.print(distance);
    display.print(" CM");
    display.display();

    double suhuFix = temp_amb + calibration;
    Firebase.setString("hasilOLED", "Suhu: (Nilai suhu)");
    Firebase.setString("hasilOLED1", "Jarak: (Nilai jarak)");

    // Clears the trigPin
    digitalWrite(trigPin, LOW);
    delayMicroseconds(2);

    // Sets the trigPin on HIGH state for 10 micro seconds
    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin, LOW);

    // Reads the echoPin, returns the sound wave travel time in microseconds
    duration = pulseIn(echoPin, HIGH);

    // Calculating the distance
    distance = duration * 0.034 / 2;

    // Prints the distance on the Serial Monitor
    Serial.print("Distance: ");
    Serial.print(distance);
    Serial.println(" CM");
    delay(500);

    Firebase.setInt("hasil_Ultrasonik", distance);

    if (distance < 15)
    {
        display.clearDisplay();
        Firebase.setInt("hasil_Ultrasonik", distance);
        Firebase.setFloat("suhu_Badan", temp_amb + calibration);
        display.setCursor(25, 20);
        display.setTextSize(1);
        display.setTextColor(WHITE);
        display.print("Terimakasih");
        Firebase.setString("hasilOLED", "Terimakasih");
        display.setCursor(20, 30);
        display.setTextSize(1);
        display.print("Mohon tunggu");
        Firebase.setString("hasilOLED1", "Mohon tunggu");
        display.display();
        // Memberikan delay 3 detik
        delay(10000);
    }

    if (Firebase.failed())
    {
        Firebase.setInt("status_Koneksi", 0);
        Serial.print("Send Data Failed");
        Serial.println(Firebase.error());
        delay(500);
        return;
    }
    else
    {
        Serial.println("Send Data Success");
        Firebase.setInt("status_Koneksi", 1);
        delay(500);
    }
}