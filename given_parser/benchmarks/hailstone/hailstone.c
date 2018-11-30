#include<stdio.h>
#include<stdlib.h>
int _mod(int EV_a,int EV_b)
{
return (EV_a-((EV_a/EV_b)*EV_b));
}
void _hailstone(int EV_n)
{
while (1)
{
printf("%d ", EV_n);
if ((_mod(EV_n, 2)==1))
{
EV_n = ((3*EV_n)+1);
}
else
{
EV_n = (EV_n/2);
}
if ((EV_n<=1))
{
printf("%d\n", EV_n);
return ;
}
}
}
int _main()
{
int EV_num;
scanf("%d", &EV_num);
_hailstone(EV_num);
return 0;
}
int main(void)
{
   return _main();
}

