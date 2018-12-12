#!/bin/bash

make clean;make

for dir in benchmarks/*; do
   f=${dir##*/}
   echo $f
   #java MiniCompiler $dir/${f}.mini -stack
   #clang $dir/${f}.ll -m32
   #start=`date +%s.%N`
   #./a.out < $dir/input.longer > ${f}.txt
   #end=`date +%s.%N`
   #diff ${f}.txt $dir/output.longer
   #elapsed="$(bc <<<"$end-$start")"
   #echo 'stack w/o opt: '$elapsed

   #java MiniCompiler $dir/${f}.mini -stack -simp
   #clang $dir/${f}.ll -m32
   #start=`date +%s.%N`
   #./a.out < $dir/input.longer > ${f}.txt
   #end=`date +%s.%N`
   #diff ${f}.txt $dir/output.longer
   #elapsed="$(bc <<<"$end-$start")"
   #echo 'stack w/ opt: '$elapsed

   java MiniCompiler $dir/${f}.mini -llvm
   clang $dir/${f}.ll -m32
   start=`date +%s.%N` 
   ./a.out < $dir/input.longer > ${f}.txt
   end=`date +%s.%N`
   diff ${f}.txt $dir/output.longer
   elapsed="$(bc <<<"$end-$start")"
   echo 'llvm w/o opt: '$elapsed

   java MiniCompiler $dir/${f}.mini -llvm -sscp -uce
   clang $dir/${f}.ll -m32
   start=`date +%s.%N`
   ./a.out < $dir/input.longer > ${f}.txt
   end=`date +%s.%N`
   diff ${f}.txt $dir/output.longer
   elapsed="$(bc <<<"$end-$start")"
   echo 'llvm w/ opt: '$elapsed

   #clang -S -O0 -Wno-everything $dir/${f}.c
   #start=`date +%s.%N`
   #./a.out < $dir/input.longer > ${f}.txt
   #end=`date +%s.%N`
   #elapsed="$(bc <<<"$end-$start")"
   #echo 'clang w/o opt: '$elapsed

   #clang -S -O3 -Wno-everything $dir/${f}.c
   #start=`date +%s.%N`
   #./a.out < $dir/input.longer > ${f}.txt
   #end=`date +%s.%N`
   #elapsed="$(bc <<<"$end-$start")"
   #echo 'clang w/ opt: '$elapsed
done


