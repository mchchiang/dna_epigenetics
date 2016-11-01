#!/bin/bash

run=$1
file="loop_${run}.dat"

> $file

for (( i=0; i<10000; i++ ))
do
    echo "$i" >> $file
done