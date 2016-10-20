# Get all exercises
# cfg would be the path to json config
select * from Assignments;

# Get all the students taking a particular exercise
select studid, cfgid
from Students as s 
JOIN Results as r ON s.studid=r.studid
JOIN Assignments as a ON r.cfgid=a.cfgid
where studid='student1';

# Student id => all courseworks he's subscribed to
select cfgid, milestone, result 
from Results as r 
where r.studid = 'student1';

# Student id, config id and milestone for coursework => all commits
select cmthash, teststatus, path, time 
from Commits as c
WHERE c.cfgid = 'HaskCrypto' AND c.studid='student1' AND c.milestone=1
ORDER BY c.time DESC;