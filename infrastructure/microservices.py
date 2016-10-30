# Add to this list all the microservices
# Please have them in the correct ORDER of execution
# this assumes that the working directory is the root directory
# of the git repository

microservices = [
    {"name": "linter", "location": "java -jar backend/linter/target/linter-1.0-SNAPSHOT-jar-with-dependencies.jar" },
    {"name": "cleaner", "location": "./infrastructure/cleaner"},
    {"name": "compiler", "location": "java -jar backend/compile-checker/target/compile-checker-1.0-SNAPSHOT-jar-with-dependencies.jar"}
]
