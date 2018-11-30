#include<stdio.h>
#include<stdlib.h>
int EV_GLOBAL;
int EV_count;
int _fun2(int EV_x,int EV_y)
{
if ((EV_x==0))
{
return EV_y;
}
else
{
return _fun2((EV_x-1), EV_y);
}
}
int _fun1(int EV_x,int EV_y,int EV_z)
{
int EV_retVal;
EV_retVal = ((((5+6)-(EV_x*2))+(4/EV_y))+EV_z);
if ((EV_retVal>EV_y))
{
return _fun2(EV_retVal, EV_x);
}
else
{
if (((5<6)&&(EV_retVal<=EV_y)))
{
return _fun2(EV_retVal, EV_y);
}
}
return EV_retVal;
}
int _main()
{
int EV_i;
EV_i = 0;
scanf("%d", &EV_i);
while ((EV_i<10000))
{
printf("%d\n", _fun1(3, EV_i, 5));
EV_i = (EV_i+1);
}
return 0;
}
int main(void)
{
   return _main();
}

