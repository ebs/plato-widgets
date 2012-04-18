#if ARDUINO >18
#include <SPI.h>
#endif
char data[256];

void setup()
{
  Serial.begin(9600);
} 


int messageCounter=0;
int temperature=40;
void loop()
{
 sprintf(data,"#mac#%s-%s-%s-%s-%s-%s#temp#%d#%c%c%c","A","B","C","D","E","F",temperature,'%','\r','\n');
 Serial.print(data);
}