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




echo "1. Test topics growing messages"
NodesNo=4
ProducersNo=1
ConsumersNo=10

hosts=""
for HostNo in $(seq 1 ${NodesNo})
do
    if [ ${HostNo} -eq 4 ]
    then
        hosts+="hpc-12,"
    else
        hosts+="hpc-$HostNo,"
    fi
done
hosts=${hosts::-1}

sshParam="-n${NodesNo} --ssh --ssh-hosts=$hosts"
for ProductsNo in {1000..5000..500}
do
    ProductsPerProducer=$((${ProductsNo}/(${NodesNo}*${ProducersNo})))
    printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerProducer},,,, "
    run ${benchmark} ${sshParam} "-s SimpleTopic -- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerProducer}"
    echo
done

echo "2. Test topics growing consumers"
NodesNo=4
ProducersNo=1
ConsumersNo=10
ProductsNo=4000

hosts=""
for HostNo in $(seq 1 ${NodesNo})
do
    if [ ${HostNo} -eq 4 ]
    then
        hosts+="hpc-12,"
    else
        hosts+="hpc-$HostNo,"
    fi
done
hosts=${hosts::-1}

sshParam="-n${NodesNo} --ssh --ssh-hosts=$hosts"
for ConsumersNo in {10..100..10}
do
    ProductsPerProducer=$((${ProductsNo}/(${NodesNo}*${ProducersNo})))
    printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerProducer},,,, "
    run ${benchmark} ${sshParam} "-s SimpleTopic -- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerProducer}"
    echo
done

echo "2. Topic nodes scaling"
for NodesNo in 2 4 8 10
do
    ProducersNo=1
    ConsumersNo=10
    ProductsNo=4000

    hosts=""
    for HostNo in $(seq 1 ${NodesNo})
    do
        if [ ${HostNo} -eq 4 ]
        then
            hosts+="hpc-12,"
        else
            hosts+="hpc-$HostNo,"
        fi
    done
    hosts=${hosts::-1}

    sshParam="-n${NodesNo} --ssh --ssh-hosts=$hosts"

    ProductsPerNode=$((${ProductsNo}/(${NodesNo}*${ProducersNo})))
    printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerNode},,, "
    run ${benchmark} ${sshParam} "-s SimpleTopic -- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerNode}"
    echo

done
#
#echo "2. Test growing number of transactional list helpers"
#NodesNo=10
#ProductsNo=150000
#ConsumersNo=50
#ProducersNo=50
#
#hosts=""
#for HostNo in $(seq 1 ${NodesNo})
#do
#    if [ ${HostNo} -eq 4 ]
#    then
#        hosts+="hpc-12,"
#    else
#        hosts+="hpc-$HostNo,"
#    fi
#done
#hosts=${hosts::-1}
#
#sshParam="-n${NodesNo} --ssh --ssh-hosts=$hosts"
#for QueueNo in {200..3000..100}
#do
#    ProductsPerProducer=$((${ProductsNo}/(${NodesNo}*${ProducersNo})))
#    printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerProducer},${QueueNo},RoundRobin,OneEntry "
#    run ${benchmark} ${sshParam} "-- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerProducer} -q${QueueNo} --ss=RoundRobin"
#    echo
#done
#
#
#echo "3. Test threads scaling - const tlist number"
#NodesNo=10
#ProductsNo=200000
#QueueNo=1000
#
#hosts=""
#for HostNo in $(seq 1 ${NodesNo})
#do
#    if [ ${HostNo} -eq 4 ]
#    then
#        hosts+="hpc-12,"
#    else
#        hosts+="hpc-$HostNo,"
#    fi
#done
#hosts=${hosts::-1}
#
#sshParam="-n${NodesNo} --ssh --ssh-hosts=$hosts"
#
#for ProducersNo in {10..150..5}
#do
#    ConsumersNo=$((${ProducersNo}))
#    ProductsPerProducer=$((${ProductsNo}/(${NodesNo}*${ProducersNo})))
#    printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerProducer},${QueueNo},RoundRobin,OneEntry "
#    run ${benchmark} ${sshParam} "-- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerProducer} -q${QueueNo} --ss=RoundRobin"
#    echo
#done
#
#
#
#echo "5. Test nodes scaling"
#for NodesNo in {4..11..1}
#do
#    ProductsNo=200000
#    ProducersNo=50
#    ConsumersNo=50
#    ProductsPerProducer=$((${ProductsNo}/(${NodesNo}*${ProducersNo})))
#    QueueNo=$((100*${NodesNo}))
#
#    hosts=""
#    for HostNo in $(seq 1 ${NodesNo})
#    do
#        if [ ${HostNo} -eq 4 ]
#        then
#            hosts+="hpc-12,"
#        else
#            hosts+="hpc-$HostNo,"
#        fi
#    done
#    hosts=${hosts::-1}
#
#    printf "${NodesNo},${ProducersNo},${ConsumersNo},${ProductsPerProducer},${QueueNo},RoundRobin,OneEntry "
#    run ${benchmark} ${sshParam} "-- -p${ProducersNo} -c${ConsumersNo} --pp=${ProductsPerProducer} -q${QueueNo} --ss=RoundRobin"
#    echo
#done
