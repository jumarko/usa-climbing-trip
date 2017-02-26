# Deploys usa-google-maps application to the remote server.
# Please, stop the application before deployment.
# Once deployment is complete, start application again.
# The application can be start as follows: java -jar usa-google-maps.jar
# Finally, visit 188.166.163.64:3000/index.html
set -e;
mkdir -p deploy;
cd deploy;
git clone git@github.com:jumarko/usa-climbing-trip.git;
cd usa-climbing-trip/usa-google-maps;
lein do clean, uberjar;
rsync -avzp target/usa-google-maps.jar root@188.166.163.64:usa-climbing-trip/
