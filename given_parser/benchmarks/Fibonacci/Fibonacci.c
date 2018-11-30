#include<stdio.h>
#include<stdlib.h>
int _computeFib(int EV_input)
{
if ((EV_input==0))
{
return 0;
}
else
{
if ((EV_input<=2))
{
return 1;
}
else
{
return (_computeFib((EV_input-1))+_computeFib((EV_input-2)));
}
}
}
int _main()
{
int EV_input;
scanf("%d", &EV_input);
printf("%d\n", _computeFib(EV_input));
return 0;
}
int main(void)
{
   return _main();
}

