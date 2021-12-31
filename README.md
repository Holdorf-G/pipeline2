# pipeline2

You can clone the ripo or download the files.

Here are the instruction to run the bach on a local system.

To run the batch on AWS go to AWS folder.

## Requirements

- linux System with root access
- installed Spark(3.1.2)/Scala(2.12.15) (if not, follow https://phoenixnap.com/kb/install-spark-on-ubuntu)
- installed SBT (if not, follow https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html)

## Package the batch

Open a terminal in this repo root.

Go to .\sbt\pipeline
```
cd .\sbt\pipeline
```

Package app with SBT

```
sbt package
```

## Run the batch

Make sure that the env variable $SPARK_HOME point to the spark version 3.1.2 folder.
If not run:
```
export SPARK_HOME="path to spark 3.1.2"
```

Start the spark job with :

```
$SPARK_HOME/bin/spark-submit \
  --class "batch" \
  --master local[4] \
  target/scala-2.12/simple-project_2.12-1.0.jar
```

If sucessfull, a folder "results" is created, with the csv final result.


 
