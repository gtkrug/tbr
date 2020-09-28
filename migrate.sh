#for i in *; do
#  echo rsync -rav ../../tbr/$i . --exclude=application.yml
#done
rsync -rav ../../tbr/build.gradle . 
rsync -rav ../../tbr/gradle . 
rsync -rav ../../tbr/gradle.properties .
rsync -rav ../../tbr/gradlew . 
rsync -rav ../../tbr/gradlew.bat . 
rsync -rav ../../tbr/grails-app . --exclude=application.yml
rsync -rav ../../tbr/scripts . 
rsync -rav ../../tbr/src . 
rsync -rav ../../tbr/test-files . 

