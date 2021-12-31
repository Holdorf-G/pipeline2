# pipeline2 AWS

Here are the instruction to run the bach on on AWS.

## Requirements

- linux System terminal
- AWS account
- Configured EC2 (https://docs.aws.amazon.com/fr_fr/AWSEC2/latest/UserGuide/get-set-up-for-amazon-ec2.html)

## Launch EC2

Connect to the AWS console.

Launch an EC2 with with Ubuntu AMI "Ubuntu Server 18.04 LTS (HVM), SSD Volume Type - ami-05760f62e0b3eab56 (64 bits x86) ".

Choose an instance type with 2Go of ram or more.

Choose the security group you have created in the tutorial and launch the instance.

## Connect to the EC2

Launch two terminals from the root of the ripo.

Copy the permition file in the root of the ripo.

To connect :
- In the AWS EC2 console, go to your running instance
-  click on "connect"
-  note the public IP and the username
-  select SSH Client tab
-  copy and execute the command under "Example" in the first terminal.

## Configure the EC2

Once connected, we need to install the required softwares.

update the modules list
```
sudo apt-get update
```

Ìnstall Java
```
sudo apt-get -y install openjdk-11-jdk
```

Ìnstall Scala
```
sudo apt install default-jdk scala git -y
```

Ìnstall Spark
```
sudo curl -L https://downloads.apache.org/spark/spark-3.1.2/spark-3.1.2-bin-hadoop3.2.tgz > spark-3.1.2-bin-hadoop3.2.tgz
sudo tar xvf spark-*
sudo mv spark-3.1.2-bin-hadoop3.2 /opt/spark
sudo echo "export SPARK_HOME=/opt/spark" >> ~/.profile
```

Install SBT
```
sudo apt-get update
sudo apt-get install apt-transport-https curl gnupg -yqq
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | sudo tee /etc/apt/sources.list.d/sbt_old.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | sudo -H gpg --no-default-keyring --keyring gnupg-ring:/etc/apt/trusted.gpg.d/scalasbt-release.gpg --import
sudo chmod 644 /etc/apt/trusted.gpg.d/scalasbt-release.gpg
sudo apt-get update
sudo apt-get install sbt
```

## Get the data to EC2

Prepare the destination folder in the EC2.
```
sudo mkdir ~/data
sudo chmod -R 777 ~/data
```

With the second terminal, upload the files to run the batch (use the username and public IP you noted).
```
scp -r -v -i "my-key-pair.pem" sbt  username@publicIP:~/data/
```
note : https should be open in security group.

## Build and Run the batch

Go to ~/data/pipeline
```
cd ~/data/pipeline
```

build the app
```
sbt package
```

with SSH, start the spark job with :

```
/opt/spark/bin/spark-submit \
  --class "batch" \
  --master local[4] \
  target/scala-2.12/simple-project_2.12-1.0.jar
```

If sucessfull, a folder "results" is created, with the csv final result.

## Get the results back

With the second terminal, download the csv in the result fodler.
```
scp -v -i "my-key-pair.pem"  username@publicIP:~/data/pipeline/output/results/*.csv .
```

## terminate the EC2

You can now use the results.



 
