#!/usr/bin/env bash
hosts=""
for HostNo in {01..17}
do
    hosts+="10.10.1$HostNo,"
done
hosts=${hosts::-1}
echo $hosts

sshParam="--ssh --ssh-hosts=${hosts}"
benchmark="java -jar Benchmark.jar -r20 -l CsvMinimal"
prodConsParams="--producers=10 --producer-products=10000 --consumers=50"

for NodesNo in 8 16
do
    simpleQueueTest="${benchmark} -n${NodesNo} ${sshParam} -s SimpleQueue -t 180000"
    printf "NodesNo = ${NodesNo}\n"
    printf "ConcurrentQueue "
    for ConcurrentQueue in 1 40 80 160 480 960
    do
        `${simpleQueueTest} -- ${prodConsParams} --concurrent-queues=${ConcurrentQueue}`
        printf "."
    done

    printf "\nConsumerSelectionType "
    for ConcurrentQueue in 1 160 480
    do
        for SelectionType in "RoundRobin" "Random"
        do
            `${simpleQueueTest} -- ${prodConsParams} --concurrent-queues=${ConcurrentQueue} --selection-strategy=${SelectionType}`
            printf "."
        done
    done

    printf "\n"
done

echo "Done!!"
