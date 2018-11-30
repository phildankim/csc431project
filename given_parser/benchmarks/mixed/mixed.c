#include<stdio.h>
#include<stdlib.h>
struct EV_simple
{
int EV_one;
};
struct EV_foo
{
int EV_bar;
int EV_cool;
struct EV_simple * EV_simp;
};
struct EV_foo * EV_globalfoo;
void _tailrecursive(int EV_num)
{
if ((EV_num<=0))
{
return ;
}
_tailrecursive((EV_num-1));
}
int _add(int EV_x,int EV_y)
{
return (EV_x+EV_y);
}
void _domath(int EV_num)
{
struct EV_foo * EV_math1;
struct EV_foo * EV_math2;
int EV_tmp;
EV_math1 = (struct EV_foo*)malloc(sizeof(struct EV_foo));
EV_math1->EV_simp = (struct EV_simple*)malloc(sizeof(struct EV_simple));
EV_math2 = (struct EV_foo*)malloc(sizeof(struct EV_foo));
EV_math2->EV_simp = (struct EV_simple*)malloc(sizeof(struct EV_simple));
EV_math1->EV_bar = EV_num;
EV_math2->EV_bar = 3;
EV_math1->EV_simp->EV_one = EV_math1->EV_bar;
EV_math2->EV_simp->EV_one = EV_math2->EV_bar;
while ((EV_num>0))
{
EV_tmp = (EV_math1->EV_bar*EV_math2->EV_bar);
EV_tmp = ((EV_tmp*EV_math1->EV_simp->EV_one)/EV_math2->EV_bar);
EV_tmp = _add(EV_math2->EV_simp->EV_one, EV_math1->EV_bar);
EV_tmp = (EV_math2->EV_bar-EV_math1->EV_bar);
EV_num = (EV_num-1);
}
free(EV_math1);
free(EV_math2);
}
void _objinstantiation(int EV_num)
{
struct EV_foo * EV_tmp;
while ((EV_num>0))
{
EV_tmp = (struct EV_foo*)malloc(sizeof(struct EV_foo));
free(EV_tmp);
EV_num = (EV_num-1);
}
}
int _ackermann(int EV_m,int EV_n)
{
if ((EV_m==0))
{
return (EV_n+1);
}
if ((EV_n==0))
{
return _ackermann((EV_m-1), 1);
}
else
{
return _ackermann((EV_m-1), _ackermann(EV_m, (EV_n-1)));
}
}
int _main()
{
int EV_a;
int EV_b;
int EV_c;
int EV_d;
int EV_e;
scanf("%d", &EV_a);
scanf("%d", &EV_b);
scanf("%d", &EV_c);
scanf("%d", &EV_d);
scanf("%d", &EV_e);
_tailrecursive(EV_a);
printf("%d\n", EV_a);
_domath(EV_b);
printf("%d\n", EV_b);
_objinstantiation(EV_c);
printf("%d\n", EV_c);
printf("%d\n", _ackermann(EV_d, EV_e));
return 0;
}
int main(void)
{
   return _main();
}

