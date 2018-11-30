#include<stdio.h>
#include<stdlib.h>
struct EV_IntHolder
{
int EV_num;
};
int EV_interval;
int EV_end;
int _multBy4xTimes(struct EV_IntHolder * EV_num,int EV_timesLeft)
{
if ((EV_timesLeft<=0))
{
return EV_num->EV_num;
}
EV_num->EV_num = (4*EV_num->EV_num);
_multBy4xTimes(EV_num, (EV_timesLeft-1));
return EV_num->EV_num;
}
void _divideBy8(struct EV_IntHolder * EV_num)
{
EV_num->EV_num = (EV_num->EV_num/2);
EV_num->EV_num = (EV_num->EV_num/2);
EV_num->EV_num = (EV_num->EV_num/2);
}
int _main()
{
int EV_start;
int EV_countOuter;
int EV_countInner;
int EV_calc;
int EV_tempAnswer;
int EV_tempInterval;
struct EV_IntHolder * EV_x;
int EV_uselessVar;
int EV_uselessVar2;
EV_x = (struct EV_IntHolder*)malloc(sizeof(struct EV_IntHolder));
EV_end = 1000000;
scanf("%d", &EV_start);
scanf("%d", &EV_interval);
printf("%d\n", EV_start);
printf("%d\n", EV_interval);
EV_countOuter = 0;
EV_countInner = 0;
EV_calc = 0;
while ((EV_countOuter<50))
{
EV_countInner = 0;
while ((EV_countInner<=EV_end))
{
EV_calc = ((((((((((1*2)*3)*4)*5)*6)*7)*8)*9)*10)*11);
EV_countInner = (EV_countInner+1);
EV_x->EV_num = EV_countInner;
EV_tempAnswer = EV_x->EV_num;
_multBy4xTimes(EV_x, 2);
_divideBy8(EV_x);
EV_tempInterval = (EV_interval-1);
EV_uselessVar = (EV_tempInterval<=0);
if ((EV_tempInterval<=0))
{
EV_tempInterval = 1;
}
EV_countInner = (EV_countInner+EV_tempInterval);
}
EV_countOuter = (EV_countOuter+1);
}
printf("%d\n", EV_countInner);
printf("%d\n", EV_calc);
return 0;
}
int main(void)
{
   return _main();
}

