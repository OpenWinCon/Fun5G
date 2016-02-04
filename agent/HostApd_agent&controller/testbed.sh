#!/bin/bash

CMD="$1"

for ((i=0;i<256;i++));
do
	bash -c "nohup $CMD $i &"
	sleep 1
done
