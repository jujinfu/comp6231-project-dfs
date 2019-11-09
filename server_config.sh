
# create server dir for storing data
mkdir /tmp/data

sudo yum install git

sudo yum install java-1.8.0-openjdk
sudo yum install java-1.8.0-openjdk-devel

sudo yum install mariadb
sudo yum install mariadb-server

sudo systemctl start mariadb.service
sudo systemctl enable mariadb.service

/usr/bin/mysql_secure_installation

mysql -u root -p