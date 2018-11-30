#include<stdio.h>
#include<stdlib.h>
int _function(int EV_n)
{
int EV_i;
int EV_j;
if ((EV_n<=0))
{
return 0;
}
EV_i = 0;
while ((EV_i<(EV_n*EV_n)))
{
EV_j = (EV_i+EV_n);
printf("%d ", EV_j);
EV_i = (EV_i+1);
}
return _function((EV_n-1));
}
int _main()
{
int EV_num;
scanf("%d", &EV_num);
_function(EV_num);
printf("%d\n", 0);
return 0;
}
int main(void)
{
   return _main();
}

