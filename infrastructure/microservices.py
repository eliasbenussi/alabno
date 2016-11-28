# Add to this list all the microservices
# Please have them in the correct ORDER of execution
# this assumes that the working directory is the root directory
# of the git repository

microservices = [
    {"name": "compiler", "location": "java -jar backend/compile-checker/target/compile-checker-1.0-SNAPSHOT-jar-with-dependencies.jar"},
    {"name": "linter", "location": "java -jar backend/linter/target/linter-1.0-SNAPSHOT-jar-with-dependencies.jar" },
    {"name": "simplehaskellmarker", "location": "simple-haskell-marker/SimpleHaskellMarker"},
    {"name": "markmarker", "location": "java -cp backend/mark_marker/target/mark_marker-1.0-SNAPSHOT-jar-with-dependencies.jar mark_marker.App"}
]
