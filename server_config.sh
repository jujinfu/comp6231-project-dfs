
# create server dir for storing data
mkdir /tmp/data

sudo yum install git

sudo yum install java-1.8.0-openjdk
sudo yum install java-1.8.0-openjdk-devel

sudo yum localinstall https://dev.mysql.com/get/mysql80-community-release-el7-1.noarch.rpm
sudo yum install mysql-community-server


sudo systemctl start mysqld
sudo systemctl enable mysqld

sudo grep 'temporary password' /var/log/mysqld.log


sudo mysql_secure_installation

mysql -u root -p