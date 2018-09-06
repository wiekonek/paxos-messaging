#!/usr/bin/env bash
hosts=""
for HostNo in {01..08}
do
    hosts+="10.10.0.1$HostNo,"
done
hosts=${hosts::-1}

sshParam="--ssh --ssh-hosts=${hosts}"
benchmark="java -jar Benchmark.jar -r20 -l CsvMinimal"
prodConsParams="--producers=5 --producer-products=1000 --consumers=25"


for NodesNo in 8
do
    simpleQueueTest="${benchmark} -n${NodesNo} ${sshParam} -s SimpleQueue -t 1800000"
    printf "NodesNo = ${NodesNo}\n"
    printf "ConcurrentQueue\n"
    for ConcurrentQueue in 1 10 20 40 80 160 200
    do
        ${simpleQueueTest} -o "concurrent_queue_${ConcurrentQueue}" -- ${prodConsParams} --concurrent-queues=${ConcurrentQueue}
        printf "\n\n"
    done

    printf "ConsumerSelectionType\n"
    for ConcurrentQueue in 20 80 160 200
    do
        for SelectionType in "RoundRobin" "Random"
        do
            ${simpleQueueTest} -o "${ConcurrentQueue}_selection_type_${SelectionType}" -- ${prodConsParams} --concurrent-queues=${ConcurrentQueue} --selection-strategy=${SelectionType}
            printf "\n\n"
        done
    done

    printf "\n"
done

echo "Done!!"
