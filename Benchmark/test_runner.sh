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

for NodesNo in 4 6 8
do
    hosts=""
    for HostNo in $(seq 1 ${NodesNo})
    do
        hosts+="hpc-$HostNo,"
    done
    hosts=${hosts::-1}
    benchmark="java -jar Benchmark.jar -l CsvMinimal -r5 -n${NodesNo} --timeout=2700000 --ssh --ssh-hosts=$hosts --"

    echo
    echo --== Starting ProdCons tests - ${NodesNo} nodes  ==--
    echo Testing prod / cons numbers
    producers=( 1 1 1  5 5 5  5)
    consumers=( 1 5 10 1 5 25 50)
    for i in "${!producers[@]}";
    do
        ProducersNo=${producers[$i]}
        ConsumersNo=${consumers[$i]}
        prodConsParams="-p${ProducersNo} -c${ConsumersNo} --pp=1000"
        printf "${NodesNo},${ProducersNo},${ConsumersNo},1000,1,RoundRobin "
        run ${benchmark} ${prodConsParams} "-q1 --ss=RoundRobin"
        echo
    done

    echo Testing concurrent queue number
    producers=( 5 5 5  5)
    consumers=( 1 5 25 50)
    for i in "${!producers[@]}";
    do
        for QueueNo in 2 5 25 50 100
        do
            ProducersNo=${producers[$i]}
            ConsumersNo=${consumers[$i]}
            printf "${NodesNo},${ProducersNo},${ConsumersNo},1000,${QueueNo},RoundRobin "
            run ${benchmark} "-p${ProducersNo} -c${ConsumersNo} --pp=1000 -q${QueueNo} --ss=RoundRobin"
            echo
        done
    done

    echo Testing queue selection strategy
    producers=( 5 5 )
    consumers=( 5 25 )
    for i in "${!producers[@]}";
    do
        for QueueNo in 5 25 50
        do
            for Strategy in "RoundRobin" "Random"
            do
                ProducersNo=${producers[$i]}
                ConsumersNo=${consumers[$i]}
                printf "${NodesNo},${ProducersNo},${ConsumersNo},1000,${QueueNo},${Strategy} "
                run ${benchmark} "-p${ProducersNo} -c${ConsumersNo} --pp=1000 -q${QueueNo} --ss=${Strategy}"
                echo
            done
        done
    done
done
