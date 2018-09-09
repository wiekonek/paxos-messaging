#!/bin/sh

outFile="./benchmark-out.txt"
errFile="./benchmark-err.txt"

run() {
    echo $@ >> ${outFile}
    echo $@ >> ${errFile}
    $@ >>${outFile} 2>>${errFile}
    if [ $? -gt 0 ]
    then
        printf '\u2718'
    else
        printf '\u2714'
    fi
    echo "" >> ${outFile}
    echo "" >> ${errFile}
}

case $1 in
    kill|Kill|k)
    for HostNo in {1..16}
    do
        echo killing hpc-${HostNo} paxos processes
        ssh hpc-${HostNo} 'jps -l | grep paxos | cut -d " " -f1 | xargs kill -9 2> /dev/null'
    done
    exit 0
    ;;

    clean|Clean|c)
    rm ${outFile}
    rm ${errFile}tail
    rm -rf benchmark-results/*
    echo Cleaned
    exit 0
    ;;

    warmup|w)
    testCmd="java -jar Benchmark.jar -l CsvMinimal -r5 -n2 --ssh --ssh-hosts=hpc-1,hpc-2 -- -p1 -c1 --pp=1000 -q1 --ss=RoundRobin"
    echo Runing ${testCmd}
    run ${testCmd}
    echo
    exit 0
    ;;
esac

echo "Start benchmark suite" >> ${outFile}
echo "Start benchmark suite" >> ${errFile}

benchmark="java -jar Benchmark.jar -l CsvMinimal -r5 --timeout=2700000"

echo "Testing concurrent queue number (10000 messages)"
nodes=(     4 4 4  4  4  4  8 8  8 )
producers=( 2 2 2  2  4  4  2 2  4 )
consumers=( 1 4 20 50 40 80 4 20 80 )
for i in "${!producers[@]}";
do
    NodesNo=${nodes[$i]}
    hosts=""
    for HostNo in $(seq 1 ${NodesNo})
    do
        hosts+="hpc-$HostNo,"
    done
    hosts=${hosts::-1}
    sshParm="-n${NodesNo} --ssh --ssh-hosts=$hosts"

    ProducersNo=${producers[$i]}
    ConsumersNo=${consumers[$i]}
    ProductsPerNode=$((10000/(${NodesNo}*${ProducersNo})))
    for QueueNo in 2 5 25 50 100
    do
        printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerNode},${QueueNo},RoundRobin,OneEntry "
        run ${benchmark} ${sshParm} "-- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerNode} -q${QueueNo} --ss=RoundRobin"
        echo
    done
done

echo
echo "Testing queue selection strategy (10000 messages)"
for NodesNo in 4 6 8
do
    hosts=""
    for HostNo in $(seq 1 ${NodesNo})
    do
        hosts+="hpc-$HostNo,"
    done
    hosts=${hosts::-1}
    sshParm="-n${NodesNo} --ssh --ssh-hosts=$hosts"

    producers=( 2 2  4  8)
    consumers=( 4 40 80 80)
    for i in "${!producers[@]}";
    do
        for QueueNo in 5 25 50 100 200
        do
            for Strategy in "RoundRobin" "Random"
            do
                ProducersNo=${producers[$i]}
                ConsumersNo=${consumers[$i]}
                ProductsPerNode=$((10000/(${NodesNo}*${ProducersNo})))
                printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerNode},${QueueNo},${Strategy},OneEntry "
                run ${benchmark} ${sshParm} "-- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerNode} -q${QueueNo} --ss=${Strategy}"
                echo
            done
        done
    done
done

echo
echo "Testing transactional list type (8000 messages)"
for NodesNo in 4 6 8
do
    hosts=""
    for HostNo in $(seq 1 ${NodesNo})
    do
        hosts+="hpc-$HostNo,"
    done
    hosts=${hosts::-1}
    sshParm="-n${NodesNo} --ssh --ssh-hosts=$hosts"

    producers=( 2 2  4  8)
    consumers=( 4 40 80 80)
    for i in "${!producers[@]}";
    do
        for QueueNo in 5 25 50 100 200
        do
            for TListType in "OneEntry" "TwoEntry"
            do
                ProducersNo=${producers[$i]}
                ConsumersNo=${consumers[$i]}
                ProductsPerNode=$((8000/(${NodesNo}*${ProducersNo})))
                printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerNode},${QueueNo},RoundRobin,${TListType} "
                run ${benchmark} ${sshParm} "-- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerNode} -q${QueueNo} --ss=RoundRobin --mlt=${TListType}"
                echo
            done
        done
    done
done

echo
echo "Testing producers / consumers numbers"
for NodesNo in 4 6 8 10
do
    hosts=""
    for HostNo in $(seq 1 ${NodesNo})
    do
        hosts+="hpc-$HostNo,"
    done
    hosts=${hosts::-1}
    sshParm="-n${NodesNo} --ssh --ssh-hosts=$hosts"

    echo "One producer per node, (5000 messages), ${NodesNo} nodes"
    for ConsumersNo in {1..10}
    do
        prodConsParams="-p${ProducersNo} -c${ConsumersNo} --pp=$((5000/${NodesNo}))"
        printf "${NodesNo},1,${ConsumersNo},$((5000/${NodesNo})),1,RoundRobin,OneEntry "
        run ${benchmark} ${sshParm} ${prodConsParams} "-- -q1 --ss=RoundRobin"
        echo
    done

    echo
    echo "Testing bigger const number of messages (8000, 1000) with more producers for ${NodesNo} nodes"
    producers=( 2 2  2  2  4  4  4)
    consumers=( 4 10 16 50 20 40 80)
    for i in "${!producers[@]}";
    do
        ProducersNo=${producers[$i]}
        ConsumersNo=${consumers[$i]}
        ProductsPerNode=$((10000/(${NodesNo}*${ProducersNo})))
        prodConsParams="-- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerNode}"
        printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerNode},1,RoundRobin,OneEntry "
        run ${benchmark} ${sshParm} ${prodConsParams} "-q1 --ss=RoundRobin"
        echo
    done
done

