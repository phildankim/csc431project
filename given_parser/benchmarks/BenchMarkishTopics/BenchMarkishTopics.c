#include<stdio.h>
#include<stdlib.h>
struct EV_intList
{
int EV_data;
struct EV_intList * EV_rest;
};
int EV_intList;
int _length(struct EV_intList * EV_list)
{
if ((EV_list==NULL))
{
return 0;
}
return (1+_length(EV_list->EV_rest));
}
struct EV_intList * _addToFront(struct EV_intList * EV_list,int EV_element)
{
struct EV_intList * EV_front;
if ((EV_list==NULL))
{
EV_list = (struct EV_intList*)malloc(sizeof(struct EV_intList));
EV_list->EV_data = EV_element;
EV_list->EV_rest = NULL;
return EV_list;
}
EV_front = (struct EV_intList*)malloc(sizeof(struct EV_intList));
EV_front->EV_data = EV_element;
EV_front->EV_rest = EV_list;
return EV_front;
}
struct EV_intList * _deleteFirst(struct EV_intList * EV_list)
{
struct EV_intList * EV_first;
if ((EV_list==NULL))
{
return NULL;
}
EV_first = EV_list;
EV_list = EV_list->EV_rest;
free(EV_first);
return EV_list;
}
int _main()
{
struct EV_intList * EV_list;
int EV_sum;
scanf("%d", &EV_intList);
EV_sum = 0;
EV_list = NULL;
while ((EV_intList>0))
{
EV_list = _addToFront(EV_list, EV_intList);
printf("%d ", EV_list->EV_data);
EV_intList = (EV_intList-1);
}
printf("%d ", _length(EV_list));
while ((_length(EV_list)>0))
{
EV_sum = (EV_sum+EV_list->EV_data);
printf("%d ", _length(EV_list));
EV_list = _deleteFirst(EV_list);
}
printf("%d\n", EV_sum);
return 0;
}
int main(void)
{
   return _main();
}

