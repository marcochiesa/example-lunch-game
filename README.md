# example-lunch-game
Example Java console application allowing a group of people to settle on a restaurant choice. The players all make a specified number of restaurant suggestion. They can then each veto one of the restaurant choices. Finally, each player can vote (up to 3 times). The final output of the application show the winning restaurant.
## Tools
build/dependency management: [Gradle](https://gradle.org)<br/>
testing: [Junit](http://junit.org/junit4/)<br/>
code style: [Checkstyle](http://checkstyle.sourceforge.net)<br/>
coverage: [JaCoCo](http://www.jacoco.org/jacoco/)<br/>
## Dependecies
* [Java-console-view](https://github.com/nathanielove/Java-Console-View)<br/>
View layer help for command line programs
## Usage
Gradle can bootstrap itself using the wrapper, so the following should work as long as Java is available
1. Play the game (-q to silence gradle runtime console output)

        ./gradlew run -q
    
2. Unit tests and code style

        ./gradlew check
    test report: build/reports/tests/test/index.html<br/>
    code style report: build/reports/checkstyle/main.html
  
3. Code coverage

        ./gradlew check jacocoTestReport
    coverage report: build/reports/jacoco/test/html/index.html

