filename.properties in folder resources
can set file.path/file.name

file dependency on jmeter's file named **.jtl.

and you can set tp.list to calculate tp, but if you calculate 999,
need set 900,950,990,999

so if you calculate 9999,
need set 9000,9500,9900,9999

App can calculate the denominator

can set array max value from 0, to two.mi by using property two.mi

when you want to generate:
before 
you need install maven and version >= 3.0.2
if use idea(Java Developer Toolkit),  you can use clean package
generate the file named jmeter_tp-1.0-SNAPSHOT.jar like the file in folder test

with run:
before 
need install jdk(version >= 1.8)

with shell command:
java -jar -Xms(N)G -Xmx(N)G jmeter_tp-1.0-SNAPSHOT.jar
examples:
java -jar -Xms2G -Xmx2G jmeter_tp-1.0-SNAPSHOT.jar

window will show the result.

with 20 millions data, cost 17seconds in 32 CPUs machine