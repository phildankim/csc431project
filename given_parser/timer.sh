#!/bin/bash

make clean;make

for dir in benchmarks/*; do
   f=${dir##*/}
   echo $f
   java MiniCompiler $dir/${f}.mini -stack
   clang $dir/${f}.ll -m32
   start=`date +%s.%N`
   ./a.out < $dir/input > ${f}.txt
   end=`date +%s.%N`
   elapsed="$(bc <<<"$end-$start")"
   echo $elapsed

   java MiniCompiler $dir/${f}.mini -llvm
   clang $dir/${f}.ll -m32
   start=`date +%s.%N` 
   ./a.out < $dir/input > ${f}.txt
   end=`date +%s.%N`
   elapsed="$(bc <<<"$end-$start")"
   echo $elapsed

   java MiniCompiler $dir/${f}.mini -llvm -sscp -uce
   clang $dir/${f}.ll -m32
   start=`date +%s.%N`
   ./a.out < $dir/input > ${f}.txt
   end=`date +%s.%N`
   elapsed="$(bc <<<"$end-$start")"
   echo $elapsed

   clang -S -O0 -Wno-everything $dir/${f}.c
   start=`date +%s.%N`
   ./a.out < $dir/input > ${f}.txt
   end=`date +%s.%N`
   elapsed="$(bc <<<"$end-$start")"
   echo $elapsed

   clang -S -O3 -Wno-everything $dir/${f}.c
   start=`date +%s.%N`
   ./a.out < $dir/input > ${f}.txt
   end=`date +%s.%N`
   elapsed="$(bc <<<"$end-$start")"
   echo $elapsed
done


