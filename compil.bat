javac -classpath ./lib/jade.jar -d ./bin src/ants_colony/*.java
java -cp ./lib/jade.jar;bin jade.Boot -agents host:src.ants_colony.Program
