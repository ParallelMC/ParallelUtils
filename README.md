# ParallelUtils
A utility plugin for play.parallel.ga


# Procedure to Start Development

1. Ensure Git and Java are installed
2. Download BuildTools from https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
3. Run `java -jar BuildTools.jar --rev 1.17`
4. Clone `https://github.com/PaperMC/Paper.git`
5. (Temporary until full release) Switch to the `dev/1.17` branch
6. Run `./gradlew publishToMavenLocal`