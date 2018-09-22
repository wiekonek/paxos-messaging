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
    rm ${errFile}
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

echo "1. Test growing number of messages (one tlist)"
for NodesNo in 4 8 10
do
    ProducersNo=4
    ConsumersNo=2
    QueueNo=1

    hosts=""
    for HostNo in $(seq 1 ${NodesNo})
    do
        hosts+="hpc-$HostNo,"
    done
    hosts=${hosts::-1}

    sshParam="-n${NodesNo} --ssh --ssh-hosts=$hosts"
    for ProductsNo in {1000..20000..1000}
    do
        ProductsPerNode=$((${ProductsNo}/(${NodesNo}*${ProducersNo})))
        printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerNode},${QueueNo},RoundRobin,OneEntry "
        run ${benchmark} ${sshParam} "-- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerNode} -q${QueueNo} --ss=RoundRobin"
        echo
    done
done

echo "2. Test growing number of transactional list helpers"
NodesNo=10
ProductsNo=150000
ConsumersNo=80

hosts=""
for HostNo in $(seq 1 ${NodesNo})
do
    hosts+="hpc-$HostNo,"
done
hosts=${hosts::-1}

sshParam="-n${NodesNo} --ssh --ssh-hosts=$hosts"
for ProducersNo in 80 120 160
do
    for QueueNo in {50..2500..50}
    do
        ProductsPerProducer=$((${ProductsNo}/(${NodesNo}*${ProducersNo})))
        printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerProducer},${QueueNo},RoundRobin,OneEntry "
        run ${benchmark} ${sshParam} "-- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerProducer} -q${QueueNo} --ss=RoundRobin"
        echo
    done
done

echo "3. Test threads scaling - const tlist number"
NodesNo=10
ProductsNo=200000
QueueNo=2000

hosts=""
for HostNo in $(seq 1 ${NodesNo})
do
    hosts+="hpc-$HostNo,"
done
hosts=${hosts::-1}

sshParam="-n${NodesNo} --ssh --ssh-hosts=$hosts"

for ProducersNo in {5..100..5}
do
    ConsumersNo=$((${ProducersNo}/2))
    ProductsPerProducer=$((${ProductsNo}/(${NodesNo}*${ProducersNo})))
    printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerProducer},${QueueNo},RoundRobin,OneEntry "
    run ${benchmark} ${sshParam} "-- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerProducer} -q${QueueNo} --ss=RoundRobin"
    echo
done

echo "4. Test threads scaling with t list scaling"
NodesNo=10
ProductsNo=200000
QueueNo=2000

hosts=""
for HostNo in $(seq 1 ${NodesNo})
do
    hosts+="hpc-$HostNo,"
done
hosts=${hosts::-1}

sshParam="-n${NodesNo} --ssh --ssh-hosts=$hosts"

for ProducersNo in {5..100..5}
do
    ConsumersNo=$((${ProducersNo}/2))
    QueueNo=$(( (${ConsumersNo}+${ProducersNo})*${NodesNo} ))
    ProductsPerProducer=$((${ProductsNo}/(${NodesNo}*${ProducersNo})))
    printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerProducer},${QueueNo},RoundRobin,OneEntry "
    run ${benchmark} ${sshParam} "-- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerProducer} -q${QueueNo} --ss=RoundRobin"
    echo
done


echo "5. Test nodes scaling"
for NodesNo in {4..12..1}
do
    ProductsNo=200000
    ProducersNo=65
    ConsumersNo=35
    ProductsPerProducer=$((${ProductsNo}/(${NodesNo}*${ProducersNo})))
    QueueNo=$((100*${NodesNo}))

    hosts=""
    for HostNo in $(seq 1 ${NodesNo})
    do
        hosts+="hpc-$HostNo,"
    done
    hosts=${hosts::-1}

    printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerProducer},${QueueNo},RoundRobin,OneEntry "
    run ${benchmark} ${sshParam} "-- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerProducer} -q${QueueNo} --ss=RoundRobin"
    echo
done
