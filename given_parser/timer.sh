#!/bin/bash

make clean;make

for dir in benchmarks/*; do
   f=${dir##*/}
   echo $f
   java MiniCompiler $dir/${f}.mini -llvm
   clang $dir/${f}.ll -m32
   start=`date +%s`
   ./a.out < $dir/input > ${f}.txt
   end=`date +%s`
   runtime=$((end-start))
   echo $runtime
   diff ${f}.txt $dir/output
   echo 'diffing complete'
done

echo 'benchmarks passed!'
