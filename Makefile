# make help
# @ surpresses the output of the line
help:
	@make -qp | awk -F':' '/^[a-zA-Z0-9][^$$#\/\t=]*:([^=]|$$)/ {split($$1,A,/ /);for(i in A)print A[i]}'

# Original is in github-flow

# raw to commandline execute
# make -qp | awk -F':' '/^[a-zA-Z0-9][^$#\/\t=]*:([^=]|$)/ {split($1,A,/ /);for(i in A)print A[i]}' 


# Will run clean on neverwinterdp
clean:
	./gradlew clean

# Will compile neverwinterdp
compile:
	./gradlew compileJava

install-skiptests:
	./gradlew clean build install -x test

# Will try to build every component for neverwinterdp
install:
	./gradlew clean build install

# Generate the javadoc in
#       $build/docs/javadoc/
docs:
	./gradlew javadoc
                
gradle-help:
	./gradlew -q tasks

