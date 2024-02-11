# spring-batch-excel-mongodb
Reading and Writing information from an Excel file to MongoDB. 



```curl
curl --location --request POST 'http://localhost:8080/import/launch/csv/job'
```
```curl
curl --location 'http://localhost:8080/import/launch/csv/job2' \
--header 'Content-Type: application/json' \
--data '{
    "name": "data/processing/employee.csv"
}'
```
