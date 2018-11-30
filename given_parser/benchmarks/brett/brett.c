#include<stdio.h>
#include<stdlib.h>
struct EV_thing
{
int EV_i;
int EV_b;
struct EV_thing * EV_s;
};
int EV_gi1;
int EV_gb1;
struct EV_thing * EV_gs1;
int EV_counter;
void _printgroup(int EV_groupnum)
{
printf("%d ", 1);
printf("%d ", 0);
printf("%d ", 1);
printf("%d ", 0);
printf("%d ", 1);
printf("%d ", 0);
printf("%d\n", EV_groupnum);
return ;
}
int _setcounter(int EV_val)
{
EV_counter = EV_val;
return 1;
}
void _takealltypes(int EV_i,int EV_b,struct EV_thing * EV_s)
{
if ((EV_i==3))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
if (EV_b)
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
if (EV_s->EV_b)
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
}
void _tonofargs(int EV_a1,int EV_a2,int EV_a3,int EV_a4,int EV_a5,int EV_a6,int EV_a7,int EV_a8)
{
if ((EV_a5==5))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", EV_a5);
}
if ((EV_a6==6))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", EV_a6);
}
if ((EV_a7==7))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", EV_a7);
}
if ((EV_a8==8))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", EV_a8);
}
}
int _returnint(int EV_ret)
{
return EV_ret;
}
int _returnbool(int EV_ret)
{
return EV_ret;
}
struct EV_thing * _returnstruct(struct EV_thing * EV_ret)
{
return EV_ret;
}
int _main()
{
int EV_b1;
int EV_b2;
int EV_i1;
int EV_i2;
int EV_i3;
struct EV_thing * EV_s1;
struct EV_thing * EV_s2;
EV_counter = 0;
_printgroup(1);
EV_b1 = 0;
EV_b2 = 0;
if ((EV_b1&&EV_b2))
{
printf("%d\n", 0);
}
else
{
printf("%d\n", 1);
}
EV_b1 = 1;
EV_b2 = 0;
if ((EV_b1&&EV_b2))
{
printf("%d\n", 0);
}
else
{
printf("%d\n", 1);
}
EV_b1 = 0;
EV_b2 = 1;
if ((EV_b1&&EV_b2))
{
printf("%d\n", 0);
}
else
{
printf("%d\n", 1);
}
EV_b1 = 1;
EV_b2 = 1;
if ((EV_b1&&EV_b2))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
EV_counter = 0;
_printgroup(2);
EV_b1 = 1;
EV_b2 = 1;
if ((EV_b1||EV_b2))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
EV_b1 = 1;
EV_b2 = 0;
if ((EV_b1||EV_b2))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
EV_b1 = 0;
EV_b2 = 1;
if ((EV_b1||EV_b2))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
EV_b1 = 0;
EV_b2 = 0;
if ((EV_b1||EV_b2))
{
printf("%d\n", 0);
}
else
{
printf("%d\n", 1);
}
_printgroup(3);
if ((42>1))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
if ((42>=1))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
if ((42<1))
{
printf("%d\n", 0);
}
else
{
printf("%d\n", 1);
}
if ((42<=1))
{
printf("%d\n", 0);
}
else
{
printf("%d\n", 1);
}
if ((42==1))
{
printf("%d\n", 0);
}
else
{
printf("%d\n", 1);
}
if ((42!=1))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
if (1)
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
if ((!1))
{
printf("%d\n", 0);
}
else
{
printf("%d\n", 1);
}
if (0)
{
printf("%d\n", 0);
}
else
{
printf("%d\n", 1);
}
if ((!0))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
if ((!0))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
_printgroup(4);
if (((2+3)==5))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", (2+3));
}
if (((2*3)==6))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", (2*3));
}
if (((3-2)==1))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", (3-2));
}
if (((6/3)==2))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", (6/3));
}
if (((-6)<0))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
_printgroup(5);
EV_i1 = 42;
if ((EV_i1==42))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
EV_i1 = 3;
EV_i2 = 2;
EV_i3 = (EV_i1+EV_i2);
if ((EV_i3==5))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
EV_b1 = 1;
if (EV_b1)
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
if ((!EV_b1))
{
printf("%d\n", 0);
}
else
{
printf("%d\n", 1);
}
EV_b1 = 0;
if (EV_b1)
{
printf("%d\n", 0);
}
else
{
printf("%d\n", 1);
}
if ((!EV_b1))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
if (EV_b1)
{
printf("%d\n", 0);
}
else
{
printf("%d\n", 1);
}
_printgroup(6);
EV_i1 = 0;
while ((EV_i1<5))
{
if ((EV_i1>=5))
{
printf("%d\n", 0);
}
EV_i1 = (EV_i1+5);
}
if ((EV_i1==5))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", EV_i1);
}
_printgroup(7);
EV_s1 = (struct EV_thing*)malloc(sizeof(struct EV_thing));
EV_s1->EV_i = 42;
EV_s1->EV_b = 1;
if ((EV_s1->EV_i==42))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", EV_s1->EV_i);
}
if (EV_s1->EV_b)
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
EV_s1->EV_s = (struct EV_thing*)malloc(sizeof(struct EV_thing));
EV_s1->EV_s->EV_i = 13;
EV_s1->EV_s->EV_b = 0;
if ((EV_s1->EV_s->EV_i==13))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", EV_s1->EV_s->EV_i);
}
if ((!EV_s1->EV_s->EV_b))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
if ((EV_s1==EV_s1))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
if ((EV_s1!=EV_s1->EV_s))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
free(EV_s1->EV_s);
free(EV_s1);
_printgroup(8);
EV_gi1 = 7;
if ((EV_gi1==7))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", EV_gi1);
}
EV_gb1 = 1;
if (EV_gb1)
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
EV_gs1 = (struct EV_thing*)malloc(sizeof(struct EV_thing));
EV_gs1->EV_i = 34;
EV_gs1->EV_b = 0;
if ((EV_gs1->EV_i==34))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", EV_gs1->EV_i);
}
if ((!EV_gs1->EV_b))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
EV_gs1->EV_s = (struct EV_thing*)malloc(sizeof(struct EV_thing));
EV_gs1->EV_s->EV_i = 16;
EV_gs1->EV_s->EV_b = 1;
if ((EV_gs1->EV_s->EV_i==16))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", EV_gs1->EV_s->EV_i);
}
if (EV_gs1->EV_s->EV_b)
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
free(EV_gs1->EV_s);
free(EV_gs1);
_printgroup(9);
EV_s1 = (struct EV_thing*)malloc(sizeof(struct EV_thing));
EV_s1->EV_b = 1;
_takealltypes(3, 1, EV_s1);
printf("%d\n", 2);
_tonofargs(1, 2, 3, 4, 5, 6, 7, 8);
printf("%d\n", 3);
EV_i1 = _returnint(3);
if ((EV_i1==3))
{
printf("%d\n", 1);
}
else
{
printf("%d ", 0);
printf("%d\n", EV_i1);
}
EV_b1 = _returnbool(1);
if (EV_b1)
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
EV_s1 = (struct EV_thing*)malloc(sizeof(struct EV_thing));
EV_s2 = _returnstruct(EV_s1);
if ((EV_s1==EV_s2))
{
printf("%d\n", 1);
}
else
{
printf("%d\n", 0);
}
_printgroup(10);
return 0;
}
int main(void)
{
   return _main();
}


