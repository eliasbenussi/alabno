# Add to this list all the microservices
# Please have them in the correct ORDER of execution
# this assumes that the working directory is the root directory
# of the git repository

microservices = [
    {"name": "linter", "location": "java -jar /var/linter"},
    {"name": "cleaner", "location": "./infrastructure/cleaner"}
]